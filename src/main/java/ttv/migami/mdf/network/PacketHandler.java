package ttv.migami.mdf.network;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.network.message.*;

public class PacketHandler
{
    private static FrameworkNetwork playChannel;

    public static void init()
    {
        playChannel = FrameworkAPI.createNetworkBuilder(new ResourceLocation(Reference.MOD_ID, "play"), 1)
                .registerPlayMessage(C2SFruitMessage.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SSwapBlessing.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SMessageBlessings.class, MessageDirection.PLAY_SERVER_BOUND)
                .build();
    }

    public static FrameworkNetwork getPlayChannel()
    {
        return playChannel;
    }

}
