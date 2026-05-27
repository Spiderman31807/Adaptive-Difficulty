package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.DeathScreen;

import adaptive.difficulty.DeathScreenAccessor;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin implements DeathScreenAccessor {
	@Shadow
	@Final
	private Component causeOfDeath;
	@Shadow
	@Final
	private boolean hardcore;

	@Override
	public Component getDeathMessage() {
		return this.causeOfDeath;
	}

	@Override
	public boolean isHardcore() {
		return this.hardcore;
	}
}
