package ttv.migami.mdf.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ttv.migami.mdf.common.network.ServerPlayHandler;

public class C2SFruitMessage extends PlayMessage<C2SFruitMessage>
{
    private int fruit;
    private int move;
    private int amount;

    public C2SFruitMessage() {}

    public C2SFruitMessage(int fruit, int move, int amount)
    {
        this.fruit = fruit;
        this.move = move;
        this.amount = amount;
    }

    @Override
    public void encode(C2SFruitMessage message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.fruit);
        buffer.writeInt(message.move);
        buffer.writeInt(message.amount);
    }

    @Override
    public C2SFruitMessage decode(FriendlyByteBuf buffer)
    {
        return new C2SFruitMessage(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(C2SFruitMessage message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = context.getPlayer();
            if(player != null && !player.isSpectator())
            {
                ServerPlayHandler.messageToFruit(player, message.fruit, message.move, message.amount);
            }
        });
        context.setHandled(true);
    }
}
