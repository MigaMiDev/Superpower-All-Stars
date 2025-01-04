package ttv.migami.spas.client.network;

import ttv.migami.spas.client.CustomFruitManager;
import ttv.migami.spas.common.NetworkFruitManager;
import ttv.migami.spas.network.message.S2CMessageUpdateFruits;

public class ClientPlayHandler {
    public static void handleUpdateFruits(S2CMessageUpdateFruits message) {
        NetworkFruitManager.updateRegisteredFruits(message);
        CustomFruitManager.updateCustomFruits(message);
    }
}