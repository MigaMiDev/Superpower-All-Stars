package ttv.migami.spas.common;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.Reference;
import ttv.migami.spas.annotation.Validator;

import javax.annotation.Nullable;
import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CustomFruitLoader extends SimpleJsonResourceReloadListener
{
    private static final Gson GSON_INSTANCE = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        builder.registerTypeAdapter(ActionType.class, JsonDeserializers.ACTION_TYPE);
        builder.registerTypeAdapter(ActionMode.class, JsonDeserializers.ACTION_MODE);
        builder.registerTypeAdapter(Component.class, JsonDeserializers.COMPONENT);
        builder.registerTypeAdapter(FoodExhaustion.class, JsonDeserializers.FOOD_EXHAUSTION);
        return builder.create();
    });

    private static CustomFruitLoader instance;

    private Map<ResourceLocation, CustomFruit> customFruitMap = new HashMap<>();

    public CustomFruitLoader()
    {
        super(GSON_INSTANCE, "custom_fruits");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler)
    {
        ImmutableMap.Builder<ResourceLocation, CustomFruit> builder = ImmutableMap.builder();
        objects.forEach((resourceLocation, object) ->
        {
            try
            {
                CustomFruit customFruit = GSON_INSTANCE.fromJson(object, CustomFruit.class);
                if(customFruit != null && Validator.isValidObject(customFruit))
                {
                    builder.put(resourceLocation, customFruit);
                }
                else
                {
                    SuperpowerAllStars.LOGGER.error("Couldn't load data file {} as it is missing or malformed", resourceLocation);
                }
            }
            catch(InvalidObjectException e)
            {
                SuperpowerAllStars.LOGGER.error("Missing required properties for {}", resourceLocation);
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
        });
        this.customFruitMap = builder.build();
    }

    /**
     * Writes all custom fruits into the provided packet buffer
     *
     * @param buffer a packet buffer get
     */
    public void writeCustomFruits(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(this.customFruitMap.size());
        this.customFruitMap.forEach((id, fruit) -> {
            buffer.writeResourceLocation(id);
            buffer.writeNbt(fruit.serializeNBT());
        });
    }

    /**
     * Reads all registered fruits from the provided packet buffer
     *
     * @param buffer a packet buffer get
     * @return a map of registered fruits from the server
     */
    public static ImmutableMap<ResourceLocation, CustomFruit> readCustomFruits(FriendlyByteBuf buffer)
    {
        int size = buffer.readVarInt();
        if(size > 0)
        {
            ImmutableMap.Builder<ResourceLocation, CustomFruit> builder = ImmutableMap.builder();
            for(int i = 0; i < size; i++)
            {
                ResourceLocation id = buffer.readResourceLocation();
                CustomFruit customFruit = new CustomFruit();
                customFruit.deserializeNBT(buffer.readNbt());
                builder.put(id, customFruit);
            }
            return builder.build();
        }
        return ImmutableMap.of();
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event)
    {
        CustomFruitLoader customFruitLoader = new CustomFruitLoader();
        event.addListener(customFruitLoader);
        CustomFruitLoader.instance = customFruitLoader;
    }

    @Nullable
    public static CustomFruitLoader get()
    {
        return instance;
    }
}