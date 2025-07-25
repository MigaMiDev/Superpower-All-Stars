package ttv.migami.spas.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.fruit.StunEntity;
import ttv.migami.spas.entity.fruit.buster.*;
import ttv.migami.spas.entity.fruit.fire.Fireball;
import ttv.migami.spas.entity.fruit.fire.LargeFireball;
import ttv.migami.spas.entity.fruit.flower.*;
import ttv.migami.spas.entity.fruit.skeleton.Bone;
import ttv.migami.spas.entity.fruit.skeleton.BoneZone;
import ttv.migami.spas.entity.fruit.skeleton.GasterBlaster;
import ttv.migami.spas.entity.fruit.spider.*;
import ttv.migami.spas.entity.fruit.squid.InkSplat;
import ttv.migami.spas.entity.fx.CracksMarkEntity;
import ttv.migami.spas.entity.fx.InkMarkEntity;
import ttv.migami.spas.entity.fx.LargeInkMarkEntity;
import ttv.migami.spas.entity.fx.ScorchMarkEntity;

/**
 * Author: MigaMi
 */
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reference.MOD_ID);

    public static final RegistryObject<EntityType<GasterBlaster>> GASTER_BLASTER = REGISTER.register("gaster_blaster", () -> EntityType.Builder.<GasterBlaster>of(GasterBlaster::new, MobCategory.MISC).sized(1.0F, 1.0F).noSave().noSummon().fireImmune().build("gaster_blaster"));
    public static final RegistryObject<EntityType<Bone>> SMALL_BONE = REGISTER.register("small_bone", () -> EntityType.Builder.<Bone>of(Bone::new, MobCategory.MISC).sized(2.5F, 1.0F).noSave().noSummon().fireImmune().build("small_bone"));
    public static final RegistryObject<EntityType<BoneZone>> BONE_ZONE = REGISTER.register("bone_zone", () -> EntityType.Builder.<BoneZone>of(BoneZone::new, MobCategory.MISC).sized(3.0F, 2.0F).noSave().noSummon().fireImmune().build("bone_zone"));
    public static final RegistryObject<EntityType<InkSplat>> INK_SPLAT = REGISTER.register("ink_splat", () -> EntityType.Builder.<InkSplat>of(InkSplat::new, MobCategory.MISC).sized(2.0F, 2.0F).noSave().noSummon().fireImmune().build("ink_splat"));
    public static final RegistryObject<EntityType<Piano>> PIANO = REGISTER.register("piano", () -> EntityType.Builder.<Piano>of(Piano::new, MobCategory.MISC).sized(3.2F, 0.8F).noSave().noSummon().fireImmune().build("piano"));
    public static final RegistryObject<EntityType<Dynamite>> DYNAMITE = REGISTER.register("dynamite", () -> EntityType.Builder.<Dynamite>of(Dynamite::new, MobCategory.MISC).sized(1.0F, 1.0F).noSave().noSummon().fireImmune().build("dynamite"));
    public static final RegistryObject<EntityType<Buster>> BUSTER = REGISTER.register("buster", () -> EntityType.Builder.<Buster>of(Buster::new, MobCategory.MISC).sized(1.3965F, 1.6F).noSave().build("buster"));
    public static final RegistryObject<EntityType<Lasso>> LASSO = REGISTER.register("lasso", () -> EntityType.Builder.<Lasso>of(Lasso::new, MobCategory.MISC).sized(2.0F, 0.5F).noSave().noSummon().build("lasso"));
    public static final RegistryObject<EntityType<Cactus>> CACTUS = REGISTER.register("cactus", () -> EntityType.Builder.<Cactus>of(Cactus::new, MobCategory.MISC).sized(6.0F, 10.0F).noSave().noSummon().build("cactus"));
    public static final RegistryObject<EntityType<FlowerSpear>> FLOWER_SPEAR = REGISTER.register("flower_spear", () -> EntityType.Builder.<FlowerSpear>of(FlowerSpear::new, MobCategory.MISC).sized(1.0F, 2.0F).noSave().noSummon().build("flower_spear"));
    public static final RegistryObject<EntityType<VineTrap>> VINE_TRAP = REGISTER.register("vine_trap", () -> EntityType.Builder.<VineTrap>of(VineTrap::new, MobCategory.MISC).sized(2.0F, 6.0F).noSave().noSummon().build("vine_trap"));
    public static final RegistryObject<EntityType<PiranhaPlant>> PIRANHA_PLANT = REGISTER.register("piranha_plant", () -> EntityType.Builder.<PiranhaPlant>of(PiranhaPlant::new, MobCategory.MISC).sized(2.0F, 6.0F).noSave().noSummon().build("piranha_plant"));
    public static final RegistryObject<EntityType<Vine>> VINE = REGISTER.register("vine", () -> EntityType.Builder.<Vine>of(Vine::new, MobCategory.MISC).sized(5.0F, 4.0F).noSave().noSummon().build("vine"));
    public static final RegistryObject<EntityType<Petal>> PETAL = REGISTER.register("petal", () -> EntityType.Builder.<Petal>of(Petal::new, MobCategory.MISC).sized(1.3F, 0.5F).noSave().noSummon().build("petal"));
    public static final RegistryObject<EntityType<Fireball>> FIREBALL = REGISTER.register("fireball", () -> EntityType.Builder.<Fireball>of(Fireball::new, MobCategory.MISC).sized(2.0F, 2.0F).noSave().noSummon().fireImmune().build("fireball"));
    public static final RegistryObject<EntityType<LargeFireball>> LARGE_FIREBALL = REGISTER.register("large_fireball", () -> EntityType.Builder.<LargeFireball>of(LargeFireball::new, MobCategory.MISC).sized(4.5F, 4.5F).noSave().noSummon().fireImmune().build("large_fireball"));
    public static final RegistryObject<EntityType<SpiderFang>> SPIDER_FANG = REGISTER.register("spider_fang", () -> EntityType.Builder.<SpiderFang>of(SpiderFang::new, MobCategory.MISC).sized(2.5F, 4.0F).noSave().noSummon().build("spider_fang"));
    public static final RegistryObject<EntityType<LeftSpiderString>> LEFT_SPIDER_STRING = REGISTER.register("left_spider_string", () -> EntityType.Builder.<LeftSpiderString>of(LeftSpiderString::new, MobCategory.MISC).sized(3.5F, 2.5F).noSave().noSummon().build("left_spider_string"));
    public static final RegistryObject<EntityType<RightSpiderString>> RIGHT_SPIDER_STRING = REGISTER.register("right_spider_string", () -> EntityType.Builder.<RightSpiderString>of(RightSpiderString::new, MobCategory.MISC).sized(3.5F, 2.5F).noSave().noSummon().build("right_spider_string"));
    public static final RegistryObject<EntityType<FireSpiderString>> FIRE_SPIDER_STRING = REGISTER.register("fire_spider_string", () -> EntityType.Builder.<FireSpiderString>of(FireSpiderString::new, MobCategory.MISC).sized(5.5F, 2.5F).noSave().noSummon().build("fire_spider_string"));
    public static final RegistryObject<EntityType<StringRing>> STRING_RING = REGISTER.register("string_ring", () -> EntityType.Builder.<StringRing>of(StringRing::new, MobCategory.MISC).sized(22F, 3F).noSave().noSummon().build("string_ring"));
    public static final RegistryObject<EntityType<TeaCup>> TEA_CUP = REGISTER.register("tea_cup", () -> EntityType.Builder.<TeaCup>of(TeaCup::new, MobCategory.MISC).sized(2F, 2F).noSave().noSummon().build("tea_cup"));
    public static final RegistryObject<EntityType<TeaKettle>> TEA_KETTLE = REGISTER.register("tea_kettle", () -> EntityType.Builder.<TeaKettle>of(TeaKettle::new, MobCategory.MISC).sized(2F, 2F).noSave().noSummon().build("tea_kettle"));
    public static final RegistryObject<EntityType<Croissant>> CROISSANT = REGISTER.register("croissant", () -> EntityType.Builder.<Croissant>of(Croissant::new, MobCategory.MISC).sized(2F, 2F).noSave().noSummon().build("croissant"));

    public static final RegistryObject<EntityType<StunEntity>> STUN_ENTITY = REGISTER.register("stun_entity", () -> EntityType.Builder.<StunEntity>of(StunEntity::new, MobCategory.MISC).sized(4.0F, 4.0F).fireImmune().noSave().noSummon().build("stun_entity"));
    public static final RegistryObject<EntityType<ScorchMarkEntity>> SCORCH_MARK = REGISTER.register("scorch_mark", () -> EntityType.Builder.<ScorchMarkEntity>of(ScorchMarkEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon().build("scorch_mark"));
    public static final RegistryObject<EntityType<InkMarkEntity>> INK_MARK = REGISTER.register("ink_mark", () -> EntityType.Builder.<InkMarkEntity>of(InkMarkEntity::new, MobCategory.MISC).sized(3.0F, 1.0F).noSave().noSummon().build("ink_mark"));
    public static final RegistryObject<EntityType<LargeInkMarkEntity>> LARGE_INK_MARK = REGISTER.register("large_ink_mark", () -> EntityType.Builder.<LargeInkMarkEntity>of(LargeInkMarkEntity::new, MobCategory.MISC).sized(11.0F, 1.0F).noSave().noSummon().build("large_ink_mark"));
    public static final RegistryObject<EntityType<CracksMarkEntity>> CRACKS_MARK = REGISTER.register("cracks_mark", () -> EntityType.Builder.<CracksMarkEntity>of(CracksMarkEntity::new, MobCategory.MISC).sized(11.0F, 1.0F).fireImmune().noSave().noSummon().build("cracks_mark"));
}
