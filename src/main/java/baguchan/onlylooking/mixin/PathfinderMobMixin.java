package baguchan.onlylooking.mixin;

import baguchan.onlylooking.LookUtils;
import baguchan.onlylooking.VibrationUserMonster;
import baguchan.onlylooking.api.IHearSound;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.BiConsumer;

@Mixin(PathfinderMob.class)
public abstract class PathfinderMobMixin extends Mob implements VibrationSystem, IHearSound {
	private static final Logger LOGGER = LogUtils.getLogger();
	private DynamicGameEventListener<Listener> dynamicVibrationListener;
	private VibrationSystem.User vibrationUser;
	private VibrationSystem.Data vibrationData;

	private AngerManagement angerManagement = new AngerManagement((entity) -> {
		return entity instanceof LivingEntity living && this.canAttack(living);
	}, Collections.emptyList());

	private int soundCooldown;

	public PathfinderMobMixin(EntityType<? extends PathfinderMob> p_19870_, Level p_19871_) {
		super(p_19870_, p_19871_);

	}

	public int getSoundCooldown() {
		return soundCooldown;
	}

	public void setSoundCooldown(int soundCooldown) {
		this.soundCooldown = soundCooldown;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onConstructor(EntityType<? extends PathfinderMob> p_19870_, Level p_19871_, CallbackInfo info) {
		PathfinderMob pathfinderMob = (PathfinderMob) (Object) this;
		this.dynamicVibrationListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));

