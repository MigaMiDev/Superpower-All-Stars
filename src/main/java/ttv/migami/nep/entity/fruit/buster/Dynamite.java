package ttv.migami.nep.entity.fruit.buster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import ttv.migami.nep.entity.CustomProjectileEntityOld;
import ttv.migami.nep.entity.CustomThrowableEntity;
import ttv.migami.nep.init.ModEntities;
import ttv.migami.nep.init.ModParticleTypes;

import java.util.List;

import static ttv.migami.nep.entity.fx.ScorchMarkEntity.summonScorchMark;
import static ttv.migami.nep.world.CraterCreator.createCrater;

/**
 * Author: MrCrayfish
 */
public class Dynamite extends CustomThrowableEntity
{
    public boolean isPowered = false;

    public Dynamite(EntityType<? extends CustomThrowableEntity> entityType, Level worldIn)
    {
        super(entityType, worldIn);
    }

    public Dynamite(EntityType<? extends CustomThrowableEntity> entityType, Level world, LivingEntity entity)
    {
        super(entityType, world, entity);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.05F);
        this.setMaxLife(20 * 1);
    }

    public Dynamite(Level world, LivingEntity entity, int timeLeft)
    {
        super(ModEntities.DYNAMITE.get(), world, entity);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.05F);
        this.setMaxLife(timeLeft);

        if (entity instanceof Player) {
            Player owner = (Player) entity;
            if (owner.experienceLevel >= 20 || owner.isCreative()) {
                this.isPowered = true;
            }
        }

    }

    @Override
    protected void defineSynchedData()
    {
    }

    @Override
    public void tick()
    {
        super.tick();
        particleTick();
    }

    public void particleTick()
    {
        if (this.level().isClientSide)
        {
            this.level().addParticle(ParticleTypes.SMOKE, true, this.getX(), this.getY() + 0.7, this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onHit(HitResult result)
    {
        switch(result.getType())
        {
            case BLOCK:
                BlockHitResult blockResult = (BlockHitResult) result;

                BlockPos resultPos = blockResult.getBlockPos();
                BlockState state = this.level().getBlockState(resultPos);
                SoundEvent event = state.getBlock().getSoundType(state, this.level(), resultPos, this).getStepSound();

                double speed = this.getDeltaMovement().length();
                if(speed > 0.1) {
                    this.level().playSound(null, result.getLocation().x, result.getLocation().y, result.getLocation().z, event, SoundSource.AMBIENT, 1.0F, 1.0F);
                } this.bounce(blockResult.getDirection());

                break;
            case ENTITY:
                this.remove(RemovalReason.KILLED);
                this.onDeath();

                break;
            default:
                break;
        }
    }

    @Override
    public void onDeath()
    {
        double effectRadius= 2;
        float radius = 1F;

        if (isPowered) {
            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 32, 1.7, 1.7, 1.7, 0.0);
            }

            effectRadius = 5;
            radius = 1.5F;
        }

        summonScorchMark(this.level(), this.getOnPos(), 200, 8);
        createCrater(this.level(), this.getOnPos(), 5);

        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.sendParticles(ModParticleTypes.SMOKE.get(), this.getX(), this.getY(), this.getZ(), 16, 2, 2, 2, 3);
        }

        CustomProjectileEntityOld.createExplosion(this, radius, true);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 4, 1);

        AABB areaOfEffect = new AABB(this.getX() - effectRadius, this.getY() - effectRadius /2 , this.getZ() - effectRadius, this.getX() + effectRadius, this.getY() + effectRadius / 2, this.getZ() + effectRadius);
        List<Entity> entitiesArea = this.level().getEntities(this, areaOfEffect);
        for (Entity entity : entitiesArea) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setSecondsOnFire(6);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0, false, false));
            }
        }
    }
}
