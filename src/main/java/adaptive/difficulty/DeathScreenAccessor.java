package adaptive.difficulty;

import net.minecraft.network.chat.Component;

public interface DeathScreenAccessor {
	abstract Component getDeathMessage();

	abstract boolean isHardcore();
}
