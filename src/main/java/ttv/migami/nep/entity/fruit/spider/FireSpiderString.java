package ttv.migami.nep.entity.fruit.spider;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ttv.migami.nep.common.network.ServerPlayHandler;
import ttv.migami.nep.entity.CustomProjectileEntityOld;
import ttv.migami.nep.init.ModEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FireSpiderString extends CustomProjectileEntityOld {
    private int warmupDelayTicks;
    public int life = 100;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Vec3 target;
    public float damage = 2;
    public float customDamage = damage;
    private List<Entity> hurtEntities = new ArrayList<>();

    public FireSpiderString(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireSpiderString(Level pLevel, Player pPlayer, Vec3 pPos, Vec3 targetPos) {
        super(ModEntities.FIRE_SPIDER_STRING.get(), pLevel);
        this.setPos(pPos.add(0, 1, 0));
        this.setOwner(pPlayer);
        this.collateral = true;
        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();
        double speed = 3.5F;
        this.setDeltaMovement(this.getLookAngle().x * speed, this.getLookAngle().y * speed, this.getLookAngle().z * speed);
        this.updateHeading();
        this.checkForCollisions = true;
    }

    @Override
    protected void defineSynchedData() {

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
        this.updateHeading();

        Level level = this.level();

        if (!level.isClientSide)
        {
            if (--this.warmupDelayTicks < 0) {
                --this.life;
                if (this.warmupDelayTicks == -1) {
                    level.playSound(this, this.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1F, 1F);
                    level.playSound(this, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1F, 1F);
                }

                if (--this.life < 0) {
                    this.discard();
                }
            }
        }
    }

    @Override
    protected void onProjectileTick()
    {
        if(!this.level().isClientSide && this.tickCount < this.life) {
            if (this.tickCount > 2)
            {
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.6, this.getZ(), 2, 1, 0.2, 1, 0.01);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 0.6, this.getZ(), 3, 2, 0.0, 2, 0.0);
            }
        }
    }

    @Override
    public float calculateDamage() {
        this.customDamage = this.damage;
        if (this.getOwner() instanceof Player) {
            Player owner = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(owner, this.damage);
        }
        return this.customDamage;
    }

    @Override
    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec)
    {
        if (entity instanceof Display.BlockDisplay) {
            return;
        }
        if (entity instanceof StringRing) {
            return;
        }
        if (entity instanceof SpiderFang) {
            return;
        }
        if (entity != this.owner && entity instanceof LivingEntity) {
            entity.hurt(this.damageSources().mobProjectile(this, this.owner), this.customDamage);
            entity.setSecondsOnFire(6);
            entity.invulnerableTime = 0;
            if (!entity.level().isClientSide) {
                ((ServerLevel) entity.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 2, 0.3, entity.getBbHeight(), 0.3, 0.2);
            }
        }
    }

    /*@Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z)
    {
        if (!this.level().isClientSide) {
            BlockPos entityPos = this.blockPosition();
            BlockPos belowPos = entityPos.below();
            ServerLevel serverLevel = (ServerLevel) this.level();
            BlockState blockStateBelow = serverLevel.getBlockState(belowPos);

            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, this.level().getBlockState(belowPos)), this.getX(), belowPos.getY() + 1, this.getZ(), 32, 0.2, 0.0, 0.2, 0.0);
            serverLevel.playSound(null, belowPos, blockStateBelow.getSoundType().getBreakSound(), SoundSource.BLOCKS, 3.0F, 1.0F);
        }
    }*/

    @Nullable
    @Override
    public Entity getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    private Vec3 getDirection(LivingEntity pShooter)
    {
        return this.getVectorFromRotation(pShooter.getXRot(), pShooter.getYHeadRot());
    }

    private Vec3 getVectorFromRotation(float pitch, float yaw)
    {
        float f = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.cos(-pitch * 0.017453292F);
        float f3 = Mth.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

}
