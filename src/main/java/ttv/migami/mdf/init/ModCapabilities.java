package ttv.migami.mdf.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import ttv.migami.mdf.capanility.IFruitEffectCapability;

public class ModCapabilities {
    public static final Capability<IFruitEffectCapability> FRUIT_EFFECT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IFruitEffectCapability.class);
    }
}