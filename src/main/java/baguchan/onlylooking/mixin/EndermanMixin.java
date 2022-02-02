package baguchan.onlylooking.mixin;

import baguchan.onlylooking.LookUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EndermanMixin extends Monster {
	protected EndermanMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
		super(p_33002_, p_33003_);
	}

	@Inject(at = @At("HEAD"), method = "isLookingAtMe", cancellable = true)
	void isLookingAtMe(Player p_32535_, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!LookUtils.isLookingAtYou(this, p_32535_)) {
			callbackInfo.setReturnValue(false);
		}
	}
}
