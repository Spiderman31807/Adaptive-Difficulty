package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.WitherSkull;

import adaptive.difficulty.DifficultySettings;
import adaptive.difficulty.DataAttachments;

@Mixin(WitherSkull.class)
public abstract class WitherSkullMixin {
	@ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/world/effect/MobEffect;IIZ)V"), index = 1)
	private int modifyWitherDuration(int duration, EntityHitResult result) {
		if (result.getEntity().getExistingDataOrNull(DataAttachments.Difficulty.get()) instanceof DifficultySettings settings) {
			return switch (settings.get()) {
				default -> 0;
				case NORMAL -> 10 * 20;
				case HARD -> 40 * 20;
			};
		}
		return duration;
	}
}