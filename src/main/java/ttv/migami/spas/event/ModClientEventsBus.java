package ttv.migami.spas.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.client.ModModelLayers;
import ttv.migami.spas.entity.client.buster.LassoModel;
import ttv.migami.spas.entity.client.buster.PianoModel;
import ttv.migami.spas.entity.client.flower.PetalModel;
import ttv.migami.spas.entity.client.skeleton.SmallBoneModel;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEventsBus {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.SMALL_BONE_LAYER, SmallBoneModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.PIANO_LAYER, PianoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.LASSO_LAYER, LassoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.PETAL_LAYER, PetalModel::createBodyLayer);
    }
}
