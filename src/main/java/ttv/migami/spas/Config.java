package ttv.migami.spas;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    /**
     * Client related config options
     */
    public static class Client
    {
        public final Display display;
        public final Controls controls;

        public Client(ForgeConfigSpec.Builder builder)
        {
            builder.push("client");
            {
                this.display = new Display(builder);
                this.controls = new Controls(builder);
            }
            builder.pop();
        }
    }

    /**
     * Display related config options
     */
    public static class Display
    {
        public final ForgeConfigSpec.BooleanValue displayCooldownGUI;
        public final ForgeConfigSpec.BooleanValue vanishCooldownGUI;
        public final ForgeConfigSpec.IntValue displayCooldownGUIXOffset;
        public final ForgeConfigSpec.IntValue displayCooldownGUIYOffset;
        public Display(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Configuration for display related options").push("display");
            {
                this.displayCooldownGUI = builder.comment("If enabled, renders a HUD element displaying the fruit's moveset and cooldowns.").define("displayCooldownGUI", true);
                this.vanishCooldownGUI = builder.comment("If disabled, the HUD will remain even if there are no cooldowns.").define("vanishCooldownGUI", false);
                this.displayCooldownGUIXOffset = builder.comment("Offsets the fruit HUD by the specified X value.").defineInRange("displayCooldownGUIXOffset", 0, -750, 0);
                this.displayCooldownGUIYOffset = builder.comment("Offsets the fruit HUD by the specified Y value.").defineInRange("displayCooldownGUIYOffset", 0, 0, 250);
            }
            builder.pop();
        }
    }

    public static class Controls
    {
        public final ForgeConfigSpec.DoubleValue aimDownSightSensitivity;
        public final ForgeConfigSpec.BooleanValue flipControls;

        public Controls(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Properties relating to controls").push("controls");
            {
                this.aimDownSightSensitivity = builder.comment("A value to multiple the mouse sensitivity by when aiming down weapon sights. Go to (Options > Controls > Mouse Settings > ADS Sensitivity) in game to change this!").defineInRange("aimDownSightSensitivity", 0.75, 0.0, 1.0);
                this.flipControls = builder.comment("When enabled, switches the shoot and aim controls of weapons. Due to technical reasons, you won't be able to use offhand items if you enable this setting.").define("flipControls", false);
            }
            builder.pop();
        }
    }

    /**
     * Common config options
     */
    public static class Common
    {
        public final Gameplay gameplay;
        public final World world;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("common");
            {
                this.gameplay = new Gameplay(builder);
                this.world = new World(builder);
            }
            builder.pop();
        }
    }

    /**
     * Gameplay related config options
     */
    public static class Gameplay
    {
        public final Griefing griefing;
        public final ForgeConfigSpec.BooleanValue onlyOneFruitPerLife;
        public final ForgeConfigSpec.BooleanValue applyHunger;
        public final ForgeConfigSpec.DoubleValue maxScaling;
        public final ForgeConfigSpec.IntValue damageMultiplier;
        public final ForgeConfigSpec.BooleanValue noSwimming;

        public Gameplay(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Properties relating to gameplay").push("gameplay");
            {
                this.onlyOneFruitPerLife = builder.comment("CAREFUL! If this option is enabled, EATING another Fruit while having an active one, will explode the Player! This can be avoided by eating a Broccoli Veggie!").define("onlyOneFruitPerLife", true);
                this.damageMultiplier = builder.comment("The damage will be multiplied by this number. It affects EVERY fruit.").defineInRange("damageMultiplier", 1, 1, 999);
                this.griefing = new Griefing(builder);
                this.maxScaling = builder.comment("The damage scaling will stop once the player reaches this Mastery level.").defineInRange("maxScaling", 3.0D, 1.0D, 999.0D);
                this.applyHunger = builder.comment("If enabled, attacks will consume hunger.").define("applyHunger", true);
                this.noSwimming = builder.comment("If enabled, users will not be able to swim if they have non-water type fruits active.").define("noSwimming", false);
            }
            builder.pop();
        }
    }

    /**
     * World related config options
     */
    public static class World
    {
        public final ForgeConfigSpec.BooleanValue mobsEatFruits;

        public World(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Properties relating to the spas world and its ecosystem").push("world");
            {
                this.mobsEatFruits = builder.comment("If enabled, mobs will chase after and eat fruits!").define("mobsEatFruits", true);
            }
            builder.pop();
        }
    }

    /**
     * Fruit griefing related config options
     */
    public static class Griefing
    {
        public final ForgeConfigSpec.BooleanValue destructionDebri;
        public final ForgeConfigSpec.BooleanValue allowVaccumingInSurvival;
        public final ForgeConfigSpec.BooleanValue setFireToBlocks;
        public final ForgeConfigSpec.BooleanValue stainBlocks;

        public Griefing(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Properties related to fruit griefing").push("griefing");
            {
                this.destructionDebri = builder.comment("If true, some attacks will produce (visual) debri").define("destructionDebri", true);
                this.allowVaccumingInSurvival = builder.comment("If true, players in Survival will be able to remove debris").define("allowVaccumingInSurvival", true);
                this.setFireToBlocks = builder.comment("If true, allows fire attacks to light and spread fires on blocks").define("setFireToBlocks", true);
                this.stainBlocks = builder.comment("If true, Squid attacks will stain and dye blocks").define("stainBlocks", true);
            }
            builder.pop();
        }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;

    static final ForgeConfigSpec commonSpec;
    public static final Config.Common COMMON;

    /*static final ForgeConfigSpec serverSpec;
    public static final Config.Server SERVER;*/

    static
    {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();

        /*final Pair<Server, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();*/
    }

    public static void saveClientConfig()
    {
        clientSpec.save();
    }
}
