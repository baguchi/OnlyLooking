package baguchan.onlylooking;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;


/*
 * Copyright (c) 2021 Laike-Endaril
 * Released under the MIT license
 * https://www.curseforge.com/project/309344/license
 */
public class LookUtils {
	public static boolean isLookingAtYou(LivingEntity entity, Entity target) {
		if (!ModConfigs.COMMON.NEW_LOOKING_BLACKLIST.get().contains(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString())) {
			return isLookingAtYouTest(entity, target);
		} else {
			return true;
		}
	}

	public static boolean hasLineOfSightOnlyClip(LivingEntity entity, Entity target) {
		if (target.level != entity.level) {
			return false;
		} else {
			Vec3 vec3 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
			Vec3 vec31 = new Vec3(target.getX(), target.getEyeY(), target.getZ());
			if (vec31.distanceTo(vec3) > 128.0D) {
				return false;
			} else {
				return entity.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
			}
		}
	}

	public static boolean isVibrationAvaiable(LivingEntity entity) {
		return !ModConfigs.COMMON.DISABLE_VIBRATION_LIST.get().contains(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()) && ModConfigs.COMMON.VIBRATION_CHECK.get();
	}

	public static boolean isPrimeDislike(LivingEntity entity) {
		return ModConfigs.COMMON.PRIME_DISLIKE_LIST.get().contains(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString());
	}

	public static boolean isLookingAtYouTest(LivingEntity entity, Entity target) {
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

		double sensitive = 10;
		return f2 > sensitive;
	}
}
