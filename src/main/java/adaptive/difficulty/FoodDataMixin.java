package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Difficulty;

import adaptive.difficulty.DifficultyHolder;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
	@ModifyVariable(method = "tick", at = @At(value = "STORE"))
	private Difficulty tick(Difficulty original, Player player) {
		return player instanceof DifficultyHolder holder ? holder.getDifficulty(original) : original;
	}
}
