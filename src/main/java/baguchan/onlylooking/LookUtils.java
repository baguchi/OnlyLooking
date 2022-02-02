package baguchan.onlylooking;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class LookUtils {
	public static boolean isLookingAtYou(LivingEntity entity, LivingEntity target) {
		Vec3 vec3 = entity.getViewVector(1.0F).normalize();
		Vec3 vec31 = new Vec3(target.getX() - entity.getX(), target.getEyeY() - entity.getEyeY(), target.getZ() - entity.getZ());
		double d0 = vec31.length();
		vec31 = vec31.normalize();
		double d1 = vec3.dot(vec31);
		return d1 > 1.0D - 0.05D / d0 ? entity.hasLineOfSight(target) : false;

	}
}
