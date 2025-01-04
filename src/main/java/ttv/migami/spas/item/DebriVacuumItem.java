package ttv.migami.spas.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import ttv.migami.spas.Config;
import ttv.migami.spas.common.network.ServerPlayHandler;

import javax.annotation.Nullable;
import java.util.List;

import static ttv.migami.spas.common.network.ServerPlayHandler.rayTrace;

public class DebriVacuumItem extends ToolTipItem {
    public DebriVacuumItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!world.isClientSide && (Config.COMMON.gameplay.griefing.allowVaccumingInSurvival.get() || player.isCreative())) {
            AABB expandedHitbox = player.getBoundingBox().inflate(3.0D);
            List<Entity> nearbyEntities = world.getEntities(player, expandedHitbox);

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Display) {
                    world.playSound(null, player, SoundEvents.BEEHIVE_EXIT, SoundSource.AMBIENT, 0.75F, 1F);
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }

            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.pass(itemStack);
    }

    private Entity getTargetEntity(Player player) {
        BlockPos blockPos = rayTrace(player, 4.0D);
        EntityHitResult entityHitResult = ServerPlayHandler.hitEntity(player.level(), player, blockPos);

        return entityHitResult.getEntity();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("info.spas.tooltip_item" + "." + this.asItem()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.spas.tooltip_item" + "." + this.asItem() + "2").withStyle(ChatFormatting.RED));
    }

}