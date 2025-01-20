package ttv.migami.spas.network.message;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import ttv.migami.spas.client.network.ClientPlayHandler;
import ttv.migami.spas.common.CustomFruit;
import ttv.migami.spas.common.CustomFruitLoader;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.NetworkFruitManager;

/**
 * Author: MrCrayfish
 */
public class S2CMessageUpdateFruits extends PlayMessage<S2CMessageUpdateFruits>
{
    private ImmutableMap<ResourceLocation, Fruit> registeredFruits;
    private ImmutableMap<ResourceLocation, CustomFruit> customFruits;

    public S2CMessageUpdateFruits() {}

    @Override
    public void encode(S2CMessageUpdateFruits message, FriendlyByteBuf buffer)
    {
        Validate.notNull(NetworkFruitManager.get());
        Validate.notNull(CustomFruitLoader.get());
        NetworkFruitManager.get().writeRegisteredFruits(buffer);
        CustomFruitLoader.get().writeCustomFruits(buffer);
    }

    @Override
    public S2CMessageUpdateFruits decode(FriendlyByteBuf buffer)
    {
        S2CMessageUpdateFruits message = new S2CMessageUpdateFruits();
        message.registeredFruits = NetworkFruitManager.readRegisteredFruits(buffer);
        message.customFruits = CustomFruitLoader.readCustomFruits(buffer);
        return message;
    }

    @Override
    public void handle(S2CMessageUpdateFruits message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUpdateFruits(message));
        context.setHandled(true);
    }

    public ImmutableMap<ResourceLocation, Fruit> getRegisteredFruits()
    {
        return this.registeredFruits;
    }

    public ImmutableMap<ResourceLocation, CustomFruit> getCustomFruits()
    {
        return this.customFruits;
    }
}