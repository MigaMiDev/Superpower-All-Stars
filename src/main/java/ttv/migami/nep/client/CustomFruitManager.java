package ttv.migami.nep.client;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.Validate;
import ttv.migami.nep.Reference;
import ttv.migami.nep.common.CustomFruit;
import ttv.migami.nep.common.CustomFruitLoader;
import ttv.migami.nep.network.message.S2CMessageUpdateFruits;

import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class CustomFruitManager
{
    private static Map<ResourceLocation, CustomFruit> customFruitMap;

    public static boolean updateCustomFruits(S2CMessageUpdateFruits message)
    {
        return updateCustomFruits(message.getCustomFruits());
    }

    private static boolean updateCustomFruits(Map<ResourceLocation, CustomFruit> customFruitMap)
    {
        CustomFruitManager.customFruitMap = customFruitMap;
        return true;
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event)
    {
        customFruitMap = null;
    }

    public static class LoginData implements ILoginData
    {
        @Override
        public void writeData(FriendlyByteBuf buffer)
        {
            Validate.notNull(CustomFruitLoader.get());
            CustomFruitLoader.get().writeCustomFruits(buffer);
        }

        @Override
        public Optional<String> readData(FriendlyByteBuf buffer)
        {
            Map<ResourceLocation, CustomFruit> customFruits = CustomFruitLoader.readCustomFruits(buffer);
            CustomFruitManager.updateCustomFruits(customFruits);
            return Optional.empty();
        }
    }
}