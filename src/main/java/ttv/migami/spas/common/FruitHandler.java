package ttv.migami.spas.common;

import net.minecraft.server.level.ServerPlayer;

public interface FruitHandler {
    void handle(ServerPlayer player, Fruit fruit, int move, int action);
}