package ttv.migami.spas.entity.fruit.spider;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ttv.migami.spas.entity.CustomProjectileEntity;
import ttv.migami.spas.init.ModEntities;
import ttv.migami.spas.init.ModParticleTypes;
import ttv.migami.spas.init.ModSounds;

import java.util.UUID;

public class TeaCup extends CustomProjectileEntity {
    private int warmupDelayTicks;
    public int life = 100;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    public float damage = 3;
    public float customDamage = damage;
    public boolean affectedByGravity;

    public TeaCup(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public TeaCup(Level pLevel, Player pPlayer, Vec3 pPos, Vec3 targetPos, float damage) {
        super(ModEntities.TEA_CUP.get(), pLevel);
        this.setPos(pPos.add(0, 1, 0));

        this.setPos(pPos.add(0, 1, 0));
        this.checkForCollisions = true;

        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();
        this.affectedByGravity = true;

        this.damage = damage;
        this.speed = 1.5D;

        this.setDeltaMovement(this.getLookAngle().x * speed, this.getLookAngle().y * speed, this.getLookAngle().z * speed);
        this.updateHeading();
    }

    @Override
    protected void onProjectileTick()
    {
        if(this.level().isClientSide && this.tickCount < this.life) {
            if (this.tickCount > 2)
            {
                this.level().addParticle(ModParticleTypes.TEA.get(), true, this.getX(), this.getY() + 0.5, this.getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    protected void onHitEntity(Entity entity)
    {
        if (entity instanceof Display.BlockDisplay) {
            return;
        }

        if (entity != this.owner && this.tickCount > 2) {
            entity.hurt(this.damageSources().playerAttack((Player) owner), this.customDamage);
            entity.invulnerableTime = 0;

            if (entity instanceof LivingEntity) {
                LivingEntity pTarget = (LivingEntity) entity;

                pTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, false));
                pTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 6, false, false));
            }

            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ModParticleTypes.TEA.get(), this.getX(), this.getY() + 1, this.getZ(), 24, 0.2, 0.0, 0.3, 0.2);
                serverLevel.playSound(null, this.getOnPos(), ModSounds.CUP_BREAKING.get(), SoundSource.PLAYERS, 1.5F, 1F);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {
        if (!this.level().isClientSide) {
            BlockPos entityPos = this.blockPosition();
            BlockPos belowPos = entityPos.below();

            ServerLevel serverLevel = (ServerLevel) this.level();

            serverLevel.sendParticles(ModParticleTypes.TEA.get(), this.getX(), belowPos.getY() + 1, this.getZ(), 24, 0.2, 0.0, 0.3, 0.2);
            serverLevel.playSound(null, this.getOnPos(), ModSounds.CUP_BREAKING.get(), SoundSource.PLAYERS, 1.5F, 1F);

            this.remove(RemovalReason.KILLED);
        }
    }

}
