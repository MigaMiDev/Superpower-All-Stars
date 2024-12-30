package ttv.migami.mdf.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.mdf.common.network.ServerPlayHandler;

public class C2SMessageBlessings extends PlayMessage<C2SMessageBlessings>
{
    public C2SMessageBlessings() {}

    @Override
    public void encode(C2SMessageBlessings message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessageBlessings decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageBlessings();
    }

    @Override
    public void handle(C2SMessageBlessings message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null)
            {
                ServerPlayHandler.handleBlessings(player);
            }
        });
        context.setHandled(true);
    }
}