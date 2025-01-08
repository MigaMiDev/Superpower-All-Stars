package ttv.migami.spas.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.spas.common.network.ServerPlayHandler;

public class C2SMessageFruitScreen extends PlayMessage<C2SMessageFruitScreen>
{
    public C2SMessageFruitScreen() {}

    @Override
    public void encode(C2SMessageFruitScreen message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessageFruitScreen decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageFruitScreen();
    }

    @Override
    public void handle(C2SMessageFruitScreen message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null)
            {
                ServerPlayHandler.handleFruitScreen(player);
            }
        });
        context.setHandled(true);
    }
}