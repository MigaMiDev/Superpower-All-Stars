package ttv.migami.spas.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ColdMagmaBlock extends WeakMagmaBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE;
    private static final int NEIGHBORS_TO_AGE = 4;
    private static final int NEIGHBORS_TO_MELT = 2;
    public static final BooleanProperty WATERLOGGED;
    // true Being Water, and false being Lava!

    public ColdMagmaBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        /*if (pEntity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.MAGMA_FRUIT.get())) {
            return;
        } else {
            pEntity.setSecondsOnFire(5);
        }*/

        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, WATERLOGGED);
    }

    static {
        AGE = BlockStateProperties.AGE_3;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}