		this.vibrationUser = new VibrationUserMonster(pathfinderMob);
		this.vibrationData = new VibrationSystem.Data();
	}


	public void addAdditionalSaveData(CompoundTag p_219434_) {
		super.addAdditionalSaveData(p_219434_);
		p_219434_.putInt("SoundCooldown", this.soundCooldown);
		AngerManagement.codec(entity -> {
					return entity instanceof LivingEntity living && this.canAttack(living);
				})
				.encodeStart(NbtOps.INSTANCE, this.angerManagement)
				.resultOrPartial(LOGGER::error)
				.ifPresent(p_219437_ -> p_219434_.put("monster_anger", p_219437_));
		VibrationSystem.Data.CODEC
				.encodeStart(NbtOps.INSTANCE, this.vibrationData)
				.resultOrPartial(LOGGER::error)
				.ifPresent(p_219418_ -> p_219434_.put("monster_listener", p_219418_));
	}

	public void readAdditionalSaveData(CompoundTag p_219415_) {
		super.readAdditionalSaveData(p_219415_);

		this.soundCooldown = p_219415_.getInt("SoundCooldown");

		if (p_219415_.contains("monster_anger")) {
			AngerManagement.codec(entity -> {
						return entity instanceof LivingEntity living && this.canAttack(living);
					})
					.parse(new Dynamic<>(NbtOps.INSTANCE, p_219415_.get("monster_anger")))
					.resultOrPartial(LOGGER::error)
					.ifPresent(p_219394_ -> this.angerManagement = p_219394_);
		}
		if (p_219415_.contains("monster_listener", 10)) {
			VibrationSystem.Data.CODEC
					.parse(new Dynamic<>(NbtOps.INSTANCE, p_219415_.getCompound("monster_listener")))
					.resultOrPartial(LOGGER::error)
					.ifPresent(p_281093_ -> this.vibrationData = p_281093_);
		}

	}

	@Override
	public VibrationSystem.Data getVibrationData() {
		return this.vibrationData;
	}

	@Override
	public VibrationSystem.User getVibrationUser() {
		return this.vibrationUser;
	}

	@Override
	public AngerManagement getAngerManagement() {
		return angerManagement;
	}

	private int getActiveAnger() {
		return this.angerManagement.getActiveAnger(this.getTarget());
	}

	public void clearAnger(Entity p_219429_) {
		this.angerManagement.clearAnger(p_219429_);
	}

	public void increaseAngerAt(@javax.annotation.Nullable Entity p_219442_) {
		this.increaseAngerAt(p_219442_, 35, true);
	}

	@VisibleForTesting
	public void increaseAngerAt(@Nullable Entity p_219388_, int p_219389_, boolean p_219390_) {
		if (!this.isNoAi() && p_219388_ instanceof LivingEntity living && this.canAttack(living)) {
			int i = this.angerManagement.increaseAnger(p_219388_, p_219389_);


			if (p_219390_) {
				this.playListeningSound();
			}
		}

	}

	private void playListeningSound() {
		if (this.getAmbientSound() != null) {
			this.playSound(this.getAmbientSound(), this.getSoundVolume(), 0.75F);
		}
	}


	public void tick() {
		super.tick();
		Level level = this.level();
		if (level instanceof ServerLevel serverlevel) {
			if (this.vibrationData.getCurrentVibration() == null) {
				trySelectAndScheduleVibration(serverlevel, this.vibrationData, this.vibrationUser);
			}
			if (this.vibrationData.getCurrentVibration() != null) {
				this.vibrationData.decrementTravelTime();
				if (this.vibrationData.getTravelTimeInTicks() <= 0) {
					receiveVibration(serverlevel, this.vibrationData, this.vibrationUser, this.vibrationData.getCurrentVibration());
					this.vibrationData.setCurrentVibration(null);
				}
			}
			if (this.tickCount % 20 == 0) {
				this.angerManagement.tick(serverlevel, entity -> {
					return entity instanceof LivingEntity living && this.canAttack(living);
				});
			}
		}


		if (this.soundCooldown > 0) {
			--this.soundCooldown;
		}
	}

	private static void trySelectAndScheduleVibration(ServerLevel p_282775_, VibrationSystem.Data p_282792_, VibrationSystem.User p_281845_) {
		p_282792_.getSelectionStrategy()
				.chosenCandidate(p_282775_.getGameTime())
				.ifPresent(
						p_282059_ -> {
							p_282792_.setCurrentVibration(p_282059_);
							Vec3 vec3 = p_282059_.pos();
							p_282792_.setTravelTimeInTicks(p_281845_.calculateTravelTimeInTicks(p_282059_.distance()));
							p_281845_.onDataChanged();
							p_282792_.getSelectionStrategy().startOver();
						}
				);
	}

	private static boolean receiveVibration(ServerLevel p_282967_, VibrationSystem.Data p_283447_, VibrationSystem.User p_282301_, VibrationInfo p_281498_) {
		BlockPos blockpos = BlockPos.containing(p_281498_.pos());
		BlockPos blockpos1 = p_282301_.getPositionSource().getPosition(p_282967_).map(BlockPos::containing).orElse(blockpos);
		if (p_282301_.requiresAdjacentChunksToBeTicking() && !areAdjacentChunksTicking(p_282967_, blockpos1)) {
			return false;
		} else {
			p_282301_.onReceiveVibration(
					p_282967_,
					blockpos,
					p_281498_.gameEvent(),
					p_281498_.getEntity(p_282967_).orElse(null),
					p_281498_.getProjectileOwner(p_282967_).orElse(null),
					VibrationSystem.Listener.distanceBetweenInBlocks(blockpos, blockpos1)
			);
			p_283447_.setCurrentVibration(null);
			return true;
		}
	}

	private static boolean areAdjacentChunksTicking(Level p_282735_, BlockPos p_281722_) {
		ChunkPos chunkpos = new ChunkPos(p_281722_);

		for (int i = chunkpos.x - 1; i <= chunkpos.x + 1; ++i) {
			for (int j = chunkpos.z - 1; j <= chunkpos.z + 1; ++j) {
				if (!p_282735_.shouldTickBlocksAt(ChunkPos.asLong(i, j)) || p_282735_.getChunkSource().getChunkNow(i, j) == null) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_218348_) {
		Level level = this.level();
		if (LookUtils.isVibrationAvaiable(this)) {
			if (level instanceof ServerLevel serverlevel) {
				p_218348_.accept(this.dynamicVibrationListener, serverlevel);
			}
		}
	}
}
