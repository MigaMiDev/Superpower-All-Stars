package ttv.migami.mdf.event;

import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import ttv.migami.mdf.entity.CustomProjectileEntity;

/**
 * <p>Fired when a projectile hits a block or entity.</p>
 *
 * @author Ocelot
 */
@Cancelable
public class FruitProjectileHitEvent2 extends Event
{
    private final HitResult result;
    private final CustomProjectileEntity projectile;

    public FruitProjectileHitEvent2(HitResult result, CustomProjectileEntity projectile)
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
