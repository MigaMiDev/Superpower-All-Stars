package ttv.migami.mdf.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.item.AnimatedBlessedFruitItem;
import ttv.migami.mdf.item.AnimatedFruitItem;
import ttv.migami.mdf.item.DebriVacuumItem;
import ttv.migami.mdf.item.VeggieItem;

import java.util.List;

public class ModItems {

    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> FIREWORK_FRUIT = REGISTER.register("firework_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.FIREWORK_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "firework_fruit")
    );

    public static final RegistryObject<Item> CREEPER_FRUIT = REGISTER.register("creeper_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.COMMON)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.CREEPER_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "creeper_fruit")
    );

    public static final RegistryObject<Item> SKELETON_FRUIT = REGISTER.register("skeleton_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.SKELETON_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "skeleton_fruit")
    );

    public static final RegistryObject<Item> SQUID_FRUIT = REGISTER.register("squid_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.SQUID_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "squid_fruit")
    );

    public static final RegistryObject<Item> BUSTER_FRUIT = REGISTER.register("buster_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.BUSTER_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "buster_fruit")
    );

    public static final RegistryObject<Item> FLOWER_FRUIT = REGISTER.register("flower_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.FLOWER_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "flower_fruit")
    );

    public static final RegistryObject<Item> FIRE_FRUIT = REGISTER.register("fire_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.FIRE_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "fire_fruit")
    );

    public static final RegistryObject<Item> RUBBER_FRUIT = REGISTER.register("rubber_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.RUBBER_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "rubber_fruit")
    );

    public static final RegistryObject<Item> SPIDER_FRUIT = REGISTER.register("spider_fruit",
            () -> new AnimatedFruitItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.SPIDER_FRUIT.get(), (72000 * 5), 0, false, false)
            ), "spider_fruit")
    );

    public static final RegistryObject<Item> BLESSED_FIREWORK_FRUIT = REGISTER.register("blessed_firework_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.FIREWORK_FRUIT.get(), (-1), 0, false, false)
            ), "firework_fruit")
    );

    public static final RegistryObject<Item> BLESSED_CREEPER_FRUIT = REGISTER.register("blessed_creeper_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.CREEPER_FRUIT.get(), -1, 0, false, false)
            ), "creeper_fruit")
    );

    public static final RegistryObject<Item> BLESSED_SKELETON_FRUIT = REGISTER.register("blessed_skeleton_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.SKELETON_FRUIT.get(), -1, 0, false, false)
            ), "skeleton_fruit")
    );

    public static final RegistryObject<Item> BLESSED_SQUID_FRUIT = REGISTER.register("blessed_squid_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.SQUID_FRUIT.get(), -1, 0, false, false)
            ), "squid_fruit")
    );

    public static final RegistryObject<Item> BLESSED_BUSTER_FRUIT = REGISTER.register("blessed_buster_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.BUSTER_FRUIT.get(), -1, 0, false, false)
            ), "buster_fruit")
    );

    public static final RegistryObject<Item> BLESSED_FLOWER_FRUIT = REGISTER.register("blessed_flower_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.FLOWER_FRUIT.get(), -1, 0, false, false)
            ), "flower_fruit")
    );

    public static final RegistryObject<Item> BLESSED_FIRE_FRUIT = REGISTER.register("blessed_fire_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.FIRE_FRUIT.get(), -1, 0, false, false)
            ), "fire_fruit")
    );

    public static final RegistryObject<Item> BLESSED_RUBBER_FRUIT = REGISTER.register("blessed_rubber_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.RUBBER_FRUIT.get(), -1, 0, false, false)
            ), "rubber_fruit")
    );

    public static final RegistryObject<Item> BLESSED_SPIDER_FRUIT = REGISTER.register("blessed_spider_fruit",
            () -> new AnimatedBlessedFruitItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                    new MobEffectInstance(ModEffects.SPIDER_FRUIT.get(), -1, 0, false, false)
            ), "spider_fruit")
    );

    public static final RegistryObject<Item> BROCCOLI_VEGGIE = REGISTER.register("broccoli_veggie",
            () -> new VeggieItem(new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    , List.of(
                    new MobEffectInstance(MobEffects.CONFUSION, 100, 0)
            ))
    );

    public static final RegistryObject<Item> DEBRI_VACUUM = REGISTER.register("debri_vacuum",
            () -> new DebriVacuumItem(new Item.Properties().stacksTo(1)));

}