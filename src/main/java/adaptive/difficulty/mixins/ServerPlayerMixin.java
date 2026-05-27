package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerPlayer;

import adaptive.difficulty.DifficultyHolder;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@Redirect(method = "tickRegeneration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getDifficulty()Lnet/minecraft/world/Difficulty;"))
	public Difficulty modifyDifficultyForRegeneration(Level world) {
		Player player = (Player) (Object) this;
		return player instanceof DifficultyHolder holder ? holder.getDifficulty(world.getDifficulty()) : world.getDifficulty();
	}

	@Inject(method = "restoreFrom", at = @At("HEAD"))
	public void restoreFrom(ServerPlayer player, boolean loadFully, CallbackInfo callback) {
		if (this instanceof DifficultyHolder holder && player instanceof DifficultyHolder refHolder) {
			holder.setAdaptiveDifficulty(refHolder.getAdaptiveDifficulty());
			holder.setHardcoreEnabled(refHolder.isHardcoreEnabled());
		}
	}
}
