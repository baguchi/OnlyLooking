package baguchan.onlylooking.mixin;

import baguchan.onlylooking.LookUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
		super(p_19870_, p_19871_);
	}

	@Inject(at = @At("HEAD"), method = "hasLineOfSight", cancellable = true)
	public void isLookingAtMe(Entity p_32535_, CallbackInfoReturnable<Boolean> callbackInfo) {
		LivingEntity livingEntity = (LivingEntity) ((Object) this);
		if (p_32535_.level == this.level && p_32535_ instanceof LivingEntity && !LookUtils.isLookingAtYou(livingEntity, (LivingEntity) p_32535_)) {
			callbackInfo.setReturnValue(false);
		}
	}
}