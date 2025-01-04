package ttv.migami.spas.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import ttv.migami.spas.common.network.ServerPlayHandler;

public class C2SMessageAction extends PlayMessage<C2SMessageAction> {
    private float rotationYaw;
    private float rotationPitch;
    private int effectID;
    private int move;
    private int amount;

    public C2SMessageAction() {
    }

    public C2SMessageAction(Player player, int effectID, int move, int amount) {
        this.rotationYaw = player.getYRot();
        this.rotationPitch = player.getXRot();
        this.effectID = effectID;
        this.move = move;
        this.amount = amount;
    }

    public C2SMessageAction(float rotationYaw, float rotationPitch, int effectID, int move, int amount) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.effectID = effectID;
        this.move = move;
        this.amount = amount;
    }

    public void encode(C2SMessageAction message, FriendlyByteBuf buffer) {
        buffer.writeFloat(message.rotationYaw);
        buffer.writeFloat(message.rotationPitch);
        buffer.writeInt(message.effectID);
        buffer.writeInt(message.move);
        buffer.writeInt(message.amount);
    }

    public C2SMessageAction decode(FriendlyByteBuf buffer) {
        float rotationYaw = buffer.readFloat();
        float rotationPitch = buffer.readFloat();
        int effectID = buffer.readInt();
        int move = buffer.readInt();
        int amount = buffer.readInt();
        return new C2SMessageAction(rotationYaw, rotationPitch, effectID, move, amount);
    }

    public void handle(C2SMessageAction message, MessageContext context) {
        context.execute(() -> {
            ServerPlayer player = context.getPlayer();
            if (player != null) {
                ServerPlayHandler.handleShoot(message, player, message.effectID, message.move, message.amount);
            }

        });
        context.setHandled(true);
    }

    public float getRotationYaw() {
        return this.rotationYaw;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }
}
