package ttv.migami.mdf.capanility;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import ttv.migami.mdf.init.ModCapabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncFruitEffectCapabilityPacket {
    private final List<MobEffectInstance> fruitEffects;

    public SyncFruitEffectCapabilityPacket(List<MobEffectInstance> fruitEffects) {
        this.fruitEffects = fruitEffects;
    }

    public static void encode(SyncFruitEffectCapabilityPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.fruitEffects.size());
        for (MobEffectInstance effect : packet.fruitEffects) {
            buffer.writeInt(MobEffect.getId(effect.getEffect()));
            buffer.writeInt(effect.getDuration());
            buffer.writeInt(effect.getAmplifier());
            buffer.writeBoolean(effect.isAmbient());
            buffer.writeBoolean(effect.isVisible());
        }
    }

    public static SyncFruitEffectCapabilityPacket decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<MobEffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MobEffect effect = MobEffect.byId(buffer.readInt());
            int duration = buffer.readInt();
            int amplifier = buffer.readInt();
            boolean ambient = buffer.readBoolean();
            boolean visible = buffer.readBoolean();
            effects.add(new MobEffectInstance(effect, duration, amplifier, ambient, visible));
        }
        return new SyncFruitEffectCapabilityPacket(effects);
    }

    public static void handle(SyncFruitEffectCapabilityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(cap -> {
                    cap.setFruitEffects(packet.fruitEffects);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}