package ttv.migami.spas.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ttv.migami.spas.event.FruitFireEvent;

import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class FruitRecoilHandler
{
    private static FruitRecoilHandler instance;

    public static FruitRecoilHandler get()
    {
        if(instance == null)
        {
            instance = new FruitRecoilHandler();
        }
        return instance;
    }

    private final Random random = new Random();

    private float cameraRecoil;
    private float progressCameraRecoil;

    private FruitRecoilHandler() {}

    //Our variable, currently null but will be assigned either a 1 or 0 every time a fruit is shot.
    private static int recoilRand;

    //This method is called every time a fruit is shot.
    @SubscribeEvent
    public void preShoot(FruitFireEvent.Pre event)
    {
        if(!event.isClient())
            return;

        recoilRand = new Random().nextInt(2);
    }

    @SubscribeEvent
    public void onFruitFire(FruitFireEvent.Post event)
    {
        if(!event.isClient())
            return;

        this.cameraRecoil = ActionHandler.get().getAction().getRecoilKick();
        this.progressCameraRecoil = 0F;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END || this.cameraRecoil <= 0)
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        float recoilAmount = this.cameraRecoil * mc.getDeltaFrameTime() * 0.15F;
        float startProgress = this.progressCameraRecoil / this.cameraRecoil;
        float endProgress = (this.progressCameraRecoil + recoilAmount) / this.cameraRecoil;

        float pitch = mc.player.getXRot();
        float yaw = mc.player.getYRot();

        if(startProgress < 0.2F)
        {
            mc.player.setXRot(pitch - ((endProgress - startProgress) / 0.2F) * this.cameraRecoil);
            if(recoilRand == 1)
                mc.player.setYRot(yaw - ((endProgress - startProgress) / 0.2F) * this.cameraRecoil/2);
            else
                mc.player.setYRot(yaw + ((endProgress - startProgress) / 0.2F) * this.cameraRecoil/2);
        }
        else
        {
            mc.player.setXRot(pitch + ((endProgress - startProgress) / 0.8F) * this.cameraRecoil);
            if(recoilRand == 1)
                mc.player.setYRot(yaw + ((endProgress - startProgress) / 0.8F) * this.cameraRecoil/2);
            else
                mc.player.setYRot(yaw - ((endProgress - startProgress) / 0.8F) * this.cameraRecoil/2);
        }

        this.progressCameraRecoil += recoilAmount;

        if(this.progressCameraRecoil >= this.cameraRecoil)
        {
            this.cameraRecoil = 0;
            this.progressCameraRecoil = 0;
        }
    }
}
