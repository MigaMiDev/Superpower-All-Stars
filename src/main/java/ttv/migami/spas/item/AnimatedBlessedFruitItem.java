package ttv.migami.spas.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.effect.FruitEffect;

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
        if (level.isClientSide && user instanceof Player player) {
            for (MobEffectInstance effect : this.getEffects()) {
                if (effect.getEffect() instanceof FruitEffect) {
                    FruitDataHandler.clearCurrentEffect(player);
                    FruitDataHandler.setCurrentEffect(player, effect.getEffect());
                    FruitDataHandler.addPreviousEffect(player, effect.getEffect());
                }
            }
        }
        if (user instanceof ServerPlayer player) {
            for (MobEffectInstance effect : this.getEffects()) {
                if (effect.getEffect() instanceof FruitEffect) {
                    FruitDataHandler.clearCurrentEffect(player);
                    FruitDataHandler.setCurrentEffect(player, effect.getEffect());
                    FruitDataHandler.addPreviousEffect(player, effect.getEffect());
                }
            }
        }
        return super.finishUsingItem(stack, level, user);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("info.spas.fruit_info").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.spas.blessed_fruit_info").withStyle(ChatFormatting.YELLOW));
    }

}
