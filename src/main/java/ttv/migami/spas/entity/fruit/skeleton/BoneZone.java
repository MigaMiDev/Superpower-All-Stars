package ttv.migami.spas.entity.fruit.skeleton;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.entity.SummonEntity;
import ttv.migami.spas.init.ModEffects;
import ttv.migami.spas.init.ModEntities;
import ttv.migami.spas.init.ModSounds;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoneZone extends SummonEntity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int warmupDelayTicks;
    private int lifeTicks = 200;
    private float damage = 3.0F;
    private float customDamage = damage;
    private Set<Entity> damagedEntities = new HashSet<>();

    private static final EntityDataAccessor<Boolean> JUST_SPAWNED =
            SynchedEntityData.defineId(BoneZone.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DESPAWNING =
            SynchedEntityData.defineId(BoneZone.class, EntityDataSerializers.BOOLEAN);

    public BoneZone(EntityType<? extends BoneZone> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BoneZone(Level pLevel, LivingEntity entity, BlockPos pPos, float pXRot, float damage) {
        super(ModEntities.BONE_ZONE.get(), pLevel, entity);
        this.setPos(pPos.getCenter().add(0, -0.5, 0));
        this.teleportToGroundOrAir();
        this.setXRot(pXRot);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setNoAi(true);
        this.noPhysics = true;

        this.damage = damage;
        this.customDamage = this.damage;
        if (this.owner instanceof Player owner) {
            this.customDamage = ServerPlayHandler.calculateCustomDamage(owner, this.damage) / 2;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(JUST_SPAWNED, true);
        this.entityData.define(DESPAWNING, false);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        if (!level.isClientSide)
        {
            List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox());
            for (Entity entity : collidedEntities) {
                if (entity instanceof LivingEntity && entity != this.owner) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (!hasBeenDamaged(entity)) {
                        this.mobEffect(livingEntity, ModEffects.STUNNED.get(), 40, 4, false, false);
                        if (this.owner instanceof Player player) {
                            this.hurt(livingEntity, this.customDamage, this.damageSources().playerAttack(player));
                        } else {
                            this.hurt(livingEntity, this.customDamage, this.damageSources().mobAttack(this.owner));
                        }
                        livingEntity.invulnerableTime = 0;
                    }
                    this.mobEffect(livingEntity, MobEffects.MOVEMENT_SLOWDOWN, 5, 5, false, false);
                    this.markAsDamaged(livingEntity);
                }
            }

            if (--this.warmupDelayTicks < 0) {
                --this.lifeTicks;
                if (this.warmupDelayTicks == -1) {
                    this.setJustSpawned(true);

                    level.playSound(this, this.blockPosition(), ModSounds.BONE_ZONE.get(), SoundSource.PLAYERS, 2F, 1F);

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

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        if (this.justSpawned())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.bone_zone.spawn", Animation.LoopType.PLAY_ONCE));
        }
        else if (this.despawning())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.bone_zone.despawn", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
