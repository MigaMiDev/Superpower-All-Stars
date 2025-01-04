package ttv.migami.spas.entity.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ttv.migami.spas.init.ModEntities;

public class CracksMarkEntity extends Entity {
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(CracksMarkEntity.class, EntityDataSerializers.FLOAT);

    public int life = 200;
    private int tickCounter = 0;
    private float randomRotation = 0;

    public CracksMarkEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CracksMarkEntity(Level pLevel, BlockPos blockPos, int life, float size) {
        super(ModEntities.CRACKS_MARK.get(), pLevel);
        this.setPos(blockPos.getCenter());
        this.teleportToGroundOrAir();
        this.life = life;
        this.setScale(size);
    }

    private void teleportToGroundOrAir() {
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

        if (this.tickCount == 1) {
            this.randomRotation = this.level().random.nextFloat() * 360.0F;
        }

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

    private void spawnParticles() {
        //level().addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 0, 0.05, 0);
    }

    public static void summonCracksMark(Level level, BlockPos pos, int life, float size) {
        if (!level.isClientSide) {
            CracksMarkEntity scorchMark = new CracksMarkEntity(level, pos, life, size);
            level.addFreshEntity(scorchMark);
        }
    }

    public float getRandomRotation() {
        return randomRotation;
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

