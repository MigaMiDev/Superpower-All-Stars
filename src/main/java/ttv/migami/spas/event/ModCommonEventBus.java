package ttv.migami.spas.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.fruit.buster.Buster;
import ttv.migami.spas.entity.fruit.flower.FlowerSpear;
import ttv.migami.spas.entity.fruit.flower.Vine;
import ttv.migami.spas.entity.fruit.skeleton.BoneZone;
import ttv.migami.spas.entity.fruit.skeleton.GasterBlaster;
import ttv.migami.spas.entity.fruit.spider.SpiderFang;
import ttv.migami.spas.init.ModEntities;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEventBus {

    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GASTER_BLASTER.get(), GasterBlaster.createAttributes().build());
        event.put(ModEntities.BONE_ZONE.get(), BoneZone.createAttributes().build());
        event.put(ModEntities.BUSTER.get(), Buster.createAttributes().build());
        event.put(ModEntities.FLOWER_SPEAR.get(), FlowerSpear.createAttributes().build());
        event.put(ModEntities.VINE.get(), Vine.createAttributes().build());
        event.put(ModEntities.SPIDER_FANG.get(), SpiderFang.createAttributes().build());
    }

}
