package adaptive.difficulty;

import net.minecraft.world.Difficulty;

public interface DifficultyHolder {
	abstract Difficulty getAdaptiveDifficulty();

	abstract void setAdaptiveDifficulty(Difficulty difficulty);

	abstract boolean isHardcoreEnabled();

	abstract void setHardcoreEnabled(boolean enabled);

	default boolean hasAdaptiveDifficulty() {
		return this.getAdaptiveDifficulty() != null;
	}

	default Difficulty getDifficulty(Difficulty worldDifficulty) {
		return this.hasAdaptiveDifficulty() ? this.getAdaptiveDifficulty() : worldDifficulty;
	}

	default boolean isPeaceful() {
		return this.hasAdaptiveDifficulty() && this.getAdaptiveDifficulty() == Difficulty.PEACEFUL;
	}
	
	default boolean isEasy() {
		return this.hasAdaptiveDifficulty() && this.getAdaptiveDifficulty() == Difficulty.EASY;
	}
	
	default boolean isNormal() {
		return this.hasAdaptiveDifficulty() && this.getAdaptiveDifficulty() == Difficulty.NORMAL;
	}
	default boolean isHard() {
		return this.hasAdaptiveDifficulty() && this.getAdaptiveDifficulty() == Difficulty.HARD;
	}
}
