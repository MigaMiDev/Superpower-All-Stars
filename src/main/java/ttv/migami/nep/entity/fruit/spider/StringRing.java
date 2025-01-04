package ttv.migami.nep.entity.fruit.spider;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import ttv.migami.nep.init.ModEffects;
import ttv.migami.nep.init.ModEntities;
import ttv.migami.nep.init.ModParticleTypes;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ttv.migami.nep.common.network.ServerPlayHandler.calculateCustomDamage;

public class StringRing extends Entity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public int life = 200;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private Set<Entity> damagedEntities = new HashSet<>();

    private static final EntityDataAccessor<Boolean> JUST_SPAWNED =
            SynchedEntityData.defineId(StringRing.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DESPAWNING =
            SynchedEntityData.defineId(StringRing.class, EntityDataSerializers.BOOLEAN);

    public StringRing(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public StringRing(LivingEntity owner, Level pLevel, BlockPos blockPos, int life) {
        super(ModEntities.STRING_RING.get(), pLevel);
        this.setPos(blockPos.getCenter().add(0.0, -0.5, 0.0));
        this.owner = owner;
        this.life = life;

        this.setOwner(owner);
    }

    public StringRing(LivingEntity owner, Level pLevel, Vec3 targetPos, int life) {
        super(ModEntities.STRING_RING.get(), pLevel);
        this.setPos(targetPos);
        this.owner = owner;
        this.life = life;

        this.setOwner(owner);
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
            for (int i = 0; i < 32; i++) {
                this.level().addParticle(ModParticleTypes.STRING_RING.get(), true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }

        if (this.tickCount >= this.life) {
            this.ejectPassengers();
            this.remove(RemovalReason.KILLED);
            return;
        }

        if (!level.isClientSide)
        {
            if (this.tickCount > 5 && (this.tickCount <= (this.life - 10))) {
                List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox().inflate(0, 10, 0));
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity && entity != owner) {
                        entity.invulnerableTime = 0;
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false));
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 2, false, false));
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 60, 0, false, false));
                    }
                }
            }
            if (this.tickCount < 24) {
                if (this.tickCount % 2 == 0) {
                    level.playSound(this, this.blockPosition(), SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 3F, 1.25F);
                }
            }
            if (this.tickCount > 47 && (this.tickCount <= (this.life - 10))) {
                List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox().inflate(0, 10, 0));
                if (this.tickCount < 53) {
                    level.playSound(this, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1F, 1F);
                }
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity && entity != owner && !(entity instanceof SpiderFang)) {
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
