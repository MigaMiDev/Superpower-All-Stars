package ttv.migami.spas.entity.fruit.fire;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.entity.CustomProjectileEntity;
import ttv.migami.spas.init.ModEffects;
import ttv.migami.spas.init.ModEntities;
import ttv.migami.spas.init.ModSounds;

import java.util.List;
import java.util.UUID;

import static ttv.migami.spas.entity.fx.ScorchMarkEntity.summonScorchMark;
import static ttv.migami.spas.world.CraterCreator.createCrater;

public class Fireball extends CustomProjectileEntity {
    private int warmupDelayTicks;
    public int life = 100;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    public float damage = 3;
    public boolean affectedByGravity;
    public boolean explosive;

    public Fireball(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Fireball(Level pLevel, Vec3 pPos, Vec3 targetPos, float damage) {
        super(ModEntities.FIREBALL.get(), pLevel);
        this.setPos(pPos.add(0, 1, 0));
        this.setPos(pPos);

        this.affectedByGravity = false;

        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();
        this.affectedByGravity = false;

        this.damage = damage;
        this.speed = 3.5D;

        this.setDeltaMovement(this.getLookAngle().x * speed, this.getLookAngle().y * speed, this.getLookAngle().z * speed);
        this.updateHeading();
    }

    public Fireball(Level pLevel, Vec3 pPos, Vec3 targetPos, boolean explosive, float damage, LivingEntity owner) {
        super(ModEntities.FIREBALL.get(), pLevel, owner);
        this.setPos(pPos);

        this.affectedByGravity = false;
        this.explosive = explosive;

        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();
        this.affectedByGravity = false;

        this.damage = damage;
        this.speed = 3.5D;

        this.setDeltaMovement(this.getLookAngle().x * speed, this.getLookAngle().y * speed, this.getLookAngle().z * speed);
        this.updateHeading();
        this.level().playSound(this, this.blockPosition(), ModSounds.BONE_THROW.get(), SoundSource.PLAYERS, 1F, 1F);
    }

    @Override
    public void tick()
    {
        super.tick();
        if(this.affectedByGravity)
        {
            this.setDeltaMovement(this.getDeltaMovement().add(0, this.modifiedGravity, 0));
        }
    }

    @Override
    protected void onProjectileTick()
    {
        if(this.level().isClientSide && this.tickCount < this.life) {
            if (this.tickCount > 2)
            {
                this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, this.getX(), this.getY(), this.getZ(), 0, 0.1, 0);
            }
        }
    }

    @Override
    public float calculateDamage() {
        this.customDamage = this.damage;
        if (this.getOwner() instanceof Player) {
            Player owner = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(owner, this.damage);
        }
        return this.customDamage;
    }

    @Override
    protected void onHitEntity(Entity entity)
    {
        if (entity instanceof Display.BlockDisplay) {
            return;
        }
        int explosionRadius = 1;
        double radius = 2;
        Level pLevel = this.level();

        if (this.explosive) {
            explosionRadius = 2;
            radius = 3;
            createCrater(pLevel, this.getOnPos(), 3);
            summonScorchMark(this.level(), this.getOnPos().above(), 200, 4);
        }

        CustomProjectileEntity.createExplosion(this, explosionRadius, true);

        AABB areaOfEffect = new AABB(this.getX() - radius, this.getY() - radius /2 , this.getZ() - radius, this.getX() + radius, this.getY() + radius / 2, this.getZ() + radius);
        List<Entity> entitiesArea = pLevel.getEntities(this, areaOfEffect);
        for (Entity entityAttack : entitiesArea) {
            if (entityAttack instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                    livingEntity.setSecondsOnFire(6);
                }
                livingEntity.setSecondsOnFire(2);
            }
        }


        if (entity != this.owner && this.tickCount > 2) {
            if (entity instanceof LivingEntity pTarget) {
                if (pTarget.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                    pTarget.setSecondsOnFire(6);
                }
                pTarget.setSecondsOnFire(2);
            }

            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                if (this.explosive) {
                    serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 10, explosionRadius, explosionRadius, explosionRadius, 0.2);
                    //serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_EXPLOSION.get(), SoundSource.PLAYERS, 8F, 1F);
                }
                serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1, this.getZ(), 5, 0.2, 0.0, 0.3, 0.2);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {
        int explosionRadius = 1;
        double radius = 2;
        Level pLevel = this.level();

        if (this.explosive) {
            explosionRadius = 2;
            radius = 3;
            createCrater(pLevel, this.getOnPos(), 3);
            summonScorchMark(this.level(), this.getOnPos().above(), 200, 4);
        }

        CustomProjectileEntity.createExplosion(this, explosionRadius, true);

        AABB areaOfEffect = new AABB(this.getX() - radius, this.getY() - radius /2 , this.getZ() - radius, this.getX() + radius, this.getY() + radius / 2, this.getZ() + radius);
        List<Entity> entitiesArea = pLevel.getEntities(this, areaOfEffect);
        for (Entity entity : entitiesArea) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                    livingEntity.setSecondsOnFire(6);
                }
                livingEntity.setSecondsOnFire(2);
            }
        }


        if (!this.level().isClientSide) {
            BlockPos entityPos = this.blockPosition();
            BlockPos belowPos = entityPos.below();

            ServerLevel serverLevel = (ServerLevel) this.level();

            if (this.explosive) {
                serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 10, explosionRadius, explosionRadius, explosionRadius, 0.2);
                //serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_EXPLOSION.get(), SoundSource.PLAYERS, 2F, 1F);
            }

            serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), belowPos.getY() + 1, this.getZ(), 5, 0.2, 0.0, 0.3, 0.2);
            //serverLevel.playSound(null, this.getOnPos(), ModSounds.SQUID_SPLAT.get(), SoundSource.PLAYERS, 2F, 1F);

            this.remove(RemovalReason.KILLED);
        }
    }
}
