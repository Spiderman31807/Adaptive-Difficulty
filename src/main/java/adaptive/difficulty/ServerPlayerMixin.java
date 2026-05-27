package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.level.ServerPlayer;

import adaptive.difficulty.DifficultyHolder;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@Inject(method = "restoreFrom", at = @At("HEAD"))
	public void restoreFrom(ServerPlayer player, boolean loadFully, CallbackInfo callback) {
		if (this instanceof DifficultyHolder holder && player instanceof DifficultyHolder refHolder) {
			holder.setAdaptiveDifficulty(refHolder.getAdaptiveDifficulty());
			holder.setHardcoreEnabled(refHolder.isHardcoreEnabled());
		}
	}
}
