package baguchan.onlylooking;

import baguchan.onlylooking.api.IHearSound;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

import javax.annotation.Nullable;
import java.util.Optional;

public class VibrationUserMonster implements VibrationSystem.User {
    private static final int GAME_EVENT_LISTENER_RANGE = 16;
    private final PositionSource positionSource;
    private final PathfinderMob pathfinderMob;

    public VibrationUserMonster(PathfinderMob pathfinderMob) {
        this.positionSource = new EntityPositionSource(pathfinderMob, pathfinderMob.getEyeHeight());
        this.pathfinderMob = pathfinderMob;
    }

    @Override
    public int getListenerRadius() {
        return ModConfigs.COMMON.VIBRATION_RANGE.get();
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return ModTags.GameEvents.MONSTER_CAN_LISTEN;
    }


    @Override
    public boolean canReceiveVibration(ServerLevel p_282574_, BlockPos p_282323_, GameEvent p_283003_, GameEvent.Context p_282515_) {
        if (LookUtils.isVibrationAvaiable(this.pathfinderMob)) {
            if (!this.pathfinderMob.isNoAi() && this.pathfinderMob instanceof IHearSound iHearSound && !(this.pathfinderMob instanceof Warden) && iHearSound.getSoundCooldown() <= 0
                    && p_282574_.getWorldBorder().isWithinBounds(p_282323_)) {
                Entity entity = p_282515_.sourceEntity();
                if ((entity instanceof Enemy) || !(this.pathfinderMob instanceof Enemy) || (entity instanceof LivingEntity monster) && !this.pathfinderMob.canAttack(monster)) {
                    return false;
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onReceiveVibration(
            ServerLevel p_281325_, BlockPos p_282386_, GameEvent p_282261_, @Nullable Entity p_281438_, @Nullable Entity p_282582_, float p_283699_
    ) {
        if (!this.pathfinderMob.isDeadOrDying() && this.pathfinderMob instanceof IHearSound iHearSound && iHearSound.getSoundCooldown() <= 0) {
            ((IHearSound) this.pathfinderMob).setSoundCooldown(25);
            BlockPos blockpos = p_282386_;
            if (p_282582_ != null && !this.pathfinderMob.hasLineOfSight(p_282582_)) {
                if (this.pathfinderMob.closerThan(p_282582_, 30.0)) {
                    if (p_282582_ instanceof LivingEntity living && this.pathfinderMob.canAttack(living)) {
                        blockpos = p_282582_.blockPosition();
                    }

                    iHearSound.increaseAngerAt(p_282582_);
                }
            }

            if (this.pathfinderMob.getTarget() == null) {
                this.pathfinderMob.getLookControl().setLookAt(blockpos.getCenter());
                Optional<LivingEntity> optional = iHearSound.getAngerManagement().getActiveEntity();
                if (p_282582_ != null && optional.isPresent() && p_282582_ == optional.get()) {
                    if (iHearSound.getAngerManagement().getActiveAnger(p_282582_) > 60) {
                        this.pathfinderMob.getNavigation().moveTo(p_282582_, 0.8F);
                    }
                }
                if (p_281438_ != null && optional.isPresent() && p_281438_ == optional.get()) {
                    if (iHearSound.getAngerManagement().getActiveAnger(p_281438_) > 60) {
                        this.pathfinderMob.getNavigation().moveTo(p_281438_, 0.8F);
                    }
                }
            }
        }
    }
}