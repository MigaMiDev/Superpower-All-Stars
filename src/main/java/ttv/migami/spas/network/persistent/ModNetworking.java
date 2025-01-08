package ttv.migami.spas.network.persistent;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import ttv.migami.spas.Reference;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Reference.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncFruitsPacket.class, SyncFruitsPacket::toBytes,
            SyncFruitsPacket::new, SyncFruitsPacket::handle);
        CHANNEL.registerMessage(id++, MasterySyncPacket.class, MasterySyncPacket::toBytes, MasterySyncPacket::new, MasterySyncPacket::handle);
    }
}