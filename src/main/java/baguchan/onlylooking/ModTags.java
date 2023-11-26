package baguchan.onlylooking;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;

public class ModTags {
	public static class GameEvents {
		public static final TagKey<GameEvent> MONSTER_CAN_LISTEN = tag("monster_can_listen");

		private static TagKey<GameEvent> tag(String name) {
			return create(new ResourceLocation(OnlyLooking.MODID, name));
		}

		private static TagKey<GameEvent> create(ResourceLocation p_203853_) {
			return TagKey.create(Registries.GAME_EVENT, p_203853_);
		}
	}
}
