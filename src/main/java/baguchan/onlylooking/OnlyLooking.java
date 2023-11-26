package baguchan.onlylooking;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(OnlyLooking.MODID)
public class OnlyLooking
{
    public static final String MODID = "onlylooking";
    public static final Logger LOGGER = LogManager.getLogger(OnlyLooking.MODID);
    public OnlyLooking() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON_SPEC);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }
}
