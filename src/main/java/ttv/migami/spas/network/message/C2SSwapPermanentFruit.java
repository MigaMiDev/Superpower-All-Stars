package ttv.migami.spas.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.common.network.ServerPlayHandler;

public class C2SSwapPermanentFruit extends PlayMessage<C2SSwapPermanentFruit>
{
    private int fruit;

    public C2SSwapPermanentFruit() {}

    public C2SSwapPermanentFruit(int fruit)
    {
        this.fruit = fruit;
    }

    @Override
    public void encode(C2SSwapPermanentFruit message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.fruit);
    }

    @Override
    public C2SSwapPermanentFruit decode(FriendlyByteBuf buffer)
    {
        return new C2SSwapPermanentFruit(buffer.readInt());
    }

    @Override
    public void handle(C2SSwapPermanentFruit message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null && !player.isSpectator())
            {
                FruitDataHandler.clearCurrentEffect(player);
                ServerPlayHandler.swapPermanentFruit(player, message.fruit);
            }
        });
        context.setHandled(true);
    }
}
