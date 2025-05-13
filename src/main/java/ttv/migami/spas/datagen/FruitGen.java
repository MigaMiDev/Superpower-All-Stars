package ttv.migami.spas.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.spas.Reference;
import ttv.migami.spas.common.ActionMode;
import ttv.migami.spas.common.ActionType;
import ttv.migami.spas.common.FoodExhaustion;
import ttv.migami.spas.common.Fruit;

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

                // General
                .setSwimDisabled(true)

                // Moves
                .setZAction(ActionType.Z, "info.spas.firework_fruit.z_action",
                        5F, ActionMode.SINGLE, 160, 1,1,
                        true, 0, 0, FoodExhaustion.BIG,
                        -0.5F, 5.0F)

                .setXAction(ActionType.X, "info.spas.firework_fruit.x_action",
                        3F, ActionMode.SINGLE, 200, 1,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        -0.5F, 0.0F)

                .setCAction(ActionType.C, "info.spas.firework_fruit.c_action",
                        2F, ActionMode.HOLD, 250, 5,8,
                        false, 15, 5, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .setVAction(ActionType.V, "info.spas.firework_fruit.v_action",
                        2.5F, ActionMode.HOLD, 300, 4,24,
                        false, 25, 10, FoodExhaustion.SMALL,
                        0.0F, 0.0F)

                .setRAction(ActionType.R, "info.spas.firework_fruit.r_action",
                        3F, ActionMode.SINGLE, 150, 1,1,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "creeper_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(false)

                // Moves
                .setZAction(ActionType.Z, "info.spas.creeper_fruit.z_action",
                        20F, ActionMode.SINGLE, 250, 1,1,
                        true, 0, 0, FoodExhaustion.LARGE,
                        -0.5F, -8.0F)

                .setXAction(ActionType.X, "info.spas.creeper_fruit.x_action",
                        8F, ActionMode.SINGLE, 250, 1,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        -0.5F, 3.0F)

                .setCAction(ActionType.C, "info.spas.creeper_fruit.c_action",
                        0F, ActionMode.SINGLE, 200, 1,1,
                        false, 15, 5, FoodExhaustion.BIG,
                        1.0F, 5.0F)

                .setVAction(ActionType.V, "info.spas.creeper_fruit.v_action",
                        0F, ActionMode.SINGLE, 700, 1,1,
                        false, 25, 10, FoodExhaustion.LARGE,
                        0.0F, -10.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "skeleton_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(false)

                // Moves
                .setZAction(ActionType.Z, "info.spas.skeleton_fruit.z_action",
                        2F, ActionMode.HOLD, 160, 5,5,
                        true, 0, 0, FoodExhaustion.SMALL,
                        0.0F, 0.2F)

                .setXAction(ActionType.X, "info.spas.skeleton_fruit.x_action",
                        2F, ActionMode.SINGLE, 200, 1,2,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.0F, 0.0F)

                .setCAction(ActionType.C, "info.spas.skeleton_fruit.c_action",
                        0F, ActionMode.SINGLE, 400, 1,1,
                        false, 15, 5, FoodExhaustion.BIG,
                        0.0F, 1.0F)

                .setVAction(ActionType.V, "info.spas.skeleton_fruit.v_action",
                        6F, ActionMode.SINGLE, 600, 1,1,
                        false, 25, 10, FoodExhaustion.LARGE,
                        0.0F, 0.5F)

                .setRAction(ActionType.R, "info.spas.skeleton_fruit.r_action",
                        0F, ActionMode.SINGLE, 120, 1,1,
                        false, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "squid_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(false)

                // Moves
                .setZAction(ActionType.Z, "info.spas.squid_fruit.z_action",
                        2F, ActionMode.SINGLE, 100, 20,2,
                        true, 0, 0, FoodExhaustion.SMALL,
                        -0.1F, 0.2F)

                .setXAction(ActionType.X, "info.spas.squid_fruit.x_action",
                        2F, ActionMode.SINGLE, 500, 1,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.3F, 0.0F)

                .setCAction(ActionType.C, "info.spas.squid_fruit.c_action",
                        0F, ActionMode.HOLD, 600, 20,3,
                        false, 15, 5, FoodExhaustion.LARGE,
                        -1.0F, 1.0F)

                .setVAction(ActionType.V, "info.spas.squid_fruit.v_action",
                        6F, ActionMode.HOLD, 800, 3,82,
                        false, 25, 10, FoodExhaustion.NONE,
                        0.0F, 0.5F)

                .setRAction(ActionType.R, "info.spas.squid_fruit.r_action",
                        0F, ActionMode.SINGLE, 800, 1,1,
                        false, 0, 0, FoodExhaustion.LARGE,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "flower_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(true)

                // Moves
                .setZAction(ActionType.Z, "info.spas.flower_fruit.z_action",
                        2F, ActionMode.SINGLE, 100, 20,2,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        -0.5F, 0.2F)

                .setXAction(ActionType.X, "info.spas.flower_fruit.x_action",
                        2F, ActionMode.SINGLE, 500, 1,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.0F, 3.0F)

                .setCAction(ActionType.C, "info.spas.flower_fruit.c_action",
                        0F, ActionMode.HOLD, 600, 20,3,
                        false, 15, 5, FoodExhaustion.LARGE,
                        0.0F, 0.0F)

                .setVAction(ActionType.V, "info.spas.flower_fruit.v_action",
                        6F, ActionMode.HOLD, 800, 3,82,
                        false, 25, 10, FoodExhaustion.SMALL,
                        0.0F, 0.0F)

                .build());

    }
}
