package ttv.migami.spas.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ttv.migami.spas.Reference;
import ttv.migami.spas.block.ColdMagmaBlock;
import ttv.migami.spas.block.WeakMagmaBlock;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> COLD_MAGMA_BLOCK = register("cold_magma_block",
            () -> new ColdMagmaBlock(BlockBehaviour.Properties.copy(Blocks.MAGMA_BLOCK)
                    .strength(0.5F)));

    public static final RegistryObject<Block> WEAK_MAGMA_BLOCK = register("weak_magma_block",
            () -> new WeakMagmaBlock(BlockBehaviour.Properties.copy(Blocks.MAGMA_BLOCK)
                    .strength(0.5F)));

    private static <T extends Block> RegistryObject<T> register(String id, Supplier<T> blockSupplier) {
        return register(id, blockSupplier, block1 -> new BlockItem(block1, new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> register(String id, Supplier<T> blockSupplier, @Nullable Function<T, BlockItem> supplier) {
        RegistryObject<T> registryObject = REGISTER.register(id, blockSupplier);
        if (supplier != null) {
            ModItems.REGISTER.register(id, () -> supplier.apply(registryObject.get()));
        }
        return registryObject;
    }
}