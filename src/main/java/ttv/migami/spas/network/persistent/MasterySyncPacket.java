package ttv.migami.spas.network.persistent;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MasterySyncPacket {
    private final ResourceLocation effectLocation;
    private final int masteryExperience;
    private final int masteryLevel;

    public MasterySyncPacket(ResourceLocation effectLocation, int masteryExperience, int masteryLevel) {
        this.effectLocation = effectLocation;
        this.masteryExperience = masteryExperience;
        this.masteryLevel = masteryLevel;
    }

    public MasterySyncPacket(FriendlyByteBuf buf) {
        this.effectLocation = buf.readResourceLocation();
        this.masteryExperience = buf.readInt();
        this.masteryLevel = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(effectLocation);
        buf.writeInt(masteryExperience);
        buf.writeInt(masteryLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getPersistentData().putInt(effectLocation + "_MasteryExperience", masteryExperience);
                player.getPersistentData().putInt(effectLocation + "_MasteryLevel", masteryLevel);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}