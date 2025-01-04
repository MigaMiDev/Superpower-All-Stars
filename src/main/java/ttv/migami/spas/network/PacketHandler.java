package ttv.migami.spas.network;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.spas.Reference;
import ttv.migami.spas.network.message.*;

public class PacketHandler
{
    private static FrameworkNetwork playChannel;

    public static void init()
    {
        playChannel = FrameworkAPI.createNetworkBuilder(new ResourceLocation(Reference.MOD_ID, "play"), 1)
                .registerPlayMessage(C2SMessageAction.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SMessageShooting.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SFruitMessage.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SSwapBlessing.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SMessageBlessings.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(S2CMessageUpdateFruits.class, MessageDirection.PLAY_CLIENT_BOUND)
                .build();
    }

    public static FrameworkNetwork getPlayChannel()
    {
        return playChannel;
    }

}
