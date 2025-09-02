package ttv.migami.spas.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ttv.migami.spas.Reference;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.effect.IncurableEffect;
import ttv.migami.spas.effect.fruit.*;

/**
 * Author: MigaMi
 */
public class ModEffects
{
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Reference.MOD_ID);

    public static final RegistryObject<IncurableEffect> STUNNED = REGISTER.register("stunned", () -> new IncurableEffect(MobEffectCategory.HARMFUL, 0));
    public static final RegistryObject<IncurableEffect> POWER = REGISTER.register("power", () -> new IncurableEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<IncurableEffect> FEATHER_FALLING = REGISTER.register("feather_falling", () -> new IncurableEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<IncurableEffect> SLINGSHOT = REGISTER.register("slingshot", () -> new IncurableEffect(MobEffectCategory.BENEFICIAL, 0));

    public static final RegistryObject<FruitEffect> FIREWORK_FRUIT = REGISTER.register("firework_fruit", () -> new FireworkFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> CREEPER_FRUIT = REGISTER.register("creeper_fruit", () -> new CreeperFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> SKELETON_FRUIT = REGISTER.register("skeleton_fruit", () -> new SkeletonFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> SQUID_FRUIT = REGISTER.register("squid_fruit", () -> new SquidFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> BUSTER_FRUIT = REGISTER.register("buster_fruit", () -> new BusterFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> FLOWER_FRUIT = REGISTER.register("flower_fruit", () -> new FlowerFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> FIRE_FRUIT = REGISTER.register("fire_fruit", () -> new FireFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> RUBBER_FRUIT = REGISTER.register("rubber_fruit", () -> new RubberFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistryObject<FruitEffect> SPIDER_FRUIT = REGISTER.register("spider_fruit", () -> new SpiderFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    //TODO: Do CTRL-Shift-F "Magma Fruit"
    //public static final RegistryObject<FruitEffect> MAGMA_FRUIT = REGISTER.register("magma_fruit", () -> new MagmaFruitEffect(MobEffectCategory.BENEFICIAL, 0));
    //public static final RegistryObject<FruitEffect> ICE_FRUIT = REGISTER.register("ice_fruit", () -> new IceFruitEffect(MobEffectCategory.BENEFICIAL, 0));

}
