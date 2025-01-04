package ttv.migami.nep.event;

import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import ttv.migami.nep.entity.CustomProjectileEntity;

/**
 * <p>Fired when a projectile hits a block or entity.</p>
 *
 * @author Ocelot
 */
@Cancelable
public class FruitProjectileHitEventNew extends Event
{
    private final HitResult result;
    private final CustomProjectileEntity projectile;

    public FruitProjectileHitEventNew(HitResult result, CustomProjectileEntity projectile)
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
    public CustomProjectileEntity getProjectile()
    {
        return projectile;
    }
}
