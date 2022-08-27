package baguchan.onlylooking;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;


/*
 * Copyright (c) 2021 Laike-Endaril
 * Released under the MIT license
 * https://www.curseforge.com/project/309344/license
 */
public class LookUtils {
	public static boolean isLookingAtYou(LivingEntity entity, LivingEntity target) {
		return isLookingAtYouTest(entity, target);
	}

	public static boolean isLookingAtYouTest(LivingEntity entity, LivingEntity target) {
		float nearDistance = 3;
		float farDistance = entity.getAttribute(Attributes.FOLLOW_RANGE) != null ? (float) entity.getAttributeBaseValue(Attributes.FOLLOW_RANGE) : 16;

		float largeAngle = 83;
		float smallAngle = 30;


		Vec3 vec3 = entity.getViewVector(1.0F).normalize();
		Vec3 vec31 = new Vec3(target.getX() - entity.getX(), target.getEyeY() - entity.getEyeY(), target.getZ() - entity.getZ());
		double d0 = vec31.length();
		vec31 = vec31.normalize();

		double d1 = vec3.dot(vec31);
		double angleDif = d1 * largeAngle;
		double distanceThreshold;
		if (angleDif > largeAngle) {
			return false;
		} else if (angleDif < smallAngle) {
			distanceThreshold = farDistance;
		} else {
			distanceThreshold = nearDistance + (farDistance - nearDistance) * (largeAngle - angleDif) / (largeAngle - smallAngle);
		}

		double f2 = vec3.distanceTo(target.getEyePosition()) / distanceThreshold;

		double sensitive = 6;
		return f2 > sensitive;
	}
}
