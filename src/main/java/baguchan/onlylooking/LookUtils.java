package baguchan.onlylooking;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LookUtils {
	public static boolean isLookingAtYou(LivingEntity entity, LivingEntity target) {
		Vec3 vec3 = entity.getViewVector(1.0F).normalize();
		Vec3 vec31 = new Vec3(target.getX() - entity.getX(), target.getEyeY() - entity.getEyeY(), target.getZ() - entity.getZ());
		double d0 = vec31.length();
		vec31 = vec31.normalize();
		double d1 = vec3.dot(vec31);
		/*
		 * range 1.0 is only Look at the hostile body beyond the mob's line of sight.
		 * If range is 7.0, move the eyes of the mob to see the mob in the visible range.
		 *
		 */
		double range = 6.5D;

		return d1 > 1.0D - range / d0 ? LookUtils.hasLineOfSight(entity, target) : false;

	}

	public static boolean hasLineOfSight(LivingEntity entity, Entity target) {
		if (target.level != entity.level) {
			return false;
		} else {
			Vec3 vec3 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
			Vec3 vec31 = new Vec3(target.getX(), target.getEyeY(), target.getZ());

			return entity.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
		}
	}
}
