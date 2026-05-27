package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.WitherSkull;

import adaptive.difficulty.DifficultyHolder;

@Mixin(WitherSkull.class)
public abstract class WitherSkullMixin {
	@ModifyConstant(method = "onHitEntity", constant = @Constant(intValue = 0))
	public int modifyWitherDefault(int original, EntityHitResult result) {
		if (result.getEntity() instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 10;
				case HARD -> 40;
			};
		}
		return original;
	}

	@ModifyConstant(method = "onHitEntity", constant = @Constant(intValue = 10))
	public int modifyWitherNormal(int original, EntityHitResult result) {
		if (result.getEntity() instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 10;
				case HARD -> 40;
			};
		}
		return original;
	}

	@ModifyConstant(method = "onHitEntity", constant = @Constant(intValue = 40))
	public int modifyWitherHard(int original, EntityHitResult result) {
		if (result.getEntity() instanceof DifficultyHolder holder && holder.hasAdaptiveDifficulty()) {
			return switch (holder.getAdaptiveDifficulty()) {
				default -> 0;
				case NORMAL -> 10;
				case HARD -> 40;
			};
		}
		return original;
	}
}
