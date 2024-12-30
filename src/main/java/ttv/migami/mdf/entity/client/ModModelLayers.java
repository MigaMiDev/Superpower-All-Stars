package ttv.migami.mdf.entity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.mdf.Reference;

public class ModModelLayers {

    public static final ModelLayerLocation SMALL_BONE_LAYER = new ModelLayerLocation(
            new ResourceLocation(Reference.MOD_ID, "small_bone_layer"), "main");

    public static final ModelLayerLocation PIANO_LAYER = new ModelLayerLocation(
            new ResourceLocation(Reference.MOD_ID, "piano"), "main");

    public static final ModelLayerLocation LASSO_LAYER = new ModelLayerLocation(
            new ResourceLocation(Reference.MOD_ID, "lasso"), "main");

    public static final ModelLayerLocation PETAL_LAYER = new ModelLayerLocation(
            new ResourceLocation(Reference.MOD_ID, "petal"), "main");

}
