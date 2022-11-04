package baguchan.onlylooking.mixin;

import baguchan.onlylooking.LookUtils;
import baguchan.onlylooking.ModTags;
import baguchan.onlylooking.OnlyLooking;
import baguchan.onlylooking.VibrationNoParticleListener;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(PathfinderMob.class)
public abstract class PathfinderMobMixin extends Mob implements VibrationListener.VibrationListenerConfig {
	private DynamicGameEventListener<VibrationListener> dynamicGameEventListener;
	private int soundCooldown;

	public PathfinderMobMixin(EntityType<? extends PathfinderMob> p_19870_, Level p_19871_) {
		super(p_19870_, p_19871_);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(EntityType<? extends PathfinderMob> p_19870_, Level p_19871_, CallbackInfo info) {
		this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationNoParticleListener(new EntityPositionSource(this, this.getEyeHeight()), 10, this, (VibrationListener.ReceivingEvent) null, 0.0F, 0));
	}


	public void addAdditionalSaveData(CompoundTag p_219434_) {
		super.addAdditionalSaveData(p_219434_);
		p_219434_.putInt("SoundCooldown", this.soundCooldown);
		VibrationListener.codec(this).encodeStart(NbtOps.INSTANCE, this.dynamicGameEventListener.getListener()).resultOrPartial(OnlyLooking.LOGGER::error).ifPresent((p_219418_) -> {
			p_219434_.put("listener", p_219418_);
		});
	}

	public void readAdditionalSaveData(CompoundTag p_219415_) {
		super.readAdditionalSaveData(p_219415_);

		this.soundCooldown = p_219415_.getInt("SoundCooldown");

		if (p_219415_.contains("listener", 10)) {
			VibrationListener.codec(this).parse(new Dynamic<>(NbtOps.INSTANCE, p_219415_.getCompound("listener"))).resultOrPartial(OnlyLooking.LOGGER::error).ifPresent((p_219408_) -> {
				this.dynamicGameEventListener.updateListener(p_219408_, this.level);
			});
		}

	}

	public boolean shouldListen(ServerLevel p_219370_, GameEventListener p_219371_, BlockPos p_219372_, GameEvent p_219373_, GameEvent.Context p_219374_) {
		if (LookUtils.isVibrationAvaiable(this) && !this.isNoAi() && !this.isDeadOrDying() && p_219370_.getWorldBorder().isWithinBounds(p_219372_) && !this.isRemoved() && this.level == p_219370_ && (this instanceof Enemy && this.soundCooldown <= 0 || p_219373_ == GameEvent.PRIME_FUSE && LookUtils.isPrimeDislike(this)) && this.getTarget() == null) {
			Entity entity = p_219374_.sourceEntity();

			if (p_219373_.is(ModTags.GameEvents.IGNORE_VIBRATION)) {
				return false;
			}

			if (entity == this) {
				return false;
			}

			if (entity instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity) entity;
				if (!this.canAttack(livingentity)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onSignalReceive(ServerLevel p_223865_, GameEventListener p_223866_, BlockPos p_223867_, GameEvent p_223868_, @Nullable Entity entity, @Nullable Entity projectile, float p_223871_) {
		if (!this.isDeadOrDying()) {
			if (p_223868_ == GameEvent.PRIME_FUSE && LookUtils.isPrimeDislike(this)) {
				PathfinderMob pathfinderMob = (PathfinderMob) ((Object) this);
				Vec3 vec3 = DefaultRandomPos.getPosAway(pathfinderMob, 16, 7, new Vec3(p_223867_.getX(), p_223867_.getY(), p_223867_.getZ()));
				this.getNavigation().moveTo(this.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0), 1.2F);
				this.soundCooldown = 60;
			} else {
				if (entity != null) {
					this.getNavigation().moveTo(entity.getX(), entity.getY(), entity.getZ(), 0.95F);
				}
				if (projectile != null) {
					this.getNavigation().moveTo(projectile.getX(), projectile.getY(), projectile.getZ(), 0.95F);
				}
				this.soundCooldown = 200;
			}

		}
	}

	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_219413_) {
		Level level = this.level;
		if (level instanceof ServerLevel serverlevel) {
			p_219413_.accept(this.dynamicGameEventListener, serverlevel);
		}

	}

	public void tick() {
		super.tick();
		Level level = this.level;
		if (LookUtils.isVibrationAvaiable(this)) {
			if (level instanceof ServerLevel serverlevel) {
				this.dynamicGameEventListener.getListener().tick(serverlevel);
			}
		}

		if (this.soundCooldown > 0) {
			--this.soundCooldown;
		}
	}
}
