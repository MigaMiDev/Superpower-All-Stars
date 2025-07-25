package ttv.migami.spas.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.spas.common.network.ServerPlayHandler;

public class C2SMessageExplodePlayer extends PlayMessage<C2SMessageExplodePlayer>
{
    public C2SMessageExplodePlayer() {}

    @Override
    public void encode(C2SMessageExplodePlayer message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessageExplodePlayer decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageExplodePlayer();
    }

    @Override
    public void handle(C2SMessageExplodePlayer message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null)
            {
                ServerPlayHandler.explodePlayer(player);
            }
        });
        context.setHandled(true);
    }
}