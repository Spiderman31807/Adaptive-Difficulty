package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import adaptive.difficulty.DifficultyHolder;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(CaveSpider.class)
public abstract class CaveSpiderMixin {
	@ModifyConstant(method = "doHurtTarget", constant = @Constant(intValue = 0))
	private int modifyPoisonDefault(int original, ServerLevel server, Entity target) {
		if (target instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 7;
				case HARD -> 15;
			};
		}
		return original;
	}

	@ModifyConstant(method = "doHurtTarget", constant = @Constant(intValue = 7))
	private int modifyPoisonNormal(int original, ServerLevel server, Entity target) {
		if (target instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 7;
				case HARD -> 15;
			};
		}
		return original;
	}

	@ModifyConstant(method = "doHurtTarget", constant = @Constant(intValue = 15))
	private int modifyPoisonHard(int original, ServerLevel server, Entity target) {
		if (target instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 7;
				case HARD -> 15;
			};
		}
		return original;
	}
}
