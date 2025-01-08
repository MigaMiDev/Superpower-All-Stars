package ttv.migami.spas.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.spas.common.network.ServerPlayHandler;

public class C2SMessagePermanentFruitsScreen extends PlayMessage<C2SMessagePermanentFruitsScreen>
{
    public C2SMessagePermanentFruitsScreen() {}

    @Override
    public void encode(C2SMessagePermanentFruitsScreen message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessagePermanentFruitsScreen decode(FriendlyByteBuf buffer)
    {
        return new C2SMessagePermanentFruitsScreen();
    }

    @Override
    public void handle(C2SMessagePermanentFruitsScreen message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null)
            {
                ServerPlayHandler.handlePermanentFruitsScreen(player);
            }
        });
        context.setHandled(true);
    }
}