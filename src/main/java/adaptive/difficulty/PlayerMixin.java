package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.neoforged.neoforge.common.damagesource.IScalingFunction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;

import adaptive.difficulty.DataAttachments;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/damagesource/IScalingFunction;scaleDamage(Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/player/Player;FLnet/minecraft/world/Difficulty;)F"))
	public float modifyScaledDamage(IScalingFunction scaler, DamageSource source, Player target, float damage, Difficulty difficulty) {
		if (source.scalesWithDifficulty() && !(source.getEntity() instanceof Player)) {
			return switch (target.getData(DataAttachments.Difficulty).get()) {
				case PEACEFUL -> 0;
				case EASY -> Math.min(damage / 2 + 1, damage);
				case NORMAL -> damage;
				case HARD -> damage * 1f;
			};
		}
		
		return damage;
	}
}