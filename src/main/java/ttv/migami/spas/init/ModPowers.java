package ttv.migami.spas.init;

import net.minecraft.resources.ResourceLocation;
import ttv.migami.spas.Reference;
import ttv.migami.spas.common.FruitHandler;
import ttv.migami.spas.common.network.fruit.*;

import java.util.HashMap;
import java.util.Map;

public class ModPowers {
    private static final Map<ResourceLocation, FruitHandler> POWER = new HashMap<>();

    public static void register(ResourceLocation effectId, FruitHandler handler) {
        POWER.put(effectId, handler);
    }

    public static void registerPowerHandlers() {
        register(new ResourceLocation(Reference.MOD_ID, "firework_fruit"), FireworkFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "creeper_fruit"), CreeperFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "skeleton_fruit"), SkeletonFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "squid_fruit"), SquidFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "buster_fruit"), BusterFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "flower_fruit"), FlowerFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "fire_fruit"), FireFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "rubber_fruit"), RubberFruitHandler::moveHandler);
        register(new ResourceLocation(Reference.MOD_ID, "spider_fruit"), SpiderFruitHandler::moveHandler);
    }

    public static FruitHandler getHandler(ResourceLocation effectId) {
        return POWER.get(effectId);
    }
}