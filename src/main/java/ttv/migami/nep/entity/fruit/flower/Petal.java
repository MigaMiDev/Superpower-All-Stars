package ttv.migami.nep.entity.fruit.flower;

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
import ttv.migami.nep.common.network.ServerPlayHandler;
import ttv.migami.nep.entity.SummonEntity;
import ttv.migami.nep.init.ModEntities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static ttv.migami.nep.common.network.ServerPlayHandler.calculateCustomDamage;

public class Petal extends Entity {
    private int warmupDelayTicks;
    public int life = 100;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    public float damage = 5.0F;
    public float customDamage = damage;

    public Petal(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Petal(Level pLevel, Player pPlayer, Vec3 targetPos) {
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
                entity.hurt(this.damageSources().fallingBlock(entity), customDamage);
                this.discard();
            }
        }
    }

    public float calculateDamage() {
        this.customDamage = this.damage;
        if (this.getOwner() instanceof Player) {
            Player owner = (Player) this.getOwner();
            this.customDamage = ServerPlayHandler.calculateCustomDamage(owner, calculateCustomDamage(owner, 2F));
        }
        return this.customDamage;
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
