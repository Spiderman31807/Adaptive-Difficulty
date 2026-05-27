package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.neoforged.neoforge.common.damagesource.IScalingFunction;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;

import adaptive.difficulty.DifficultyHolder;

@Mixin(Player.class)
public abstract class PlayerMixin implements DifficultyHolder {
	private static final EntityDataAccessor adaptiveDiff = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor adaptiveHardcore = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);

	public SynchedEntityData playerEntityData() {
		return ((Player) (Object) this).getEntityData();
	}

	@Override
	public Difficulty getAdaptiveDifficulty() {
		int id = (int) this.playerEntityData().get(adaptiveDiff);
		return id == -1 ? null : Difficulty.byId(id);
	}

	@Override
	public void setAdaptiveDifficulty(Difficulty difficulty) {
		this.playerEntityData().set(adaptiveDiff, difficulty == null ? -1 : difficulty.getId());
	}

	@Override
	public boolean isHardcoreEnabled() {
		return (boolean) this.playerEntityData().get(adaptiveHardcore);
	}

	@Override
	public void setHardcoreEnabled(boolean enabled) {
		this.playerEntityData().set(adaptiveHardcore, enabled);
	}

	@Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/damagesource/IScalingFunction;scaleDamage(Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/player/Player;FLnet/minecraft/world/Difficulty;)F"))
	public float modifyScaledDamage(IScalingFunction scaler, DamageSource source, Player target, float damage, Difficulty difficulty) {
		if (source.scalesWithDifficulty()) {
			return switch (this.getDifficulty(difficulty)) {
				case PEACEFUL -> 0;
				case EASY -> Math.min(damage / 2 + 1, damage);
				case NORMAL -> damage;
				case HARD -> damage * 1f;
			};
		}
		return damage;
	}

	@Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getDifficulty()Lnet/minecraft/world/Difficulty;"))
	public Difficulty modifyDifficultyForRegeneration(Level world) {
		Player player = (Player) (Object) this;
		return player instanceof DifficultyHolder holder ? holder.getDifficulty(world.getDifficulty()) : world.getDifficulty();
	}

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	public void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo callback) {
		builder.define(adaptiveDiff, -1);
		builder.define(adaptiveHardcore, false);
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void addAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
		compound.putInt("adaptiveDiff", (int) this.playerEntityData().get(adaptiveDiff));
		compound.putBoolean("adaptiveHardcore", this.isHardcoreEnabled());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
		if (compound.contains("adaptiveDiff"))
			this.playerEntityData().set(adaptiveDiff, compound.getInt("adaptiveDiff"));
		this.setHardcoreEnabled(compound.getBoolean("adaptiveHardcore"));
	}
}
