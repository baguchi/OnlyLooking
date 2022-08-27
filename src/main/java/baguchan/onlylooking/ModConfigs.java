package baguchan.onlylooking;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ModConfigs {
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static {
		Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Common {
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> NEW_LOOKING_BLACKLIST;

		public Common(ForgeConfigSpec.Builder builder) {
			NEW_LOOKING_BLACKLIST = builder
					.comment("Add Entity on Looking AI Blacklist. Use the full name(This config only disabled mob enchant when mob spawn. not mean delete complete, eg: minecraft:zomibe.")
					.define("LookingAIMobBlacklist", Lists.newArrayList("minecraft:phantom"));
		}
	}
}
