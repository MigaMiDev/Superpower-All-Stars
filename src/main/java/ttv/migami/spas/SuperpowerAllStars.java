package ttv.migami.spas;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;
import ttv.migami.spas.client.ClientHandler;
import ttv.migami.spas.client.CustomFruitManager;
import ttv.migami.spas.client.KeyBinds;
import ttv.migami.spas.client.MetaLoader;
import ttv.migami.spas.common.NetworkFruitManager;
import ttv.migami.spas.datagen.FruitGen;
import ttv.migami.spas.entity.ai.FruitAI;
import ttv.migami.spas.entity.ai.fruit.skeleton.SkeletonFruitAI;
import ttv.migami.spas.entity.client.StunEntityRenderer;
import ttv.migami.spas.entity.client.effect.CracksMarkRenderer;
import ttv.migami.spas.entity.client.effect.InkMarkRenderer;
import ttv.migami.spas.entity.client.effect.LargeInkMarkRenderer;
import ttv.migami.spas.entity.client.effect.ScorchMarkRenderer;
import ttv.migami.spas.entity.client.fruit.buster.CactusGeoRenderer;
import ttv.migami.spas.entity.client.fruit.buster.CustomLassoRenderer;
import ttv.migami.spas.entity.client.fruit.buster.DynamiteRenderer;
import ttv.migami.spas.entity.client.fruit.buster.PianoRenderer;
import ttv.migami.spas.entity.client.fruit.fire.FireballRenderer;
import ttv.migami.spas.entity.client.fruit.fire.LargeFireballRenderer;
import ttv.migami.spas.entity.client.fruit.flower.*;
import ttv.migami.spas.entity.client.fruit.skeleton.BoneZoneGeoRenderer;
import ttv.migami.spas.entity.client.fruit.skeleton.GasterBlasterGeoRenderer;
import ttv.migami.spas.entity.client.fruit.skeleton.SmallBoneRenderer;
import ttv.migami.spas.entity.client.fruit.spider.*;
import ttv.migami.spas.entity.client.fruit.squid.InkSplatRenderer;
import ttv.migami.spas.init.*;
import ttv.migami.spas.network.PacketHandler;
import ttv.migami.spas.network.persistent.ModNetworking;
import ttv.migami.spas.world.loot.ModLootModifiers;

import java.util.concurrent.CompletableFuture;

@Mod(Reference.MOD_ID)
public class SuperpowerAllStars {
    public static boolean debugging = false;
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    public static boolean jegLoaded = false;
    public static boolean playerReviveLoaded = false;

    public SuperpowerAllStars() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
        ModEffects.REGISTER.register(bus);
        ModEnchantments.REGISTER.register(bus);
        ModItems.REGISTER.register(bus);
        ModEntities.REGISTER.register(bus);
        ModContainers.REGISTER.register(bus);
        ModParticleTypes.REGISTER.register(bus);
        ModSounds.REGISTER.register(bus);
        ModLootModifiers.register(bus);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onGatherData);

        // OooOoOh spooky!
        GeckoLib.initialize();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FrameworkClientAPI.registerDataLoader(MetaLoader.getInstance());
            ClientHandler.onRegisterCreativeTab(bus);
            bus.addListener(KeyBinds::registerKeyMappings);
        });
        MinecraftForge.EVENT_BUS.register(new FruitAI());
        MinecraftForge.EVENT_BUS.register(new SkeletonFruitAI());
        jegLoaded = ModList.get().isLoaded("jeg");
        playerReviveLoaded = ModList.get().isLoaded("playerrevive");
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientHandler::setup);
        EntityRenderers.register(ModEntities.GASTER_BLASTER.get(), GasterBlasterGeoRenderer::new);
        EntityRenderers.register(ModEntities.SMALL_BONE.get(), SmallBoneRenderer::new);
        EntityRenderers.register(ModEntities.BONE_ZONE.get(), BoneZoneGeoRenderer::new);
        EntityRenderers.register(ModEntities.INK_SPLAT.get(), InkSplatRenderer::new);
        EntityRenderers.register(ModEntities.PIANO.get(), PianoRenderer::new);
        EntityRenderers.register(ModEntities.DYNAMITE.get(), DynamiteRenderer::new);
        EntityRenderers.register(ModEntities.BUSTER.get(), HorseRenderer::new);
        EntityRenderers.register(ModEntities.LASSO.get(), CustomLassoRenderer::new);
        EntityRenderers.register(ModEntities.CACTUS.get(), CactusGeoRenderer::new);
        EntityRenderers.register(ModEntities.FLOWER_SPEAR.get(), FlowerSpearGeoRenderer::new);
        EntityRenderers.register(ModEntities.VINE_TRAP.get(), VineTrapGeoRenderer::new);
        EntityRenderers.register(ModEntities.PIRANHA_PLANT.get(), PiranhaPlantGeoRenderer::new);
        EntityRenderers.register(ModEntities.VINE.get(), VineGeoRenderer::new);
        EntityRenderers.register(ModEntities.PETAL.get(), PetalRenderer::new);
        EntityRenderers.register(ModEntities.FIREBALL.get(), FireballRenderer::new);
        EntityRenderers.register(ModEntities.LARGE_FIREBALL.get(), LargeFireballRenderer::new);
        EntityRenderers.register(ModEntities.SPIDER_FANG.get(), SpiderFangGeoRenderer::new);
        EntityRenderers.register(ModEntities.LEFT_SPIDER_STRING.get(), SpiderStringLeftRenderer::new);
        EntityRenderers.register(ModEntities.RIGHT_SPIDER_STRING.get(), SpiderStringRightRenderer::new);
        EntityRenderers.register(ModEntities.FIRE_SPIDER_STRING.get(), SpiderStringFireRenderer::new);
        EntityRenderers.register(ModEntities.STRING_RING.get(), StringRingRenderer::new);
        EntityRenderers.register(ModEntities.TEA_CUP.get(), TeaCupRenderer::new);
        EntityRenderers.register(ModEntities.TEA_KETTLE.get(), TeaKettleRenderer::new);
        EntityRenderers.register(ModEntities.CROISSANT.get(), CroissantRenderer::new);

        EntityRenderers.register(ModEntities.STUN_ENTITY.get(), StunEntityRenderer::new);
        EntityRenderers.register(ModEntities.SCORCH_MARK.get(), ScorchMarkRenderer::new);
        EntityRenderers.register(ModEntities.INK_MARK.get(), InkMarkRenderer::new);
        EntityRenderers.register(ModEntities.LARGE_INK_MARK.get(), LargeInkMarkRenderer::new);
        EntityRenderers.register(ModEntities.CRACKS_MARK.get(), CracksMarkRenderer::new);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
        {
            PacketHandler.init();
            ModNetworking.register();
            FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.SHOOTING);
            FrameworkAPI.registerLoginData(new ResourceLocation(Reference.MOD_ID, "network_fruit_manager"), NetworkFruitManager.LoginData::new);
            FrameworkAPI.registerLoginData(new ResourceLocation(Reference.MOD_ID, "custom_fruit_manager"), CustomFruitManager.LoginData::new);
            ModPowers.registerPowerHandlers();
        });
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new FruitGen(output, lookupProvider));
    }

}
