package ttv.migami.spas.client;

import com.mrcrayfish.framework.api.data.login.ILoginData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.Validate;
import ttv.migami.spas.Reference;
import ttv.migami.spas.common.CustomFruit;
import ttv.migami.spas.common.CustomFruitLoader;
import ttv.migami.spas.init.ModItems;
import ttv.migami.spas.network.message.S2CMessageUpdateFruits;

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

    public static void fill(CreativeModeTab.Output output)
    {
        if(customFruitMap != null)
        {
            customFruitMap.forEach((id, fruit) ->
            {
                ItemStack stack = new ItemStack(ModItems.FIREWORK_FRUIT.get());
                stack.setHoverName(Component.translatable("item." + id.getNamespace() + "." + id.getPath() + ".name"));
                CompoundTag tag = stack.getOrCreateTag();
                tag.put("Fruit", fruit.getFruit().serializeNBT());
                tag.putBoolean("Custom", true);
                output.accept(stack);
            });
        }
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