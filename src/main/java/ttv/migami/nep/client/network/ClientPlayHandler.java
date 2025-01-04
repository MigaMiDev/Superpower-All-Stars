package ttv.migami.nep.client.network;

import ttv.migami.nep.client.CustomFruitManager;
import ttv.migami.nep.common.NetworkFruitManager;
import ttv.migami.nep.network.message.S2CMessageUpdateFruits;

public class ClientPlayHandler {
    public static void handleUpdateFruits(S2CMessageUpdateFruits message) {
        NetworkFruitManager.updateRegisteredFruits(message);
        CustomFruitManager.updateCustomFruits(message);
    }
}