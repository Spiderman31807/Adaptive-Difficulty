package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerPlayer;

import adaptive.difficulty.DataAttachments;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	@Inject(method = "tickRegeneration", at = @At(value = "HEAD"), cancellable = true)
	public void tickRegeneration(CallbackInfo callback) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (player.getExistingDataOrNull(DataAttachments.Difficulty).get() == Difficulty.PEACEFUL && player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
			if (player.tickCount % 20 == 0) {
				float saturation = player.getFoodData().getSaturationLevel();
				if (saturation < 20)
					player.getFoodData().setSaturation(saturation + 1);
				if (player.getHealth() < player.getMaxHealth())
					player.heal(1);
			}
			if (player.tickCount % 10 == 0 && player.getFoodData().needsFood())
				player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 1);
		}
		callback.cancel();
	}
}