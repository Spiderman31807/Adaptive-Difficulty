package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.LivingEntity;

import adaptive.difficulty.DifficultyHolder;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "canBeSeenAsEnemy", at = @At("RETURN"), cancellable = true)
	public void canBeSeenAsEnemy(CallbackInfoReturnable<Boolean> callback) {
		if (this instanceof DifficultyHolder holder && holder.isPeaceful())
			callback.setReturnValue(false);
	}
}
