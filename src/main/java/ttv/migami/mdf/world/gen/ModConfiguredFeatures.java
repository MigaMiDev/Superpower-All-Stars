package ttv.migami.mdf.world.gen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.init.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures
{
    public static final ResourceKey<ConfiguredFeature<?, ?>> SKELETON_ROOT_KEY = registerKey("skeleton_root");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest soulSoilReplaceables = new BlockMatchTest(Blocks.SOUL_SOIL);
        RuleTest soulSandReplaceables = new BlockMatchTest(Blocks.SOUL_SAND);
        RuleTest netherrackReplaceables = new BlockMatchTest(Blocks.NETHERRACK);

        List<OreConfiguration.TargetBlockState> netherSkeletonRoot = List.of(
                OreConfiguration.target(soulSoilReplaceables, ModBlocks.SKELETON_ROOT.get().defaultBlockState()),
                OreConfiguration.target(soulSandReplaceables, ModBlocks.SKELETON_ROOT.get().defaultBlockState()),
                OreConfiguration.target(netherrackReplaceables, ModBlocks.SKELETON_ROOT.get().defaultBlockState()));

        register(context, SKELETON_ROOT_KEY, Feature.ORE, new OreConfiguration(netherSkeletonRoot, 3, 0.8F));

    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Reference.MOD_ID, name));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
