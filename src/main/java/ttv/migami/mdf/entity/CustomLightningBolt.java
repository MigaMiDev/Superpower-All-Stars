package ttv.migami.mdf.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

public class CustomLightningBolt extends LightningBolt {

    public CustomLightningBolt(EntityType<? extends LightningBolt> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CustomLightningBolt(Level pLevel, int pX, int pY, int pZ) {
        this(EntityType.LIGHTNING_BOLT, pLevel);
        this.setPos(pX, pY, pZ);
        this.setVisualOnly(true);
    }

}
