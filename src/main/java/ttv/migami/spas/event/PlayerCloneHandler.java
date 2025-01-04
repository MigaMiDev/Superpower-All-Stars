package ttv.migami.spas.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Reference;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.common.network.ServerPlayHandler;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PlayerCloneHandler {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();

        if (oldData.contains(FruitDataHandler.CURRENT_EFFECT_KEY)) {
            newData.putInt(FruitDataHandler.CURRENT_EFFECT_KEY,
                    oldData.getInt(FruitDataHandler.CURRENT_EFFECT_KEY));
        }

        if (oldData.contains(FruitDataHandler.PREVIOUS_EFFECTS_KEY)) {
            newData.put(FruitDataHandler.PREVIOUS_EFFECTS_KEY,
                    oldData.get(FruitDataHandler.PREVIOUS_EFFECTS_KEY));
        }

        if (!event.getEntity().level().isClientSide) {
            ServerPlayHandler.syncToClient((ServerPlayer) event.getEntity());
        }
    }
}