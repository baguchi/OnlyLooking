package baguchan.onlylooking.mixin;

import baguchan.onlylooking.LookUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin extends TargetGoal {
	@Shadow
	protected LivingEntity target;
	public NearestAttackableTargetGoalMixin(Mob p_26140_, boolean p_26141_) {
		super(p_26140_, p_26141_);
	}

	@Inject(at = @At("TAIL"), method = "findTarget")
	protected void findTarget(CallbackInfo callbackInfo) {
		if(target != null && !LookUtils.isLookingAtYou(this.mob, target)){
			target = null;
		}
	}
}
