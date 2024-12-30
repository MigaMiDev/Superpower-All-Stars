package ttv.migami.mdf.entity.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractGroundMarkEntity extends Entity {
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(AbstractGroundMarkEntity.class, EntityDataSerializers.FLOAT);

    protected int life;
    private int tickCounter = 0;

    public AbstractGroundMarkEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractGroundMarkEntity(EntityType<?> entityType, Level level, BlockPos blockPos, int life, float size) {
        super(entityType, level);
        this.setPos(blockPos.getCenter());
        this.teleportToGroundOrAir();
        this.life = life;
        this.setScale(size);
    }

    void teleportToGroundOrAir() {
        BlockPos currentPos = this.blockPosition();
        Level level = this.level();

        while (currentPos.getY() > level.getMinBuildHeight()) {
            if (isSolidBlock(level, currentPos.below())) {
                break;
            }
            currentPos = currentPos.below();
        }

        while (currentPos.getY() < level.getMaxBuildHeight()) {
            if (!isSolidBlock(level, currentPos.above())) {
                currentPos = currentPos.above();
                break;
            }
            currentPos = currentPos.above();
        }

        this.setPos(currentPos.getX() + 0.5, currentPos.getY() - 1, currentPos.getZ() + 0.5);
    }

    private boolean isSolidBlock(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        return blockState.isSolidRender(level, pos) && !blockState.isAir();
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCounter % 10 == 0) {
            spawnParticles();
        }

        this.teleportToGroundOrAir();
        tickCounter++;

        life--;
        if (life <= 0) {
            this.discard();
        }
    }

    protected abstract void spawnParticles();

    public static void createGroundMark(Level level, BlockPos pos, int life, float size, EntityType<? extends AbstractGroundMarkEntity> entityType) {
        if (!level.isClientSide) {
            AbstractGroundMarkEntity entity = entityType.create(level);
            entity.setPos(pos.getCenter());
            entity.life = life;
            entity.setScale(size);
            level.addFreshEntity(entity);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SCALE, 1.0F);
    }

    public void setScale(float scale) {
        this.entityData.set(SCALE, scale);
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("Scale", this.getScale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setScale(compound.getFloat("Scale"));
    }
}