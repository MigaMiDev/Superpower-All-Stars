package ttv.migami.spas.entity.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TimedBlockDisplayEntity extends Display.BlockDisplay {

    private int lifeTime;

    public TimedBlockDisplayEntity(EntityType<? extends BlockDisplay> entityType, Level level) {
        super(entityType, level);
        this.lifeTime = 160;
        this.teleportToGroundOrAir();
        this.noCulling = true;
    }

    @Override
    public void tick() {
        super.tick();

        this.noCulling = true;
        if (this.isSolidBlock(this.level(), this.getOnPos())) {
            this.setBlockState(this.getBlockStateOn());
        }

        if (this.lifeTime >= 159) {
            setPos(this.getX(), this.getY() + 0.6, this.getZ());
        }
        if (this.lifeTime <= 20) {
            setPos(this.getX(), this.getY() - 0.2, this.getZ());
        } else {
            this.teleportToGroundOrAir();
        }
        if (this.lifeTime <= 0) {
            this.discard();
            this.kill();
        }
        this.lifeTime--;
    }

    private void teleportToGroundOrAir() {
        BlockPos currentPos = this.blockPosition();
        Level level = this.level();

        while (currentPos.getY() > level.getMinBuildHeight() && level.getBlockState(currentPos.below()).isAir()) {
            currentPos = currentPos.below();
        }

        while (!level.getBlockState(currentPos).isAir() && currentPos.getY() < level.getMaxBuildHeight()) {
            currentPos = currentPos.above();
        }

        this.setPos(currentPos.getX() + 0.5, currentPos.getY(), currentPos.getZ() + 0.5);
    }

    private boolean isSolidBlock(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        return blockState.isSolidRender(level, pos) && !blockState.isAir();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LifeTime", this.lifeTime);
        compound.putBoolean("IsDebri", true);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("LifeTime")) {
            this.lifeTime = compound.getInt("LifeTime");
        }
    }
}