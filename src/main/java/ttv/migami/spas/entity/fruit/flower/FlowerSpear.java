package ttv.migami.spas.entity.fruit.flower;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.entity.SummonEntity;
import ttv.migami.spas.init.ModEntities;
import ttv.migami.spas.init.ModSounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class FlowerSpear extends SummonEntity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public int life = 14;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private float customDamage = 4;

    private static final EntityDataAccessor<Boolean> JUST_SPAWNED =
            SynchedEntityData.defineId(FlowerSpear.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> RIGHT =
            SynchedEntityData.defineId(FlowerSpear.class, EntityDataSerializers.BOOLEAN);

    public FlowerSpear(EntityType<? extends FlowerSpear> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FlowerSpear(Player owner, Level pLevel, Vec3 blockPos, LivingEntity target, boolean right, float damage) {
        super(ModEntities.FLOWER_SPEAR.get(), pLevel);
        this.setRight(right);
        this.setPos(blockPos);
        this.owner = owner;
        this.noPhysics = true;
        this.lookAt(EntityAnchorArgument.Anchor.FEET, target.getEyePosition());

        this.setOwner(owner);

        if (this.getOwner() instanceof Player) {
            Player ownerAttack = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(ownerAttack, damage);
        }
    }

    public FlowerSpear(Player owner, Level pLevel, Vec3 vec3, BlockPos blockPos, boolean right, float damage) {
        super(ModEntities.FLOWER_SPEAR.get(), pLevel);
        this.setRight(right);
        this.setPos(vec3.add(0.0, -0.5, 0.0));
        this.owner = owner;
        this.noPhysics = true;
        this.lookAt(EntityAnchorArgument.Anchor.FEET, blockPos.getCenter());

        this.setOwner(owner);

        if (this.getOwner() instanceof Player) {
            Player ownerAttack = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(ownerAttack, damage);
        }
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();
        this.setNoAi(true);
        this.noPhysics = true;

        double attackRange = 5.0;
        double coneAngle = Math.toRadians(100);

        Vec3 playerPos = this.position();
        Vec3 lookVec = this.getLookAngle();

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(attackRange));
        if (this.tickCount == 1) {
            if (!level.isClientSide) {
                level.playSound(this, this.blockPosition(), ModSounds.FLOWER_WHIP.get(), SoundSource.PLAYERS, 0.5F, 1F);
            }
        }

        if (this.tickCount == 5) {
            if (!level.isClientSide) {
                level.playSound(this, this.blockPosition(), ModSounds.FLOWER_RUSTLE.get(), SoundSource.PLAYERS, 1F, 1F);
            }

            for (LivingEntity entity : entities) {
                Vec3 entityPos = entity.position().subtract(playerPos);
                double angle = Math.acos(entityPos.normalize().dot(lookVec.normalize()));

                if (angle < coneAngle / 2 && entity != this && (entity != owner)) {
                    if (!level.isClientSide) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 4, 0.3, entity.getBbHeight(), 0.3, 0.2);
                    }

                    entity.hurt(entity.damageSources().playerAttack((Player) owner), customDamage);
                    entity.invulnerableTime = 0;
                }
            }
        }

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

    public void setJustSpawned(boolean justSpawned) {
        this.entityData.set(JUST_SPAWNED, justSpawned);
    }

    public boolean justSpawned() {
        return this.entityData.get(JUST_SPAWNED);
    }

    public boolean isRight() {
        return this.entityData.get(RIGHT);
    }

    public void setRight(boolean right) {
        this.entityData.set(RIGHT, right);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(JUST_SPAWNED, true);
        this.entityData.define(RIGHT, true);
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
                .add(Attributes.MAX_HEALTH, 3.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        if (this.justSpawned() && this.isRight())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.plant_spear.attack_right", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }
        if (this.justSpawned() && !this.isRight())
        {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.plant_spear.attack_left", Animation.LoopType.HOLD_ON_LAST_FRAME));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
