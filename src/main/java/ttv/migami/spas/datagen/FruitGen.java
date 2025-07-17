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
                .setMoveA(ActionType.MOVE_A, "info.spas.firework_fruit.move_a",
                        5F, ActionMode.PRESS, 160, 1,1,
                        true, 0, 0, FoodExhaustion.BIG,
                        -0.5F, 5.0F)

                .setMoveB(ActionType.MOVE_B, "info.spas.firework_fruit.move_b",
                        3F, ActionMode.PRESS, 200, 1,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        -0.5F, 0.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.firework_fruit.special",
                        2F, ActionMode.HOLD, 250, 5,8,
                        false, 15, 5, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.firework_fruit.ultimate",
                        2.5F, ActionMode.HOLD, 300, 4,24,
                        false, 25, 10, FoodExhaustion.SMALL,
                        0.0F, 0.0F)

                .setMobility(ActionType.MOBILITY, "info.spas.firework_fruit.mobility",
                        3F, ActionMode.PRESS, 150, 1,1,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "creeper_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(false)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.creeper_fruit.move_a",
                        20F, ActionMode.PRESS, 250, 1,1,
                        true, 0, 0, FoodExhaustion.LARGE,
                        -0.5F, -8.0F)

                .setMoveB(ActionType.MOVE_B, "info.spas.creeper_fruit.move_b",
                        8F, ActionMode.PRESS, 250, 1,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        -0.5F, 3.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.creeper_fruit.special",
                        0F, ActionMode.PRESS, 200, 1,1,
                        false, 15, 5, FoodExhaustion.BIG,
                        1.0F, 5.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.creeper_fruit.ultimate",
                        0F, ActionMode.PRESS, 700, 1,1,
                        false, 25, 10, FoodExhaustion.LARGE,
                        0.0F, -10.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "skeleton_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(false)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.skeleton_fruit.move_a",
                        2F, ActionMode.HOLD, 160, 5,5,
                        true, 0, 0, FoodExhaustion.SMALL,
                        0.0F, 0.2F)

                .setMoveB(ActionType.MOVE_B, "info.spas.skeleton_fruit.move_b",
                        2F, ActionMode.PRESS, 200, 1,2,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.0F, 0.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.skeleton_fruit.special",
                        0F, ActionMode.PRESS, 400, 1,1,
                        false, 15, 5, FoodExhaustion.BIG,
                        0.0F, 1.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.skeleton_fruit.ultimate",
                        6F, ActionMode.PRESS, 600, 1,1,
                        false, 25, 10, FoodExhaustion.LARGE,
                        0.0F, 0.5F)

                .setMobility(ActionType.MOBILITY, "info.spas.skeleton_fruit.mobility",
                        0F, ActionMode.PRESS, 120, 1,1,
                        false, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "squid_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(false)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.squid_fruit.move_a",
                        3F, ActionMode.HOLD, 160, 2,8,
                        true, 0, 0, FoodExhaustion.SMALL,
                        -0.1F, 0.2F)

                .setMoveB(ActionType.MOVE_B, "info.spas.squid_fruit.move_b",
                        3F, ActionMode.PRESS, 200, 20,3,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.3F, 0.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.squid_fruit.special",
                        10F, ActionMode.PRESS, 250, 40,2,
                        false, 15, 5, FoodExhaustion.LARGE,
                        -1.0F, 1.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.squid_fruit.ultimate",
                        0.4F, ActionMode.HOLD, 300, 2,24,
                        false, 25, 10, FoodExhaustion.NONE,
                        0.0F, 0.5F)

                .setMobility(ActionType.MOBILITY, "info.spas.squid_fruit.mobility",
                        0F, ActionMode.PRESS, 150, 1,1,
                        false, 0, 0, FoodExhaustion.LARGE,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "buster_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(false)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.buster_fruit.move_a",
                        2F, ActionMode.HOLD, 100, 2,6,
                        true, 0, 0, FoodExhaustion.BIG,
                        -0.2F, 2.5F)

                .setMoveB(ActionType.MOVE_B, "info.spas.buster_fruit.move_b",
                        15F, ActionMode.PRESS, 300, 0,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        -0.5F, 3.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.buster_fruit.special",
                        5F, ActionMode.PRESS, 550, 0,1,
                        false, 15, 5, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.buster_fruit.ultimate",
                        7F, ActionMode.HOLD, 300, 0,1,
                        false, 25, 10, FoodExhaustion.SMALL,
                        0.5F, -2.0F)

                .setMobility(ActionType.MOBILITY, "info.spas.buster_fruit.mobility",
                        0F, ActionMode.PRESS, 800, 0,1,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "flower_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(true)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.flower_fruit.move_a",
                        4F, ActionMode.PRESS, 100, 20,2,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        -0.5F, 0.2F)

                .setMoveB(ActionType.MOVE_B, "info.spas.flower_fruit.move_b",
                        3F, ActionMode.PRESS, 500, 0,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.0F, 3.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.flower_fruit.special",
                        3F, ActionMode.HOLD, 600, 20,3,
                        false, 15, 5, FoodExhaustion.LARGE,
                        0.0F, 0.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.flower_fruit.ultimate",
                        5F, ActionMode.HOLD, 800, 3,82,
                        false, 25, 10, FoodExhaustion.SMALL,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "fire_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(true)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.fire_fruit.move_a",
                        1.8F, ActionMode.HOLD, 200, 2,16,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        -0.2F, 0.2F)

                .setMoveB(ActionType.MOVE_B, "info.spas.fire_fruit.move_b",
                        4F, ActionMode.PRESS, 250, 0,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.0F, 3.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.fire_fruit.special",
                        5F, ActionMode.PRESS, 350, 0,1,
                        false, 15, 5, FoodExhaustion.LARGE,
                        0.0F, 3.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.fire_fruit.ultimate",
                        10F, ActionMode.PRESS, 300, 0,1,
                        false, 25, 10, FoodExhaustion.SMALL,
                        -3.0F, -3.0F)

                .setMobility(ActionType.MOBILITY, "info.spas.fire_fruit.mobility",
                        2F, ActionMode.PRESS, 150, 10,3,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "rubber_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(true)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.rubber_fruit.move_a",
                        5F, ActionMode.PRESS, 200, 30,2,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        -0.2F, 1.7F)

                .setMoveB(ActionType.MOVE_B, "info.spas.rubber_fruit.move_b",
                        15F, ActionMode.PRESS, 250, 0,1,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.0F, -2.5F)

                .setSpecial(ActionType.SPECIAL, "info.spas.rubber_fruit.special",
                        0.2F, ActionMode.HOLD, 350, 2,30,
                        false, 15, 5, FoodExhaustion.LARGE,
                        0.0F, 0.0F)

                // Gear Shift coming soon!

                .setMobility(ActionType.MOBILITY, "info.spas.rubber_fruit.mobility",
                        3F, ActionMode.PRESS, 200, 50,2,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 0.0F)

                .build());

        this.addFruit(new ResourceLocation(Reference.MOD_ID, "spider_fruit"), Fruit.Builder.create()

                // General
                .setSwimDisabled(true)

                // Moves
                .setMoveA(ActionType.MOVE_A, "info.spas.spider_fruit.move_a",
                        2F, ActionMode.PRESS, 120, 3,3,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        -0.2F, 1.7F)

                .setMoveB(ActionType.MOVE_B, "info.spas.spider_fruit.move_b",
                        2F, ActionMode.HOLD, 220, 3,8,
                        false, 5, 3, FoodExhaustion.BIG,
                        0.0F, 2.0F)

                .setSpecial(ActionType.SPECIAL, "info.spas.spider_fruit.special",
                        3F, ActionMode.HOLD, 400, 5,20,
                        false, 15, 5, FoodExhaustion.LARGE,
                        0.0F, 0.0F)

                .setUltimate(ActionType.ULTIMATE, "info.spas.spider_fruit.ultimate",
                        5F, ActionMode.PRESS, 450, 0,1,
                        false, 25, 10, FoodExhaustion.SMALL,
                        0.0F, 2.5F)

                .setMobility(ActionType.MOBILITY, "info.spas.spider_fruit.mobility",
                        0F, ActionMode.PRESS, 35, 0,1,
                        true, 0, 0, FoodExhaustion.MEDIUM,
                        0.0F, 1.0F)

                .build());

    }
}
