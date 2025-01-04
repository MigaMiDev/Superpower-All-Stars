package ttv.migami.nep.event;

import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import ttv.migami.nep.entity.CustomProjectileEntityOld;

/**
 * <p>Fired when a projectile hits a block or entity.</p>
 *
 * @author Ocelot
 */
@Cancelable
public class FruitProjectileHitEvent extends Event
{
    private final HitResult result;
    private final CustomProjectileEntityOld projectile;

    public FruitProjectileHitEvent(HitResult result, CustomProjectileEntityOld projectile)
    {
        this.result = result;
        this.projectile = projectile;
    }

    /**
     * @return The result of the entity's ray trace
     */
    public HitResult getRayTrace()
    {
        return result;
    }

    /**
     * @return The projectile that hit
     */
    public CustomProjectileEntityOld getProjectile()
    {
        return projectile;
    }
}
