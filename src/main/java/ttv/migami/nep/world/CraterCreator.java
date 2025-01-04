package ttv.migami.nep.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ttv.migami.nep.Config;
import ttv.migami.nep.entity.fruit.TimedBlockDisplayEntity;

public class CraterCreator {

    /**
     * Creates a crater effect by spawning block display entities in a ring.
     *
     * @param level        The current world level.
     * @param explosionPos The center position of the explosion.
     * @param radius       The radius of the crater.
     */
    public static void createCrater(Level level, BlockPos explosionPos, int radius) {
        if (level.isClientSide()) return;

        if (!(level instanceof ServerLevel serverLevel)) return;

        if (Config.COMMON.gameplay.griefing.destructionDebri.get()) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + z * z);
                
                    if (distance > radius - 1 && distance <= radius) {
                        BlockPos pos = explosionPos.offset(x, 0, z).below(1);

                        BlockState blockState = level.getBlockState(pos);

                        createBlockDisplayEntity(serverLevel, pos, blockState, explosionPos);
                    }
                }
            }
        }
    }

    /**
     * Creates a block display entity at the specified position with rotation facing the center.
     * The entity despawns after the specified number of seconds.
     *
     * @param serverLevel  The server-level world.
     * @param pos          The position to spawn the block display.
     * @param blockState   The block state to display.
     * @param centerPos    The center position of the crater for rotation calculation.
     */
    private static void createBlockDisplayEntity(ServerLevel serverLevel, BlockPos pos, BlockState blockState, BlockPos centerPos) {
        TimedBlockDisplayEntity blockDisplay = new TimedBlockDisplayEntity(EntityType.BLOCK_DISPLAY, serverLevel);
        if (!blockState.canBeReplaced()) {
            blockDisplay.setBlockState(blockState);

            Vec3 displayPos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            blockDisplay.setPos(displayPos.x, displayPos.y, displayPos.z);

            blockDisplay.setBillboardConstraints(Display.BillboardConstraints.FIXED);
            blockDisplay.setShadowRadius(0);
            blockDisplay.setShadowStrength(0);

            double deltaX = centerPos.getX() + 0.5 - displayPos.x;
            double deltaZ = centerPos.getZ() + 0.5 - displayPos.z;
            float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90f;
            blockDisplay.setYRot(yaw);

            blockDisplay.setXRot(25f);

            serverLevel.addFreshEntity(blockDisplay);

        }
    }
}