package ttv.migami.nep.event;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class FruitFireEvent extends PlayerEvent {
    private final Player player;
    private final MobEffect effect;
    private final int move;

    public FruitFireEvent(Player player, MobEffect effect, int move)
    {
        super(player);
        this.player = player;
        this.effect = effect;
        this.move = move;
    }

    /**
     * @return Whether or not this event was fired on the client side
     */
    public boolean isClient()
    {
        return player.getCommandSenderWorld().isClientSide();
    }

    public MobEffect getEffect()
    {
        return this.effect;
    }

    public int getEffectID()
    {
        return MobEffect.getId(this.effect);
    }

    public int getMove()
    {
        return this.move;
    }

    /**
     * <p>Fired when a player is about to shoot a bullet.</p>
     *
     * @author Ocelot
     */
    @Cancelable
    public static class Pre extends FruitFireEvent
    {
        public Pre(Player player, MobEffect effect, int move)
        {
            super(player, effect, move);
        }
    }

    /**
     * <p>Fired after a player has shot a bullet.</p>
     *
     * @author Ocelot
     */
    public static class Post extends FruitFireEvent
    {
        public Post(Player player, MobEffect effect, int move)
        {
            super(player, effect, move);
        }
    }
}
