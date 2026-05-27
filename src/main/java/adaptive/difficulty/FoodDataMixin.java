package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.food.FoodData;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerPlayer;

import adaptive.difficulty.DifficultySettings;
import adaptive.difficulty.DataAttachments;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
	@ModifyVariable(method = "tick", at = @At(value = "STORE"))
	private Difficulty tick(Difficulty original, ServerPlayer player) {
		return player.getExistingDataOrNull(DataAttachments.Difficulty.get()) instanceof DifficultySettings settings ? settings.get() : original;
	}
}