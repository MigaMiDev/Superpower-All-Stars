package ttv.migami.mdf.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class FruitFireEvent extends Event {
    private final Player player;
    private final float recoilKick;
    
    public FruitFireEvent(Player player, float recoilKick)
    {
        this.player = player;
        this.recoilKick = recoilKick;
    }

    /**
     * @return Whether or not this event was fired on the client side
     */
    public boolean isClient()
    {
        return player.getCommandSenderWorld().isClientSide();
    }

    public float getRecoilKick()
    {
        return recoilKick;
    }

    /**
     * <p>Fired when a player is about to shoot a bullet.</p>
     *
     * @author Ocelot
     */
    @Cancelable
    public static class Pre extends FruitFireEvent
    {
        public Pre(Player player, float recoilKick)
        {
            super(player, recoilKick);
        }
    }

    /**
     * <p>Fired after a player has shot a bullet.</p>
     *
     * @author Ocelot
     */
    public static class Post extends FruitFireEvent
    {
        public Post(Player player, float recoilKick)
        {
            super(player, recoilKick);
        }
    }
}
