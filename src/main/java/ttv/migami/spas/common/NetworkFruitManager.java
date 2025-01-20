package ttv.migami.spas.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.framework.api.data.login.ILoginData;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.Reference;
import ttv.migami.spas.annotation.Validator;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.network.PacketHandler;
import ttv.migami.spas.network.message.S2CMessageUpdateFruits;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class NetworkFruitManager extends SimplePreparableReloadListener<Map<FruitEffect, Fruit>>
{
    private static final int FILE_TYPE_LENGTH_VALUE = ".json".length();
    private static final Gson GSON_INSTANCE = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        builder.registerTypeAdapter(ActionType.class, JsonDeserializers.ACTION_TYPE);
        builder.registerTypeAdapter(ActionMode.class, JsonDeserializers.ACTION_MODE);
        builder.registerTypeAdapter(Component.class, JsonDeserializers.COMPONENT);
        builder.registerTypeAdapter(FoodExhaustion.class, JsonDeserializers.FOOD_EXHAUSTION);
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        return builder.create();
    });

    private static final List<FruitEffect> clientRegisteredFruits = new ArrayList<>();
    private static NetworkFruitManager instance;

    private Map<ResourceLocation, Fruit> registeredFruits = new HashMap<>();

    @Override
    protected Map<FruitEffect, Fruit> prepare(ResourceManager manager, ProfilerFiller profiler)
    {
        Map<FruitEffect, Fruit> map = new HashMap<>();
        ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(effect -> effect instanceof FruitEffect).forEach(effect ->
        {
            ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
            if (id != null)
            {
                List<ResourceLocation> resources = new ArrayList<>(manager.listResources("fruits", (fileName) -> fileName.getPath().endsWith(id.getPath() + ".json")).keySet());
                resources.sort((r1, r2) -> {
                    if(r1.getNamespace().equals(r2.getNamespace())) return 0;
                    return r2.getNamespace().equals(Reference.MOD_ID) ? 1 : -1;
                });
                resources.forEach(resourceLocation ->
                {
                    String path = resourceLocation.getPath().substring(0, resourceLocation.getPath().length() - FILE_TYPE_LENGTH_VALUE);
                    String[] splitPath = path.split("/");

                    // Makes sure the file name matches exactly with the id of the fruit
                    if(!id.getPath().equals(splitPath[splitPath.length - 1]))
                        return;

                    // Also check if the mod id matches with the fruit's registered namespace
                    if (!id.getNamespace().equals(resourceLocation.getNamespace()))
                        return;

                    manager.getResource(resourceLocation).ifPresent(resource ->
                    {
                        try (Reader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8)))
                        {
                            Fruit fruit = GsonHelper.fromJson(GSON_INSTANCE, reader, Fruit.class);
                            if (fruit != null && Validator.isValidObject(fruit))
                            {
                                map.put((FruitEffect) effect, fruit);
                            }
                            else
                            {
                                SuperpowerAllStars.LOGGER.error("Couldn't load data file {} as it is missing or malformed. Using default fruit data", resourceLocation);
                                map.putIfAbsent((FruitEffect) effect, new Fruit());
                            }
                        }
                        catch (InvalidObjectException e)
                        {
                            SuperpowerAllStars.LOGGER.error("Missing required properties for {}", resourceLocation);
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            SuperpowerAllStars.LOGGER.error("Couldn't parse data file {}", resourceLocation);
                        }
                        catch (IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
                    });
                });
            }
        });
        return map;
    }

    @Override
    protected void apply(Map<FruitEffect, Fruit> objects, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        ImmutableMap.Builder<ResourceLocation, Fruit> builder = ImmutableMap.builder();
        objects.forEach((effect, fruit) -> {
            builder.put(Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(effect)), fruit);
            effect.setFruit(new Supplier(fruit));
        });
        this.registeredFruits = builder.build();
    }

    /**
     * Writes all registered fruits into the provided packet buffer
     *
     * @param buffer a packet buffer get
     */
    public void writeRegisteredFruits(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(this.registeredFruits.size());
        this.registeredFruits.forEach((id, fruit) -> {
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
    public static ImmutableMap<ResourceLocation, Fruit> readRegisteredFruits(FriendlyByteBuf buffer)
    {
        int size = buffer.readVarInt();
        if(size > 0)
        {
            ImmutableMap.Builder<ResourceLocation, Fruit> builder = ImmutableMap.builder();
            for(int i = 0; i < size; i++)
            {
                ResourceLocation id = buffer.readResourceLocation();
                Fruit fruit = Fruit.create(buffer.readNbt());
                builder.put(id, fruit);
            }
            return builder.build();
        }
        return ImmutableMap.of();
    }

    public static boolean updateRegisteredFruits(S2CMessageUpdateFruits message)
    {
        return updateRegisteredFruits(message.getRegisteredFruits());
    }

    /**
     * Updates registered fruits from data provided by the server
     *
     * @return true if all registered fruits were able to update their corresponding fruit item
     */
    private static boolean updateRegisteredFruits(Map<ResourceLocation, Fruit> registeredFruits)
    {
        clientRegisteredFruits.clear();
        if(registeredFruits != null)
        {
            for(Map.Entry<ResourceLocation, Fruit> entry : registeredFruits.entrySet())
            {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(entry.getKey());
                if(!(effect instanceof FruitEffect))
                {
                    return false;
                }
                ((FruitEffect) effect).setFruit(new Supplier(entry.getValue()));
                clientRegisteredFruits.add((FruitEffect) effect);
            }
            return true;
        }
        return false;
    }

    /**
     * Gets a map of all the registered fruits objects. Note, this is an immutable map.
     *
     * @return a map of registered fruit objects
     */
    public Map<ResourceLocation, Fruit> getRegisteredFruits()
    {
        return this.registeredFruits;
    }

    /**
     * Gets a list of all the fruits registered on the client side. Note, this is an immutable list.
     *
     * @return a map of fruits registered on the client
     */
    public static List<FruitEffect> getClientRegisteredFruits()
    {
        return ImmutableList.copyOf(clientRegisteredFruits);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event)
    {
        NetworkFruitManager.instance = null;
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event)
    {
        NetworkFruitManager networkFruitManager = new NetworkFruitManager();
        event.addListener(networkFruitManager);
        NetworkFruitManager.instance = networkFruitManager;
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event)
    {
        if(event.getPlayer() == null)
        {
            PacketHandler.getPlayChannel().sendToAll(new S2CMessageUpdateFruits());
        }
    }

    /**
     * Gets the network fruit manager. This will be null if the client isn't running an integrated
     * server or the client is connected to a dedicated server.
     *
     * @return the network fruit manager
     */
    @Nullable
    public static NetworkFruitManager get()
    {
        return instance;
    }

    /**
     * A simple wrapper for a fruit object to pass to FruitEffect. This is to indicate to developers that
     * Fruit instances shouldn't be changed on FruitEffects as they are controlled by NetworkFruitManager.
     * Changes to fruit properties should be made through the JSON file.
     */
    public static class Supplier
    {
        private final Fruit fruit;

        private Supplier(Fruit fruit)
        {
            this.fruit = fruit;
        }

        public Fruit getFruit()
        {
            return this.fruit;
        }
    }

    public static class LoginData implements ILoginData
    {
        @Override
        public void writeData(FriendlyByteBuf buffer)
        {
            Validate.notNull(NetworkFruitManager.get());
            NetworkFruitManager.get().writeRegisteredFruits(buffer);
        }

        @Override
        public Optional<String> readData(FriendlyByteBuf buffer)
        {
            Map<ResourceLocation, Fruit> registeredFruits = NetworkFruitManager.readRegisteredFruits(buffer);
            NetworkFruitManager.updateRegisteredFruits(registeredFruits);
            return Optional.empty();
        }
    }
}