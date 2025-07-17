package ttv.migami.spas.entity.fruit.flower;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.entity.SummonEntity;
import ttv.migami.spas.init.ModEntities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Petal extends Entity {
    private int warmupDelayTicks;
    public int life = 100;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    public float damage = 5.0F;

    public Petal(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Petal(Level pLevel, LivingEntity pPlayer, Vec3 targetPos, float damage) {
        super(ModEntities.PETAL.get(), pLevel);
        this.setPos(targetPos.add(0, 10, 0));
        this.setOwner(pPlayer);

        Random random = new Random();
        float yaw = random.nextFloat() * 360;
        float pitch = random.nextFloat() * 180 - 90;

        this.setYRot(yaw);
        this.setXRot(pitch);

        this.setYHeadRot(yaw);
        this.setDeltaMovement(0, -0.5, 0);

        if (pPlayer instanceof Player player) {
            this.damage = ServerPlayHandler.calculateCustomDamage(player, damage);
        }
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

        Level level = this.level();

        Vec3 nextPos = this.position().add(this.getDeltaMovement());
        this.setPos(nextPos.x(), nextPos.y(), nextPos.z());
        if (!level.isClientSide)
        {
            if (this.warmupDelayTicks == -1){
                level.playSound(this, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.33F, 1F);
            }
            if (--this.warmupDelayTicks < 0) {
                --this.life;

                if (--this.life < 0) {
                    this.discard();
                }
            }
        }

        AABB hitbox = this.getBoundingBox();
        List<Entity> entities = this.level().getEntities(this, hitbox);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity != owner && !(entity instanceof SummonEntity)) {
                entity.hurt(this.damageSources().fallingBlock(entity), this.damage);
                this.discard();
            }
        }
    }

    public void setOwner(@javax.annotation.Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    /**
     * Returns null or the entityliving it was ignited by
     */
    @javax.annotation.Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

}
