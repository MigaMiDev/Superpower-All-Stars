package ttv.migami.spas.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nullable;

public class WeakMagmaBlock extends MagmaBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE;
    private static final int NEIGHBORS_TO_AGE = 4;
    private static final int NEIGHBORS_TO_MELT = 2;
    public static final BooleanProperty WATERLOGGED;
    // true Being Water, and false being Lava!

    public WeakMagmaBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(WATERLOGGED, false));
    }

    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.tick(pState, pLevel, pPos, pRandom);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if ((pRandom.nextInt(3) == 0 || this.fewerNeigboursThan(pLevel, pPos, 4)) && pLevel.getMaxLocalRawBrightness(pPos) > 11 - (Integer)pState.getValue(AGE) - pState.getLightBlock(pLevel, pPos) && this.slightlyEvaporate(pState, pLevel, pPos)) {
            BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Direction $$5 = var6[var8];
                $$4.setWithOffset(pPos, $$5);
                BlockState $$6 = pLevel.getBlockState($$4);
                if ($$6.is(this) && !this.slightlyEvaporate($$6, pLevel, $$4)) {
                    pLevel.scheduleTick($$4, this, Mth.nextInt(pRandom, 20, 40));
                }
            }

        } else {
            pLevel.scheduleTick(pPos, this, Mth.nextInt(pRandom, 20, 40));
        }
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        pLevel.setBlockAndUpdate(pPos, evaporatesInto(pState));
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        /*if (pEntity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.MAGMA_FRUIT.get())) {
            return;
        }*/

        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    private boolean slightlyEvaporate(BlockState pState, Level pLevel, BlockPos pPos) {
        int $$3 = (Integer)pState.getValue(AGE);
        if ($$3 < 2) {
            pLevel.setBlock(pPos, (BlockState)pState.setValue(AGE, $$3 + 1), 2);
            return false;
        } else {
            this.evaporate(pState, pLevel, pPos);
            return true;
        }
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (pBlock.defaultBlockState().is(this) && this.fewerNeigboursThan(pLevel, pPos, 2)) {
            this.evaporate(pState, pLevel, pPos);
        }

        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    private boolean fewerNeigboursThan(BlockGetter pLevel, BlockPos pPos, int pNeighborsRequired) {
        int $$3 = 0;
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        Direction[] var6 = Direction.values();
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction $$5 = var6[var8];
            $$4.setWithOffset(pPos, $$5);
            if (pLevel.getBlockState($$4).is(this)) {
                ++$$3;
                if ($$3 >= pNeighborsRequired) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, WATERLOGGED);
    }

    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return ItemStack.EMPTY;
    }

    static {
        AGE = BlockStateProperties.AGE_3;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }

    protected void evaporate(BlockState pState, Level pLevel, BlockPos pPos) {
        pLevel.setBlockAndUpdate(pPos, evaporatesInto(pState));
        pLevel.neighborChanged(pPos, evaporatesInto(pState).getBlock(), pPos);
    }

    public static BlockState evaporatesInto(BlockState state) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            if (state.getValue(BlockStateProperties.WATERLOGGED)) {
                return Blocks.WATER.defaultBlockState();
            } else {
                return Blocks.LAVA.defaultBlockState();
            }
        }
        return Blocks.LAVA.defaultBlockState();
    }
}