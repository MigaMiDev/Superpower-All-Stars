package ttv.migami.mdf.init;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.util.LazyOptional;
import ttv.migami.mdf.network.PacketHandler;
import ttv.migami.mdf.network.message.C2SMessageBlessings;
import ttv.migami.mdf.capanility.IFruitEffectCapability;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("addBlessing")
            .then(Commands.argument("effect", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String effectName = StringArgumentType.getString(context, "effect");
                    ResourceLocation effectId = new ResourceLocation(effectName);

                    // Retrieve the MobEffect by ID
                    MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                    if (effect == null) {
                        context.getSource().sendFailure(Component.literal("Effect not found: " + effectName));
                        return Command.SINGLE_SUCCESS;
                    }

                    // Access and modify the player's capability
                    LazyOptional<IFruitEffectCapability> fruitEffectCap = player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY);
                    fruitEffectCap.ifPresent(cap -> {
                        cap.addFruitEffect(new MobEffectInstance(effect, Integer.MAX_VALUE, 0, true, false, true));
                        context.getSource().sendSuccess(() -> Component.literal("Added effect: " + effectName), true);
                    });
                    
                    return Command.SINGLE_SUCCESS;
                })));

        dispatcher.register(Commands.literal("listBlessings")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    LazyOptional<IFruitEffectCapability> fruitEffectCap = player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY);
                    fruitEffectCap.ifPresent(cap -> {
                        context.getSource().sendSuccess(() -> Component.literal("Bazinga"), true);
                        for (MobEffectInstance effectInstance : cap.getFruitEffects()) {
                            MobEffect effect = effectInstance.getEffect();
                            ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
                            context.getSource().sendSuccess(() -> Component.literal("-" + effectId), true);
                        }
                    });

                    return Command.SINGLE_SUCCESS;
                }));

        dispatcher.register(Commands.literal("removeBlessings")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    LazyOptional<IFruitEffectCapability> fruitEffectCap = player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY);
                    fruitEffectCap.ifPresent(IFruitEffectCapability::clearFruitEffects);

                    return Command.SINGLE_SUCCESS;
                }));

        dispatcher.register(Commands.literal("blessingsScreen")
                .executes(context -> {
                    /*ServerPlayer player = context.getSource().getPlayerOrException();
                    /*Minecraft.getInstance().setScreen(new BlessingScreen(player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY)
                            .orElseThrow(() -> new IllegalStateException("Fruit effect capability not found")).getFruitEffects()));*/
                    PacketHandler.getPlayChannel().sendToServer(new C2SMessageBlessings());
                    return Command.SINGLE_SUCCESS;
                }));
    }
}