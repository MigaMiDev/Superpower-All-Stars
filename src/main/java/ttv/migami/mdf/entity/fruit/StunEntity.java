package ttv.migami.mdf.entity.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import ttv.migami.mdf.init.ModEntities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class StunEntity extends Entity {
    public int life = 200;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public StunEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public StunEntity(LivingEntity owner, Level pLevel, BlockPos blockPos, int life) {
        super(ModEntities.STUN_ENTITY.get(), pLevel);
        this.setPos(blockPos.getCenter());
        this.owner = owner;
        this.life = life;

        this.setOwner(owner);
    }

    public StunEntity(LivingEntity owner, Level pLevel, Vec3 targetPos, int life) {
        super(ModEntities.STUN_ENTITY.get(), pLevel);
        this.setPos(targetPos);
        this.owner = owner;
        this.life = life;

        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();

        if (this.tickCount >= this.life) {
            this.ejectPassengers();
            this.remove(RemovalReason.KILLED);
            return;
        }

        if (!level.isClientSide)
        {
            if (this.tickCount > 5) {
                List<Entity> collidedEntities = level.getEntities(this, this.getBoundingBox());
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity && entity != owner) {
                        entity.startRiding(this, true);
                    }
                }
            }
        }
    }

    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() <= 5;
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

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

}
