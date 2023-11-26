package baguchan.onlylooking;

import com.google.common.collect.Lists;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ModConfigs {
	public static final Common COMMON;
	public static final ModConfigSpec COMMON_SPEC;

	static {
		Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Common {
		public final ModConfigSpec.ConfigValue<List<? extends String>> NEW_LOOKING_BLACKLIST;
		public final ModConfigSpec.ConfigValue<List<? extends String>> DISABLE_VIBRATION_LIST;
		public final ModConfigSpec.BooleanValue VIBRATION_CHECK;
		public final ModConfigSpec.IntValue VIBRATION_RANGE;


		public Common(ModConfigSpec.Builder builder) {
			NEW_LOOKING_BLACKLIST = builder
					.comment("Add Entity on Looking AI Blacklist. Use the full name(This config only disabled mob enchant when mob spawn. not mean delete complete, eg: minecraft:zomibe.")
					.define("LookingAIMobBlacklist", Lists.newArrayList("minecraft:phantom", "minecraft:player", "minecraft:ender_dragon"));
			DISABLE_VIBRATION_LIST = builder
					.comment("Add Entity on Mob check Vibration BlackList")
					.define("VibrationBlackList", Lists.newArrayList("minecraft:phantom", "minecraft:warden"));
			VIBRATION_CHECK = builder
					.comment("Enable Mob check Vibration(when they find sounds. find sound source.)")
					.define("Vibration", true);
			VIBRATION_RANGE = builder
					.comment("Change Mob check Vibration Range.(It mean you can more encounter enemy)")
					.defineInRange("Vibration Range", 16, 6, 24);
		}
	}
}
