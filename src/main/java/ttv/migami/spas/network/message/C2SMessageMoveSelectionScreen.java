package ttv.migami.spas.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.spas.common.network.ServerPlayHandler;

public class C2SMessageMoveSelectionScreen extends PlayMessage<C2SMessageMoveSelectionScreen>
{
    public C2SMessageMoveSelectionScreen() {}

    @Override
    public void encode(C2SMessageMoveSelectionScreen message, FriendlyByteBuf buffer) {}

    @Override
    public C2SMessageMoveSelectionScreen decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageMoveSelectionScreen();
    }

    @Override
    public void handle(C2SMessageMoveSelectionScreen message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null)
            {
                ServerPlayHandler.handleMoveSelectionScreen(player);
            }
        });
        context.setHandled(true);
    }
}