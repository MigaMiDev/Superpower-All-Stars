package ttv.migami.spas.entity.fruit.buster;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import ttv.migami.spas.init.ModEffects;
import ttv.migami.spas.init.ModEntities;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ttv.migami.spas.common.network.ServerPlayHandler.calculateCustomDamage;

public class Cactus extends Entity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public int life = 200;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private Set<Entity> damagedEntities = new HashSet<>();

    private static final EntityDataAccessor<Boolean> JUST_SPAWNED =
            SynchedEntityData.defineId(Cactus.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DESPAWNING =
            SynchedEntityData.defineId(Cactus.class, EntityDataSerializers.BOOLEAN);

    public Cactus(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Cactus(LivingEntity owner, Level pLevel, BlockPos blockPos, int life) {
        super(ModEntities.CACTUS.get(), pLevel);
        this.setPos(blockPos.getCenter().add(0.0, -0.5, 0.0));
        this.teleportToGroundOrAir();

        this.owner = owner;
        this.life = life;

        this.setOwner(owner);
    }

    public Cactus(LivingEntity owner, Level pLevel, Vec3 targetPos, int life) {
        super(ModEntities.CACTUS.get(), pLevel);
        this.setPos(targetPos);
        this.owner = owner;
        this.life = life;

        this.setOwner(owner);
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

        this.setPos(currentPos.getX() + 0.5, currentPos.getY(), currentPos.getZ() + 0.5);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        if (this.tickCount == 1) {
            this.setJustSpawned(true);
            if (this.owner != null) {
                this.lookAt(EntityAnchorArgument.Anchor.FEET, owner.getEyePosition());
            }
        }

        if (this.tickCount >= (this.life - 10)) {
            this.ejectPassengers();
            this.setJustSpawned(false);
            this.setDespawning(true);
        }

        if (this.tickCount >= this.life) {
            this.ejectPassengers();
            this.remove(RemovalReason.KILLED);
            return;
        }

        if (!level.isClientSide)
        {
            if (this.tickCount > 5 && (this.tickCount <= (this.life - 10))) {
                List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox());
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity && entity != owner) {
                        entity.startRiding(this, true);
                        if (!hasBeenDamaged(entity)) {
                            entity.hurt(this.damageSources().cactus(), calculateCustomDamage((Player) owner, 3F));
                            entity.invulnerableTime = 0;
                            ((ServerLevel) level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 6, 0.3, entity.getBbHeight(), 0.3, 0.2);
                        }
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(ModEffects.FEATHER_FALLING.get(), 60, 0, false, false));
                        this.markAsDamaged(entity);

                    }
                }
            }
        }
    }

    private boolean hasBeenDamaged(Entity entity) {
        return damagedEntities.contains(entity);
    }

    private void markAsDamaged(Entity entity) {
        damagedEntities.add(entity);
    }

    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() <= 5;
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

    public void setJustSpawned(boolean justSpawned) {
        this.entityData.set(JUST_SPAWNED, justSpawned);
    }

    public boolean justSpawned() {
        return this.entityData.get(JUST_SPAWNED);
    }

    public void setDespawning(boolean despawning) {
        this.entityData.set(DESPAWNING, despawning);
    }

    public boolean despawning() {
        return this.entityData.get(DESPAWNING);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(JUST_SPAWNED, true);
        this.entityData.define(DESPAWNING, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        if (this.justSpawned())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.cactus.spawn", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        else if (this.despawning())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.cactus.despawn", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        else {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.cactus.idle", Animation.LoopType.LOOP));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
