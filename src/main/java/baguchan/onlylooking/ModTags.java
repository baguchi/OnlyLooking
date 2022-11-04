package baguchan.onlylooking;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;

public class ModTags {
	public static class GameEvents {
		public static final TagKey<GameEvent> IGNORE_VIBRATION = tag("ignore_vibrations");

		private static TagKey<GameEvent> tag(String name) {
			return create(new ResourceLocation(OnlyLooking.MODID, name));
		}

		private static TagKey<GameEvent> create(ResourceLocation p_203853_) {
			return TagKey.create(Registry.GAME_EVENT_REGISTRY, p_203853_);
		}
	}
}
