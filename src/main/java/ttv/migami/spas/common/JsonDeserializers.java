package ttv.migami.spas.common;

import com.google.gson.JsonDeserializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class JsonDeserializers {
    public static final JsonDeserializer<ResourceLocation> RESOURCE_LOCATION = (json, typeOfT, context) -> {
        return new ResourceLocation(json.getAsString());
    };
    public static final JsonDeserializer<ActionType> ACTION_TYPE = (json, typeOfT, context) -> {
        return ActionType.valueOf(json.getAsString());
    };
    public static final JsonDeserializer<ActionMode> ACTION_MODE = (json, typeOfT, context) -> {
        return ActionMode.getType(ResourceLocation.tryParse(json.getAsString()));
    };
    public static final JsonDeserializer<Component> COMPONENT = (json, typeOfT, context) -> {
        return Component.translatable(ResourceLocation.tryParse(json.getAsString()).toString());
    };
    public static final JsonDeserializer<FoodExhaustion> FOOD_EXHAUSTION = (json, typeOfT, context) -> {
        return FoodExhaustion.valueOf(json.getAsString());
    };

    public JsonDeserializers() {
    }
}