package ttv.migami.nep.common;

import net.minecraft.server.level.ServerPlayer;

public interface FruitHandler {
    void handle(ServerPlayer player, int move, int action);
}