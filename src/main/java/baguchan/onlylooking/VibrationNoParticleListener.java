package baguchan.onlylooking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class VibrationNoParticleListener extends VibrationListener {
	public VibrationNoParticleListener(PositionSource p_223760_, int p_223761_, VibrationListener.VibrationListenerConfig p_223762_, @Nullable VibrationListener.ReceivingEvent p_223763_, float p_223764_, int p_223765_) {
		super(p_223760_, p_223761_, p_223762_, p_223763_, p_223764_, p_223765_);
	}

	@Override
	public boolean handleGameEvent(ServerLevel p_223767_, GameEvent.Message p_223768_) {
		if (this.receivingEvent != null) {
			return false;
		} else {
			GameEvent gameevent = p_223768_.gameEvent();
			GameEvent.Context gameevent$context = p_223768_.context();
			if (!this.config.isValidVibration(gameevent, gameevent$context)) {
				return false;
			} else {
				Optional<Vec3> optional = this.listenerSource.getPosition(p_223767_);
				if (optional.isEmpty()) {
					return false;
				} else {
					Vec3 vec3 = p_223768_.source();
					Vec3 vec31 = optional.get();
					if (!this.config.shouldListen(p_223767_, this, new BlockPos(vec3), gameevent, gameevent$context)) {
						return false;
					} else if (isOccluded(p_223767_, vec3, vec31)) {
						return false;
					} else {
						this.scheduleSignal(p_223767_, gameevent, gameevent$context, vec3, vec31);
						return true;
					}
				}
			}
		}
	}

	private void scheduleSignal(ServerLevel p_223770_, GameEvent p_223771_, GameEvent.Context p_223772_, Vec3 p_223773_, Vec3 p_223774_) {
		this.receivingDistance = (float) p_223773_.distanceTo(p_223774_);
		this.receivingEvent = new VibrationListener.ReceivingEvent(p_223771_, this.receivingDistance, p_223773_, p_223772_.sourceEntity());
		this.travelTimeInTicks = Mth.floor(this.receivingDistance);
		this.config.onSignalSchedule();
	}

	private static boolean isOccluded(Level p_223776_, Vec3 p_223777_, Vec3 p_223778_) {
		Vec3 vec3 = new Vec3((double) Mth.floor(p_223777_.x) + 0.5D, (double) Mth.floor(p_223777_.y) + 0.5D, (double) Mth.floor(p_223777_.z) + 0.5D);
		Vec3 vec31 = new Vec3((double) Mth.floor(p_223778_.x) + 0.5D, (double) Mth.floor(p_223778_.y) + 0.5D, (double) Mth.floor(p_223778_.z) + 0.5D);

		for (Direction direction : Direction.values()) {
			Vec3 vec32 = vec3.relative(direction, (double) 1.0E-5F);
			if (p_223776_.isBlockInLine(new ClipBlockStateContext(vec32, vec31, (p_223780_) -> {
				return p_223780_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
			})).getType() != HitResult.Type.BLOCK) {
				return false;
			}
		}

		return true;
	}
}
