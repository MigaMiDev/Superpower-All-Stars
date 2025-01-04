package ttv.migami.spas.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.common.network.ServerPlayHandler;

public class C2SSwapBlessing extends PlayMessage<C2SSwapBlessing>
{
    private int fruit;

    public C2SSwapBlessing() {}

    public C2SSwapBlessing(int fruit)
    {
        this.fruit = fruit;
    }

    @Override
    public void encode(C2SSwapBlessing message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.fruit);
    }

    @Override
    public C2SSwapBlessing decode(FriendlyByteBuf buffer)
    {
        return new C2SSwapBlessing(buffer.readInt());
    }

    @Override
    public void handle(C2SSwapBlessing message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null && !player.isSpectator())
            {
                FruitDataHandler.clearCurrentEffect(player);
                ServerPlayHandler.swapBlessing(player, message.fruit);
            }
        });
        context.setHandled(true);
    }
}
