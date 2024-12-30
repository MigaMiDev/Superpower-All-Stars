package ttv.migami.mdf.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.capanility.SyncFruitEffectCapabilityPacket;

public class ModNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reference.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(packetId++, SyncFruitEffectCapabilityPacket.class, SyncFruitEffectCapabilityPacket::encode, SyncFruitEffectCapabilityPacket::decode, SyncFruitEffectCapabilityPacket::handle);
    }
}