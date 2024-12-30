package ttv.migami.mdf.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import ttv.migami.mdf.DevilFruits;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.capanility.SyncFruitEffectCapabilityPacket;
import ttv.migami.mdf.init.ModCapabilities;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PlayerLoginHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(cap -> {
            DevilFruits.LOGGER.atInfo().log("Capability initialized for: " + event.getEntity().getName().getString());
        });

        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(cap -> {
            ModNetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncFruitEffectCapabilityPacket(cap.getFruitEffects()));
        });
    }
}