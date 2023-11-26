package baguchan.onlylooking.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.AngerManagement;

public interface IHearSound {
    int getSoundCooldown();

    void setSoundCooldown(int soundCooldown);

    AngerManagement getAngerManagement();

    void increaseAngerAt(Entity entity);

    void increaseAngerAt(Entity entity, int tick, boolean b);
}
