package ttv.migami.spas.init;

import com.mrcrayfish.framework.api.sync.Serializers;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import ttv.migami.spas.Reference;

public class ModSyncedDataKeys {
    public static final SyncedDataKey<Player, Boolean> SHOOTING = SyncedDataKey.builder(SyncedClassKey.PLAYER, Serializers.BOOLEAN)
            .id(new ResourceLocation(Reference.MOD_ID, "shooting"))
            .defaultValueSupplier(() -> false)
            .resetOnDeath()
            .build();
}
