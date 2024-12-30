package ttv.migami.mdf.effect;

import net.minecraft.network.chat.MutableComponent;

public class Action {
    private final float damage;
    private final boolean canBeHeld;
    private final int cooldown;
    private final int attackInterval;
    private final int attackAmount;
    private final MutableComponent name;
    private final boolean disabled;
    private final boolean hasRecoil;
    private final float recoilKick;

    public Action(float damage, boolean canBeHeld, int cooldown, int attackInterval, int attackAmount, MutableComponent name, boolean disabled, boolean hasRecoil, float recoilKick) {
        this.damage = damage;
        this.canBeHeld = canBeHeld;
        this.cooldown = cooldown;
        this.attackInterval = attackInterval;
        this.attackAmount = attackAmount;
        this.name = name;
        this.disabled = disabled;
        this.hasRecoil = hasRecoil;
        this.recoilKick = recoilKick;
    }

    public float getDamage() { return damage; }
    public boolean canBeHeld() { return canBeHeld; }
    public int getCooldown() { return cooldown; }
    public int getAttackInterval() { return attackInterval; }
    public int getAttackAmount() { return attackAmount; }
    public MutableComponent getName() { return name; }
    public boolean isDisabled() { return disabled; }
    public boolean hasRecoil() { return hasRecoil; }
    public float getRecoilKick() { return recoilKick; }

}
