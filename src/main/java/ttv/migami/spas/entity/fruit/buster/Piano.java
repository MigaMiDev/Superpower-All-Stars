package ttv.migami.spas.entity.fruit.buster;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.init.ModEntities;
import ttv.migami.spas.init.ModSounds;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ttv.migami.spas.common.network.ServerPlayHandler.calculateCustomDamage;

public class Piano extends Entity {
    public int life = 188;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    public float damage = 4;
    public float customDamage = damage;
    public Vec3 fallPos = this.position();
    public double modifiedGravity = -0.05;
    public boolean falling = true;
    private Set<Entity> damagedEntities = new HashSet<>();

    public Piano(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Piano(Player owner, Level pLevel, BlockPos blockPos) {
        super(ModEntities.PIANO.get(), pLevel);
        this.owner = owner;
        this.setOwner(owner);

        this.fallPos = blockPos.getCenter();
        this.setPos(blockPos.getCenter().add(0, 10, 0));
        this.lookAt(EntityAnchorArgument.Anchor.EYES, owner.getEyePosition());
        this.setDeltaMovement(0, -0.5 ,0);

        if (this.getOwner() instanceof Player) {
            Player ownerAttack = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(ownerAttack, this.damage);
        }
    }

    public Piano(Player owner, Level pLevel, Vec3 targetPos) {
        super(ModEntities.PIANO.get(), pLevel);
        this.owner = owner;
        this.setOwner(owner);

        this.fallPos = targetPos;
        this.setPos(targetPos.add(0, 10, 0));
        this.lookAt(EntityAnchorArgument.Anchor.EYES, owner.getEyePosition());
        this.setDeltaMovement(0, -0.5,0);

        if (this.getOwner() instanceof Player) {
            Player ownerAttack = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(ownerAttack, this.damage);
        }
    }

    private void teleportToGroundOrAir() {
        BlockPos currentPos = this.blockPosition();
        Level level = this.level();

        while (currentPos.getY() > level.getMinBuildHeight() && level.getBlockState(currentPos.below()).isAir()) {
            currentPos = currentPos.below();
        }

        while (!level.getBlockState(currentPos).isAir() && currentPos.getY() < level.getMaxBuildHeight()) {
            currentPos = currentPos.above();
        }

        this.setPos(currentPos.getX() + 0.5, currentPos.getY() + 1.0, currentPos.getZ() + 0.5);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        Vec3 nextPos = this.position().add(this.getDeltaMovement());

        BlockPos posBelow = new BlockPos((int) nextPos.x(), (int) (nextPos.y() - 1), (int) nextPos.z());
        if (!level.isEmptyBlock(posBelow) && this.falling) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPos(posBelow.getX(), posBelow.getY() + 1, posBelow.getZ());
            //this.teleportToGroundOrAir();
            this.falling = false;

            AABB hitbox = this.getBoundingBox();
            List<Entity> entities = this.level().getEntities(this, hitbox);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity && !hasBeenDamaged(entity) && owner != null) {
                    entity.hurt(this.damageSources().fallingBlock(entity), customDamage);
                    this.markAsDamaged(entity);
                }
            }

            if (!level.isClientSide)
            {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.playSound(null, this.getOnPos(), ModSounds.BUSTER_PIANO.get(), SoundSource.PLAYERS, 4F, 1F);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, this.level().getBlockState(posBelow)), posBelow.getX(), posBelow.getY() + 1, posBelow.getZ(), 128, 1.0, 0.0, 1.0, 0.0);
            }
        } else if (this.falling) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, this.modifiedGravity, 0));

            AABB hitbox = this.getBoundingBox();
            List<Entity> entities = this.level().getEntities(this, hitbox);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity && !hasBeenDamaged(entity) && owner != null) {
                    entity.hurt(this.damageSources().fallingBlock(entity), calculateCustomDamage((Player) owner, customDamage));
                    this.markAsDamaged(entity);
                }
            }
        }
        this.setPos(nextPos.x(), nextPos.y(), nextPos.z());

        /*double nextPosX = this.getX() + this.getDeltaMovement().x();
        double nextPosY = this.getY() + this.getDeltaMovement().y();
        double nextPosZ = this.getZ() + this.getDeltaMovement().z();
        this.setPos(nextPosX, nextPosY, nextPosZ);*/

        if (this.tickCount >= this.life) {
            this.remove(RemovalReason.KILLED);
        }
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    /**
     * Returns null or the entityliving it was ignited by
     */
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    private boolean hasBeenDamaged(Entity entity) {
        return damagedEntities.contains(entity);
    }

    private void markAsDamaged(Entity entity) {
        damagedEntities.add(entity);
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
}
