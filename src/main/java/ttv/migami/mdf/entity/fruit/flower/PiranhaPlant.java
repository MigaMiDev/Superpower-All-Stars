package ttv.migami.mdf.entity.fruit.flower;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import ttv.migami.mdf.common.network.ServerPlayHandler;
import ttv.migami.mdf.entity.SummonEntity;
import ttv.migami.mdf.init.ModEntities;
import ttv.migami.mdf.init.ModSounds;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PiranhaPlant extends Entity implements TraceableEntity, GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int warmupDelayTicks;
    private int lifeTicks = 60;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private float damage = 3.0F;
    private float customDamage = damage;
    private Set<Entity> damagedEntities = new HashSet<>();

    private static final EntityDataAccessor<Boolean> JUST_SPAWNED =
            SynchedEntityData.defineId(PiranhaPlant.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DESPAWNING =
            SynchedEntityData.defineId(PiranhaPlant.class, EntityDataSerializers.BOOLEAN);

    public PiranhaPlant(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PiranhaPlant(Level pLevel, Player pPlayer, BlockPos pPos, float pXRot) {
        super(ModEntities.PIRANHA_PLANT.get(), pLevel);
        this.setPos(pPos.getCenter().add(0, 0.1, 0));
        this.setXRot(pXRot);
        this.setOwner(pPlayer);
        this.lookAt(EntityAnchorArgument.Anchor.FEET, pPlayer.getPosition(1F));

        if (this.getOwner() instanceof Player) {
            Player owner = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(owner, this.damage);
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

    @Override
    protected void defineSynchedData() {
        this.entityData.define(JUST_SPAWNED, true);
        this.entityData.define(DESPAWNING, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        if (!level.isClientSide)
        {
            List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox());
            for (Entity entity : collidedEntities) {
                if (entity instanceof LivingEntity && entity != owner && !(entity instanceof SummonEntity)) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (!hasBeenDamaged(entity)) {
                        livingEntity.hurt(this.damageSources().playerAttack((Player) owner), this.customDamage);
                        livingEntity.invulnerableTime = 0;
                        livingEntity.removeEffect(MobEffects.LEVITATION);
                        livingEntity.removeEffect(MobEffects.SLOW_FALLING);
                    }
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 5, false, false));
                    this.markAsDamaged(livingEntity);
                }
            }

            if (--this.warmupDelayTicks < 0) {
                --this.lifeTicks;
                if (this.warmupDelayTicks == -1) {
                    this.setJustSpawned(true);

                    level.playSound(this, this.blockPosition(), ModSounds.FLOWER_RUSTLE.get(), SoundSource.PLAYERS, 2F, 1F);
                    level.playSound(this, this.blockPosition(), ModSounds.PIRANHA_PLANT_BITE.get(), SoundSource.PLAYERS, 1F, 1F);

                    BlockPos entityPos = this.blockPosition();
                    BlockPos belowPos = entityPos.below();
                    BlockState blockStateBelow = level.getBlockState(belowPos);

                    ServerLevel serverLevel = (ServerLevel) this.level();
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, this.level().getBlockState(belowPos)), belowPos.getX(), belowPos.getY() + 1, belowPos.getZ(), 128, 1.0, 0.0, 1.0, 0.0);
                    serverLevel.playSound(null, belowPos, blockStateBelow.getSoundType().getBreakSound(), SoundSource.BLOCKS, 3.0F, 1.0F);
                }
                if (this.lifeTicks < 5) {
                    this.setJustSpawned(false);
                    this.setDespawning(true);
                }
                if (this.lifeTicks < 0) {
                    this.discard();
                    ServerLevel serverLevel = (ServerLevel) this.level();
                    serverLevel.playSound(null, this.getOnPos(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 4F, 2F);
                    serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 64, 1.5, 2.0, 1.5, 0.05);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        if (this.justSpawned())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.piranha_plant.bite", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
