package ttv.migami.spas.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FruitEventBus {

    @SubscribeEvent
    public static void preShoot(FruitFireEvent.Pre event)
    {
    }

    @SubscribeEvent
    public static void postShoot(FruitFireEvent.Post event)
    {
    }
}