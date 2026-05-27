package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.neoforged.neoforge.attachment.AttachmentHolder;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.Difficulty;

import adaptive.difficulty.DifficultySettings;
import adaptive.difficulty.DataAttachments;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "canBeSeenAsEnemy", at = @At("RETURN"), cancellable = true)
	public void canBeSeenAsEnemy(CallbackInfoReturnable<Boolean> callback) {
		if ((Object) this instanceof AttachmentHolder holder && holder.getExistingDataOrNull(DataAttachments.Difficulty.get()) instanceof DifficultySettings settings && settings.get() == Difficulty.PEACEFUL)
			callback.setReturnValue(false);
	}
}