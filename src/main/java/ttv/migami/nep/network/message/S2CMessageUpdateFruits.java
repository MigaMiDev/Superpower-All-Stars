package ttv.migami.nep.network.message;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import ttv.migami.jeg.common.CustomGunLoader;
import ttv.migami.jeg.common.NetworkGunManager;
import ttv.migami.nep.client.network.ClientPlayHandler;
import ttv.migami.nep.common.CustomFruit;
import ttv.migami.nep.common.CustomFruitLoader;
import ttv.migami.nep.common.Fruit;
import ttv.migami.nep.common.NetworkFruitManager;

/**
 * Author: MrCrayfish
 */
public class S2CMessageUpdateFruits extends PlayMessage<S2CMessageUpdateFruits>
{
    private ImmutableMap<ResourceLocation, Fruit> registeredGuns;
    private ImmutableMap<ResourceLocation, CustomFruit> customGuns;

    public S2CMessageUpdateFruits() {}

    @Override
    public void encode(S2CMessageUpdateFruits message, FriendlyByteBuf buffer)
    {
        Validate.notNull(NetworkGunManager.get());
        Validate.notNull(CustomGunLoader.get());
        NetworkFruitManager.get().writeRegisteredFruits(buffer);
        CustomFruitLoader.get().writeCustomFruits(buffer);
    }

    @Override
    public S2CMessageUpdateFruits decode(FriendlyByteBuf buffer)
    {
        S2CMessageUpdateFruits message = new S2CMessageUpdateFruits();
        message.registeredGuns = NetworkFruitManager.readRegisteredFruits(buffer);
        message.customGuns = CustomFruitLoader.readCustomFruits(buffer);
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
        return this.registeredGuns;
    }

    public ImmutableMap<ResourceLocation, CustomFruit> getCustomFruits()
    {
        return this.customGuns;
    }
}