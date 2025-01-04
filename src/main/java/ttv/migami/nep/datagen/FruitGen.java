package ttv.migami.nep.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.nep.Reference;
import ttv.migami.nep.common.ActionMode;
import ttv.migami.nep.common.Fruit;

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

                .setZAction(Component.translatable("info.nep.firework_fruit.z_action"),
                        5, ActionMode.SINGLE, 160, 1,1,
                        0.0F, 0.0F, 5.0F)

                .setXAction(Component.translatable("info.nep.firework_fruit.x_action"),
                        5, ActionMode.SINGLE, 200, 1,1,
                        0.0F, 0.0F, 0.0F)

                .setCAction(Component.translatable("info.nep.firework_fruit.c_action"),
                        5, ActionMode.HOLD, 250, 5,1,
                        0.0F, 0.0F, 0.0F)

                .setVAction(Component.translatable("info.nep.firework_fruit.v_action"),
                        5, ActionMode.HOLD, 300, 4,24,
                        0.0F, 0.0F, 0.0F)

                .setRAction(Component.translatable("info.nep.firework_fruit.r_action"),
                        5, ActionMode.SINGLE, 150, 0,1,
                        0.0F, 0.0F, 0.0F)

                .build());

    }
}
