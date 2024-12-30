package ttv.migami.mdf.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.event.FruitProjectileHitEvent;
import ttv.migami.mdf.interfaces.IExplosionDamageable;
import ttv.migami.mdf.world.ProjectileExplosion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class CustomProjectileEntityOld extends Entity implements TraceableEntity {
    private static final Predicate<Entity> PROJECTILE_TARGETS = input -> input != null && input.isPickable() && !input.isSpectator();

    public int ownerID;
    public LivingEntity owner;
    public boolean collateral = false;
    public boolean affectedByGravity = false;
    public double modifiedGravity = -0.04;
    public int life = 100;
    public float damage = 5.0F;
    public float customDamage = damage;
    public boolean checkForCollisions = false;

    public CustomProjectileEntityOld(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CustomProjectileEntityOld(EntityType<?> pEntityType, Level pLevel, int ownerID, LivingEntity owner) {
        super(pEntityType, pLevel);
        this.ownerID = owner.getId();
        this.owner = owner;
    }

    @Override
    public void tick()
    {
        super.tick();

        calculateDamage();

        this.updateHeading();
        this.onProjectileTick();

        double nextPosX = this.getX() + this.getDeltaMovement().x();
        double nextPosY = this.getY() + this.getDeltaMovement().y();
        double nextPosZ = this.getZ() + this.getDeltaMovement().z();
        this.setPos(nextPosX, nextPosY, nextPosZ);

        Vec3 startVec = this.position();
        Vec3 endVec = startVec.add(this.getDeltaMovement());
        HitResult result = rayTraceBlocks(this.level(), new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if(result.getType() != HitResult.Type.MISS)
        {
            endVec = result.getLocation();
        }

        List<EntityResult> hitEntities = null;
        if(!this.collateral)
        {
            EntityResult entityResult = this.findEntityOnPath(startVec, endVec);
            if(entityResult != null)
            {
                hitEntities = Collections.singletonList(entityResult);
            }
        }
        else
        {
            hitEntities = this.findEntitiesOnPath(startVec, endVec);
        }

        if(hitEntities != null && hitEntities.size() > 0)
        {
            for(EntityResult entityResult : hitEntities)
            {
                result = new EntityHitResult(entityResult.getEntity());
                if(((EntityHitResult) result).getEntity() instanceof Player)
                {
                    Player player = (Player) ((EntityHitResult) result).getEntity();

                    if(this.owner instanceof Player && !((Player) this.owner).canHarmPlayer(player))
                    {
                        result = null;
                    }
                }
                if(result != null)
                {
                    this.onHit(result, startVec, endVec);
                }
            }
        }
        else
        {
            this.onHit(result, startVec, endVec);
        }

        if (this.checkForCollisions) {
            AABB boundingBox = this.getBoundingBox();
            List<Entity> nearbyEntities = this.level().getEntities(this, boundingBox);

            for (Entity entity : nearbyEntities) {
                if (entity != this && entity != this.getOwner() && this.getBoundingBox().intersects(entity.getBoundingBox())) {
                    if (entity instanceof Display.BlockDisplay) {
                        return;
                    }
                    this.onHitEntity(entity, entity.position(), startVec, endVec);

                    if (!this.collateral) {
                        this.remove(RemovalReason.KILLED);
                    }

                    entity.invulnerableTime = 0;
                }
            }
        }

        /*if(this.affectedByGravity)
        {
            this.setDeltaMovement(this.getDeltaMovement().add(0, this.modifiedGravity, 0));
        }*/

        if(this.tickCount >= this.life)
        {
            if(this.isAlive())
            {
                this.onExpired();
            }
            this.remove(RemovalReason.KILLED);
        }
    }

    public float calculateDamage() {
        this.customDamage = this.damage;
        if (this.getOwner() instanceof Player) {
            Player owner = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(owner, this.damage);
        }
        return this.customDamage;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private EntityResult getHitResult(Entity entity, Vec3 startVec, Vec3 endVec)
    {
        double expandHeight = entity instanceof Player && !entity.isCrouching() ? 0.0625 : 0.0;
        AABB boundingBox = entity.getBoundingBox();
        boundingBox = boundingBox.expandTowards(0, expandHeight, 0);

        Vec3 hitPos = boundingBox.clip(startVec, endVec).orElse(null);
        if(hitPos == null)
        {
            return null;
        }

        return new EntityResult(entity, hitPos);
    }

    protected void onHit(HitResult result, Vec3 startVec, Vec3 endVec)
    {
        if(MinecraftForge.EVENT_BUS.post(new FruitProjectileHitEvent(result, this)))
        {
            return;
        }

        if(result instanceof BlockHitResult blockHitResult)
        {
            if(blockHitResult.getType() == HitResult.Type.MISS)
            {
                return;
            }

            Vec3 hitVec = result.getLocation();
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState state = this.level().getBlockState(pos);
            Block block = state.getBlock();

            this.onHitBlock(state, pos, blockHitResult.getDirection(), hitVec.x, hitVec.y, hitVec.z);

        }

        if(result instanceof EntityHitResult entityHitResult)
        {
            Entity entity = entityHitResult.getEntity();
            if(entity.getId() == this.ownerID)
            {
                return;
            }
            if (entity instanceof Display.BlockDisplay) {
                return;
            }

            if(this.owner instanceof Player player)
            {
                if(entity.hasIndirectPassenger(player))
                {
                    return;
                }
            }

            this.onHitEntity(entity, result.getLocation(), startVec, endVec);

            if (!this.collateral) {
                this.remove(RemovalReason.KILLED);
            }

            entity.invulnerableTime = 0;
        }
    }

    public void updateHeading()
    {
        double horizontalDistance = this.getDeltaMovement().horizontalDistance();
        this.setYRot((float) (Mth.atan2(this.getDeltaMovement().x(), this.getDeltaMovement().z()) * (180D / Math.PI)));
        this.setXRot((float) (Mth.atan2(this.getDeltaMovement().y(), horizontalDistance) * (180D / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    /**
     * A simple method to perform logic on each tick of the projectile. This method is appropriate
     * for spawning particles. Override {@link #tick()} to make changes to physics
     */
    protected void onProjectileTick()
    {
    }

    /**
     * Will spawn a particle just before being deleted!
     */
    protected void impactEffect()
    {
    }

    /**
     * Called when the projectile has run out of it's life. In other words, the projectile managed
     * to not hit any blocks and instead aged. The grenade uses this to explode in the air.
     */
    protected void onExpired()
    {
    }

    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec)
    {
    }

    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {

    }

    @Override
    public void onRemovedFromWorld()
    {
        this.impactEffect();
    }

    /**
     * Creates a projectile explosion for the specified entity.
     *
     * @param entity The entity to explode
     * @param radius The amount of radius the entity should deal
     * @param forceNone If true, forces the explosion mode to be NONE instead of config value
     */
    public static void createExplosion(Entity entity, float radius, boolean forceNone)
    {
        Level world = entity.level();
        if(world.isClientSide())
            return;

        DamageSource source = entity instanceof CustomProjectileEntityOld projectile ? entity.damageSources().explosion(entity, projectile.getOwner()) : null;
        Explosion.BlockInteraction mode = forceNone ? Explosion.BlockInteraction.KEEP : Explosion.BlockInteraction.DESTROY;
        Explosion explosion = new ProjectileExplosion(world, entity, source, null, entity.getX(), entity.getY(), entity.getZ(), radius, false, mode);

        if(net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion))
            return;

        // Do explosion logic
        explosion.explode();
        explosion.finalizeExplosion(true);

        // Send event to blocks that are exploded (none if mode is none)
        explosion.getToBlow().forEach(pos ->
        {
            if(world.getBlockState(pos).getBlock() instanceof IExplosionDamageable)
            {
                ((IExplosionDamageable) world.getBlockState(pos).getBlock()).onProjectileExploded(world, world.getBlockState(pos), pos, entity);
            }
        });

        // Clears the affected blocks if mode is none
        if(!explosion.interactsWithBlocks())
        {
            explosion.clearToBlow();
        }

        for(ServerPlayer player : ((ServerLevel) world).players())
        {
            if(player.distanceToSqr(entity.getX(), entity.getY(), entity.getZ()) < 4096)
            {
                player.connection.send(new ClientboundExplodePacket(entity.getX(), entity.getY(), entity.getZ(), radius, explosion.getToBlow(), explosion.getHitPlayers().get(player)));
            }
        }
    }

    public static void createFireExplosion(Entity entity, float radius, boolean forceNone)
    {
        Level world = entity.level();
        if(world.isClientSide())
            return;

        DamageSource source = entity instanceof CustomProjectileEntityOld projectile ? entity.damageSources().explosion(entity, projectile.getOwner()) : null;
        Explosion.BlockInteraction mode = Explosion.BlockInteraction.KEEP;
        Explosion explosion = new ProjectileExplosion(world, entity, source, null, entity.getX(), entity.getY(), entity.getZ(), radius, true, mode);

        if(net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion))
            return;

        // Do explosion logic
        explosion.explode();
        explosion.finalizeExplosion(true);

    }

    @Nullable
    protected EntityResult findEntityOnPath(Vec3 startVec, Vec3 endVec)
    {
        Vec3 hitVec = null;
        Entity hitEntity = null;
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), PROJECTILE_TARGETS);
        double closestDistance = Double.MAX_VALUE;
        for(Entity entity : entities)
        {
            if(!entity.equals(this.owner))
            {
                EntityResult result = this.getHitResult(entity, startVec, endVec);
                if(result == null)
                    continue;
                Vec3 hitPos = result.getHitPos();
                double distanceToHit = startVec.distanceTo(hitPos);
                if(distanceToHit < closestDistance)
                {
                    hitVec = hitPos;
                    hitEntity = entity;
                    closestDistance = distanceToHit;
                }
            }
        }
        return hitEntity != null ? new EntityResult(hitEntity, hitVec) : null;
    }

    @Nullable
    protected List<EntityResult> findEntitiesOnPath(Vec3 startVec, Vec3 endVec)
    {
        List<EntityResult> hitEntities = new ArrayList<>();
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), PROJECTILE_TARGETS);
        for(Entity entity : entities)
        {
            if(!entity.equals(this.owner))
            {
                EntityResult result = this.getHitResult(entity, startVec, endVec);
                if(result == null)
                    continue;
                hitEntities.add(result);
            }
        }
        return hitEntities;
    }

    /**
     * A custom implementation of ray tracing that allows you to pass a predicate to ignore certain
     * blocks when checking for collisions.
     *
     * @param world     the world to perform the ray trace
     * @param context   the ray trace context
     * @return a result of the raytrace
     */
    public static BlockHitResult rayTraceBlocks(Level world, ClipContext context)
    {
        return performRayTrace(context, (rayTraceContext, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            FluidState fluidState = world.getFluidState(blockPos);
            Vec3 startVec = rayTraceContext.getFrom();
            Vec3 endVec = rayTraceContext.getTo();
            VoxelShape blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos);
            BlockHitResult blockResult = world.clipWithInteractionOverride(startVec, endVec, blockPos, blockShape, blockState);
            VoxelShape fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos);
            BlockHitResult fluidResult = fluidShape.clip(startVec, endVec, blockPos);
            double blockDistance = blockResult == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(blockResult.getLocation());
            double fluidDistance = fluidResult == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(fluidResult.getLocation());
            return blockDistance <= fluidDistance ? blockResult : fluidResult;
        }, (rayTraceContext) -> {
            Vec3 Vector3d = rayTraceContext.getFrom().subtract(rayTraceContext.getTo());
            return BlockHitResult.miss(rayTraceContext.getTo(), Direction.getNearest(Vector3d.x, Vector3d.y, Vector3d.z), BlockPos.containing(rayTraceContext.getTo()));
        });
    }

    public static <T> T performRayTrace(ClipContext context, BiFunction<ClipContext, BlockPos, T> hitFunction, Function<ClipContext, T> p_217300_2_)
    {
        Vec3 startVec = context.getFrom();
        Vec3 endVec = context.getTo();
        if(startVec.equals(endVec))
        {
            return p_217300_2_.apply(context);
        }
        else
        {
            double startX = Mth.lerp(-0.0000001, endVec.x, startVec.x);
            double startY = Mth.lerp(-0.0000001, endVec.y, startVec.y);
            double startZ = Mth.lerp(-0.0000001, endVec.z, startVec.z);
            double endX = Mth.lerp(-0.0000001, startVec.x, endVec.x);
            double endY = Mth.lerp(-0.0000001, startVec.y, endVec.y);
            double endZ = Mth.lerp(-0.0000001, startVec.z, endVec.z);
            int blockX = Mth.floor(endX);
            int blockY = Mth.floor(endY);
            int blockZ = Mth.floor(endZ);
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(blockX, blockY, blockZ);
            T t = hitFunction.apply(context, mutablePos);
            if(t != null)
            {
                return t;
            }

            double deltaX = startX - endX;
            double deltaY = startY - endY;
            double deltaZ = startZ - endZ;
            int signX = Mth.sign(deltaX);
            int signY = Mth.sign(deltaY);
            int signZ = Mth.sign(deltaZ);
            double d9 = signX == 0 ? Double.MAX_VALUE : (double) signX / deltaX;
            double d10 = signY == 0 ? Double.MAX_VALUE : (double) signY / deltaY;
            double d11 = signZ == 0 ? Double.MAX_VALUE : (double) signZ / deltaZ;
            double d12 = d9 * (signX > 0 ? 1.0D - Mth.frac(endX) : Mth.frac(endX));
            double d13 = d10 * (signY > 0 ? 1.0D - Mth.frac(endY) : Mth.frac(endY));
            double d14 = d11 * (signZ > 0 ? 1.0D - Mth.frac(endZ) : Mth.frac(endZ));

            while(d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D)
            {
                if(d12 < d13)
                {
                    if(d12 < d14)
                    {
                        blockX += signX;
                        d12 += d9;
                    }
                    else
                    {
                        blockZ += signZ;
                        d14 += d11;
                    }
                }
                else if(d13 < d14)
                {
                    blockY += signY;
                    d13 += d10;
                }
                else
                {
                    blockZ += signZ;
                    d14 += d11;
                }

                T t1 = hitFunction.apply(context, mutablePos.set(blockX, blockY, blockZ));
                if(t1 != null)
                {
                    return t1;
                }
            }

            return p_217300_2_.apply(context);
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Nullable
    @Override
    public Entity getOwner() {
        return this.owner;
    }

    /**
     * Author: MrCrayfish
     */
    public static class EntityResult
    {
        private final Entity entity;
        private final Vec3 hitVec;

        public EntityResult(Entity entity, Vec3 hitVec)
        {
            this.entity = entity;
            this.hitVec = hitVec;
        }

        /**
         * Gets the entity that was hit by the projectile
         */
        public Entity getEntity()
        {
            return this.entity;
        }

        /**
         * Gets the position the projectile hit
         */
        public Vec3 getHitPos()
        {
            return this.hitVec;
        }
    }
}
