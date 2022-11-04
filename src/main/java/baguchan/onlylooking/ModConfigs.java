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
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLE_VIBRATION_LIST;
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> PRIME_DISLIKE_LIST;
		public final ForgeConfigSpec.BooleanValue VIBRATION_CHECK;


		public Common(ForgeConfigSpec.Builder builder) {
			NEW_LOOKING_BLACKLIST = builder
					.comment("Add Entity on Looking AI Blacklist. Use the full name(This config only disabled mob enchant when mob spawn. not mean delete complete, eg: minecraft:zomibe.")
					.define("LookingAIMobBlacklist", Lists.newArrayList("minecraft:phantom"));
			DISABLE_VIBRATION_LIST = builder
					.comment("Add Entity on Mob check Vibration BlackList")
					.define("VibrationBlackList", Lists.newArrayList("minecraft:phantom", "minecraft:warden"));
			PRIME_DISLIKE_LIST = builder
					.comment("Add Entity on Prime Dislike List(example. They run for TNT)")
					.define("PrimeDislikeBlackList", Lists.newArrayList("minecraft:creeper", "minecraft:zombie", "minecraft:skeleton", "minecraft:sheep", "minecraft:cow", "minecraft:mooshroom", "minecraft:rabbit"
							, "minecraft:cat", "minecraft:ocelot", "minecraft:villager", "minecraft:pillager", "minecraft:evoker", "minecraft:vindicator", "minecraft:illusioner"
							, "hunterillager:hunterillager", "minecraft:stray", "minecraft:husk", "minecraft:pig", "minecraft:chicken"
							, "earthmobsmod:wooly_cow", "earthmobsmod:cluck_shroom", "earthmobsmod:horned_sheep", "earthmobsmod:hyper_rabbit", "earthmobsmod:moobloom", "earthmobsmod:moolip", "earthmobsmod:jumbo_rabbit"));
			VIBRATION_CHECK = builder
					.comment("Enable Mob check Vibration(when they find sounds. find sound source.)")
					.define("Vibration", true);
		}
	}
}
