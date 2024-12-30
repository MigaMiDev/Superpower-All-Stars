package ttv.migami.mdf.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import ttv.migami.mdf.effect.FruitEffect;
import ttv.migami.mdf.init.ModCapabilities;
import ttv.migami.mdf.event.ModNetworkHandler;
import ttv.migami.mdf.capanility.SyncFruitEffectCapabilityPacket;

import javax.annotation.Nullable;
import java.util.List;

public class AnimatedBlessedFruitItem extends AnimatedFruitItem {
    public AnimatedBlessedFruitItem(Properties properties, List<MobEffectInstance> effects, String fruitEffect) {
        super(properties, effects, fruitEffect);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (user instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) user;

            player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(cap -> {
                for (MobEffectInstance effect : this.getEffects()) {
                    if (effect.getEffect() instanceof FruitEffect) {
                        cap.addFruitEffect(effect);
                    }
                }
                ModNetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                        new SyncFruitEffectCapabilityPacket(cap.getFruitEffects()));
            });
        }
        return super.finishUsingItem(stack, level, user);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("info.mdf.fruit_info").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.mdf.blessed_fruit_info").withStyle(ChatFormatting.YELLOW));
    }

}
