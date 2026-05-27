package adaptive.difficulty;

import net.minecraft.world.level.Level;
import net.minecraft.world.Difficulty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;

import java.util.Map;
import java.util.HashMap;

import java.nio.file.Path;
import java.nio.file.Files;

import java.io.IOException;

import com.mojang.datafixers.util.Pair;

public class ClientPref {
	public static final Map<ResourceKey<Level>, Pair<Difficulty, Boolean>> dimensionPrefs = new HashMap();

	public static boolean getHardcorePref(ResourceKey<Level> dimension) {
		if (dimensionPrefs.containsKey(dimension))
			return dimensionPrefs.get(dimension).getSecond();
		return false;
	}

	public static Difficulty getDifficultyPref(ResourceKey<Level> dimension) {
		if (dimensionPrefs.containsKey(dimension))
			return dimensionPrefs.get(dimension).getFirst();
		return null;
	}

	public static void setDimensionPref(ResourceKey<Level> dimension, Difficulty difficulty, boolean hardcore, boolean loadedFromFile) {
		dimensionPrefs.put(dimension, Pair.of(difficulty, hardcore));
		if(!loadedFromFile)
			savePrefsFile();
	}

	public static boolean hasDimensionPref(ResourceKey<Level> dimension) {
		return dimensionPrefs.containsKey(dimension);
	}

	public static void savePrefsFile() {
		CompoundTag savedPrefs = savePrefs();
		if (savedPrefs.size() == 0)
			return;
		try {
			Path path = Path.of("", "config", "Adaptive Preferences.dat");
			if (path.getParent() instanceof Path directories)
				Files.createDirectories(directories);
			NbtIo.write(savedPrefs, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadPrefsFile() {
		try {
			loadPrefs(NbtIo.read(Path.of("", "config", "Adaptive Preferences.dat")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CompoundTag savePrefs() {
		CompoundTag compound = new CompoundTag();
		for (Map.Entry<ResourceKey<Level>, Pair<Difficulty, Boolean>> entry : dimensionPrefs.entrySet()) {
			ResourceKey<Level> dimension = entry.getKey();
			Difficulty difficulty = entry.getValue().getFirst();
			boolean hardcore = entry.getValue().getSecond();
			CompoundTag dimensionTag = new CompoundTag();
			dimensionTag.putInt("difficulty", difficulty.getId());
			dimensionTag.putBoolean("hardcore", hardcore);
			compound.put(dimension.location().getPath(), dimensionTag);
		}
		return compound;
	}

	public static void loadPrefs(CompoundTag compound) {
		if(compound == null)
			return;
		for (String dimensionKey : compound.getAllKeys()) {
			ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimensionKey));
			CompoundTag dimensionTag = compound.getCompound(dimensionKey);
			Difficulty difficulty = Difficulty.byId(dimensionTag.getInt("difficulty"));
			boolean hardcore = dimensionTag.getBoolean("hardcore");
			setDimensionPref(dimension, difficulty, hardcore, true);
		}
	}
}
