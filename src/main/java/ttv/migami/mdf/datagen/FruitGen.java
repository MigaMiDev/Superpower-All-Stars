package ttv.migami.mdf.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.common.Fruit;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MigaMi
 */
public class FruitGen extends FruitProvider
{
    public FruitGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries);
    }

    @Override
    protected void registerFruits()
    {

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "firework_fruit"), Fruit.Builder.create()

                // Z-Move
                .setZMoveValues(5, true, 160, 0, 1)
                .setXMoveValues(5, true, 200, 0, 1)
                .setCMoveValues(5, true, 250, 5, 8)
                .setVMoveValues(5, true, 300, 4, 24)
                .setFMoveValues(5, false, 150, 0, 1)

                .build());

    }
}
