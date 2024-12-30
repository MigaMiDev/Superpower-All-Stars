package ttv.migami.mdf.datagen;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.common.Fruit;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public abstract class FruitProvider implements DataProvider
{
    protected final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final Map<ResourceLocation, Fruit> fruits = new HashMap<>();

    protected FruitProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "fruits");
        this.registries = registries;
    }

    protected abstract void registerFruits();

    protected final void addFruit(ResourceLocation id, Fruit fruit)
    {
        this.fruits.put(id, fruit);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        return this.registries.thenCompose(provider ->
        {
            this.fruits.clear();
            this.registerFruits();
            return CompletableFuture.allOf(this.fruits.entrySet().stream().map(entry -> {
                ResourceLocation key = entry.getKey();
                Fruit fruit = entry.getValue();
                Path path = this.pathProvider.json(key);
                JsonObject object = fruit.toJsonObject();
                return DataProvider.saveStable(cache, object, path);
            }).toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName()
    {
        return "Fruits: " + Reference.MOD_ID;
    }

}
