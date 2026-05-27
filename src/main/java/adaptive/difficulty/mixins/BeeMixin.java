package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import adaptive.difficulty.DifficultyHolder;

@Mixin(Bee.class)
public abstract class BeeMixin {
	@ModifyConstant(method = "doHurtTarget", constant = @Constant(intValue = 0))
	private int modifyPoisonDefault(int original, ServerLevel server, Entity target) {
		if (target instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 10;
				case HARD -> 18;
			};
		}
		return original;
	}

	@ModifyConstant(method = "doHurtTarget", constant = @Constant(intValue = 10))
	private int modifyPoisonNormal(int original, ServerLevel server, Entity target) {
		if (target instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 10;
				case HARD -> 18;
			};
		}
		return original;
	}

	@ModifyConstant(method = "doHurtTarget", constant = @Constant(intValue = 18))
	private int modifyPoisonHard(int original, ServerLevel server, Entity target) {
		if (target instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 10;
				case HARD -> 18;
			};
		}
		return original;
	}
}
