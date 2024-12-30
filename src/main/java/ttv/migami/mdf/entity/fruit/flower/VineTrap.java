package ttv.migami.mdf.entity.fruit.flower;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import ttv.migami.mdf.entity.SummonEntity;
import ttv.migami.mdf.init.ModEffects;
import ttv.migami.mdf.init.ModEntities;
import ttv.migami.mdf.init.ModSounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class VineTrap extends Entity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public int life = 500;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    private static final EntityDataAccessor<Boolean> JUST_SPAWNED =
            SynchedEntityData.defineId(VineTrap.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DESPAWNING =
            SynchedEntityData.defineId(VineTrap.class, EntityDataSerializers.BOOLEAN);

    public VineTrap(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public VineTrap(LivingEntity owner, Level pLevel, Vec3 targetPos, int life) {
        super(ModEntities.VINE_TRAP.get(), pLevel);
        this.setPos(targetPos);
        this.owner = owner;
        this.life = life;

        this.setOwner(owner);
        this.lookAt(EntityAnchorArgument.Anchor.FEET, owner.getEyePosition());
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        if (this.tickCount >= this.life) {
            this.ejectPassengers();
            this.remove(RemovalReason.KILLED);
            return;
        }

        if (!level.isClientSide)
        {
            if (this.tickCount == 1) {
                level.playSound(this, this.blockPosition(), ModSounds.FLOWER_RUSTLE.get(), SoundSource.PLAYERS, 3F, 1F);
                this.setJustSpawned(false);
            }
            if (this.tickCount > 2) {
                List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox());
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity livingEntity && entity != owner && !(entity instanceof SummonEntity)) {
                        livingEntity.startRiding(this, true);
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 6, false, false));
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 6, false, false));
                        livingEntity.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 40, 4, false, false));
                    }
                }
            }
        }
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
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.vine_trap.spawn", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        else {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.vine_trap.idle", Animation.LoopType.LOOP));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
