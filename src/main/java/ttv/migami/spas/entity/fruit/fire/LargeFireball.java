package ttv.migami.spas.entity.fruit.fire;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ttv.migami.spas.Config;
import ttv.migami.spas.entity.CustomProjectileEntity;
import ttv.migami.spas.entity.CustomProjectileEntityOld;
import ttv.migami.spas.init.ModEffects;
import ttv.migami.spas.init.ModEntities;
import ttv.migami.spas.init.ModParticleTypes;
import ttv.migami.spas.init.ModSounds;

import java.util.List;
import java.util.UUID;

import static ttv.migami.spas.entity.fx.ScorchMarkEntity.summonScorchMark;
import static ttv.migami.spas.world.CraterCreator.createCrater;

public class LargeFireball extends CustomProjectileEntity {
    private int warmupDelayTicks;
    public int life = 100;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    public float damage = 3;
    public boolean affectedByGravity;
    public boolean explosive;

    public LargeFireball(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LargeFireball(Level pLevel, Vec3 pPos, Vec3 targetPos, float damage) {
        super(ModEntities.FIREBALL.get(), pLevel);
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

    public LargeFireball(Level pLevel, Vec3 pPos, Vec3 targetPos, boolean explosive, float damage) {
        super(ModEntities.LARGE_FIREBALL.get(), pLevel);
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
    protected void onHitEntity(Entity entity)
    {
        if (entity instanceof Display.BlockDisplay) {
            return;
        }
        int explosionRadius = 1;
        double radius = 2;
        if (this.explosive) {
            explosionRadius = 3;
            radius = 8;
        }

        Level pLevel = this.level();

        /*if (this.owner instanceof Player) {
            Player playerOwner = (Player) this.owner;
            if (playerOwner.experienceLevel > 20 || playerOwner.isCreative()) {
                explosionRadius = 1;
            }
        }*/
        if (Config.COMMON.gameplay.griefing.setFireToBlocks.get()) {
            CustomProjectileEntityOld.createFireExplosion(this, explosionRadius, true);
        } else {
            CustomProjectileEntityOld.createExplosion(this, explosionRadius, true);
        }

        AABB areaOfEffect = new AABB(this.getX() - radius, this.getY() - radius /2 , this.getZ() - radius, this.getX() + radius, this.getY() + radius / 2, this.getZ() + radius);
        List<Entity> entitiesArea = pLevel.getEntities(this, areaOfEffect);
        for (Entity entityAttack : entitiesArea) {
            if (entityAttack instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                    livingEntity.setSecondsOnFire(9);
                }
                livingEntity.setSecondsOnFire(5);
            }
        }

        summonScorchMark(pLevel, this.getOnPos(), 200, 10F);
        createCrater(pLevel, this.getOnPos(), 6);

        if (entity != this.owner && this.tickCount > 2) {
            if (entity instanceof LivingEntity pTarget) {
                if (pTarget.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                    pTarget.setSecondsOnFire(15);
                }
                pTarget.setSecondsOnFire(5);
            }

            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();

                BlockPos entityPos = this.blockPosition();
                BlockPos belowPos = entityPos.below();

                if (this.explosive) {
                    serverLevel.sendParticles(ModParticleTypes.SMOKE.get(), this.getX(), this.getY(), this.getZ(), 16, 2, 2, 2, 3);
                    serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 20, explosionRadius, explosionRadius, explosionRadius, 0.2);
                }

                serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1, this.getZ(), 5, 0.2, 0.0, 0.3, 0.2);
                serverLevel.playSound(null, this.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10F, 1F);
            }
        }
    }

    @Override
    protected void impactEffect() {
        if (this.level().isClientSide) {
            this.level().addParticle(ModParticleTypes.FIRE_BIG_EXPLOSION.get(), true, this.getX() - (this.getDeltaMovement().x()), this.getY() - (this.getDeltaMovement().y()), this.getZ() - (this.getDeltaMovement().z()), 0, 0, 0);
        }
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {
        int explosionRadius = 1;
        double radius = 2;
        if (this.explosive) {
            explosionRadius = 3;
            radius = 8;
        }

        Level pLevel = this.level();

        if (Config.COMMON.gameplay.griefing.setFireToBlocks.get()) {
            CustomProjectileEntityOld.createFireExplosion(this, explosionRadius, true);
        } else {
            CustomProjectileEntityOld.createExplosion(this, explosionRadius, true);
        }

        AABB areaOfEffect = new AABB(this.getX() - radius, this.getY() - radius /2 , this.getZ() - radius, this.getX() + radius, this.getY() + radius / 2, this.getZ() + radius);
        List<Entity> entitiesArea = pLevel.getEntities(this, areaOfEffect);
        for (Entity entity : entitiesArea) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
                    livingEntity.setSecondsOnFire(9);
                }
                livingEntity.setSecondsOnFire(5);
            }
        }

        summonScorchMark(pLevel, this.getOnPos(), 200, 10);
        createCrater(pLevel, this.getOnPos(), 6);

        if (!this.level().isClientSide) {
            BlockPos entityPos = this.blockPosition();
            BlockPos belowPos = entityPos.below();

            ServerLevel serverLevel = (ServerLevel) this.level();

            if (this.explosive) {
                serverLevel.sendParticles(ModParticleTypes.SMOKE.get(), this.getX(), this.getY(), this.getZ(), 16, 2, 2, 2, 3);
                serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 20, explosionRadius, explosionRadius, explosionRadius, 0.2);
            }

            serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), belowPos.getY() + 1, this.getZ(), 5, 0.2, 0.0, 0.3, 0.2);
            serverLevel.playSound(null, this.getOnPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 10F, 1F);

            this.remove(RemovalReason.KILLED);
        }
    }
}
