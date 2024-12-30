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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
import ttv.migami.mdf.init.ModEntities;
import ttv.migami.mdf.init.ModSounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Vine extends SummonEntity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public int life = 200;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    private static final EntityDataAccessor<Boolean> JUST_SPAWNED =
            SynchedEntityData.defineId(Vine.class, EntityDataSerializers.BOOLEAN);

    public Vine(EntityType<? extends Vine> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Vine(LivingEntity owner, Level pLevel, Vec3 blockPos) {
        super(ModEntities.VINE.get(), pLevel);
        this.setPos(blockPos);
        this.teleportToGroundOrAir();
        this.owner = owner;
        this.noPhysics = true;
        this.setNoAi(true);
        this.lookAt(EntityAnchorArgument.Anchor.FEET, owner.getPosition(1F));

        this.setOwner(owner);
    }

    protected void teleportToGroundOrAir() {
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

        this.fallDistance = 0;

        if (this.tickCount == 1) {
            this.setJustSpawned(true);
        }

        if (this.tickCount >= this.life) {
            this.remove(RemovalReason.KILLED);
            if (!level.isClientSide)
            {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 64, 2.25, 3.0, 2.25, 0.05);
            }
            return;
        }

        if (!level.isClientSide)
        {
            ServerLevel serverLevel = (ServerLevel) this.level();
            if (this.tickCount == 1) {
                level.playSound(this, this.blockPosition(), ModSounds.FLOWER_RUSTLE.get(), SoundSource.PLAYERS, 1F, 1F);

                BlockPos entityPos = this.blockPosition();
                BlockPos belowPos = entityPos.below();
                BlockState blockStateBelow = level.getBlockState(belowPos);

                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, this.level().getBlockState(belowPos)), belowPos.getX(), belowPos.getY() + 1, belowPos.getZ(), 128, 1.0, 0.0, 1.0, 0.0);
                serverLevel.playSound(null, belowPos, blockStateBelow.getSoundType().getBreakSound(), SoundSource.BLOCKS, 3.0F, 1.0F);
            }
            if (this.tickCount <= 5) {
                List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox());
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity && entity != owner && !(entity instanceof SummonEntity)) {
                        VineTrap vineTrap = new VineTrap(owner, level, entity.getPosition(1F), 100);
                        level.addFreshEntity(vineTrap);
                        entity.hurt(this.damageSources().cactus(), 3);
                        this.remove(RemovalReason.KILLED);
                    }
                }
            }
            if (this.tickCount > 5) {
                List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox());
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity && entity != owner && !(entity instanceof SummonEntity)) {
                        if (!(((LivingEntity) entity).hasEffect(MobEffects.POISON))){
                            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON,  120, 1, false, true));
                        }
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,  40, 2, false, false));
                    }
                }
            }
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

    public void setJustSpawned(boolean justSpawned) {
        this.entityData.set(JUST_SPAWNED, justSpawned);
    }

    public boolean justSpawned() {
        return this.entityData.get(JUST_SPAWNED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(JUST_SPAWNED, true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        if (this.justSpawned())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.vine.move", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
