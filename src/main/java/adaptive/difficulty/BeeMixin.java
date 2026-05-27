package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import adaptive.difficulty.DifficultySettings;
import adaptive.difficulty.DataAttachments;

@Mixin(Bee.class)
public abstract class BeeMixin {
	@ModifyArg(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/world/effect/MobEffect;IIZ)V"), index = 1)
	private int modifyPoisonDuration(int duration, ServerLevel level, Entity target) {
		if (target.getExistingDataOrNull(DataAttachments.Difficulty.get()) instanceof DifficultySettings settings) {
			return switch (settings.get()) {
				default -> 0;
				case NORMAL -> 10 * 20;
				case HARD -> 18 * 20;
			};
		}
		return duration;
	}
}