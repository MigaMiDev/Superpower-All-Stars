package ttv.migami.spas.entity.fruit.spider;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ttv.migami.spas.entity.CustomProjectileEntity;
import ttv.migami.spas.init.ModEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RightSpiderString extends CustomProjectileEntity {
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

    public RightSpiderString(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public RightSpiderString(Level pLevel, Player pPlayer, Vec3 pPos, Vec3 targetPos, float damage) {
        super(ModEntities.RIGHT_SPIDER_STRING.get(), pLevel);
        this.setPos(pPos.add(0, 1, 0));

        this.collateral = true;
        this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        this.getLookAngle();

        this.damage = damage;
        this.speed = 3.5D;

        this.setDeltaMovement(this.getLookAngle().x * speed, this.getLookAngle().y * speed, this.getLookAngle().z * speed);
        this.updateHeading();
        this.checkForCollisions = true;
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
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 0.6, this.getZ(), 3, 2, 0.0, 2, 0.05);
            }
        }
    }

    @Override
    protected void onHitEntity(Entity entity)
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
            entity.invulnerableTime = 0;
            if (!entity.level().isClientSide) {
                ((ServerLevel) entity.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(), entity.getZ(), 2, 0.3, entity.getBbHeight(), 0.3, 0.2);
            }
        }
    }
}
