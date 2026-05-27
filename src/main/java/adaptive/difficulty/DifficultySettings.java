package adaptive.difficulty;

import net.minecraft.world.level.Level;
import net.minecraft.world.Difficulty;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.registries.Registries;

import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public record DifficultySettings(HashSet<DimensionDifficulty> dimensions, Difficulty difficulty, boolean hardcore) {
	public static final DifficultySettings Default = new DifficultySettings(new HashSet(), Difficulty.NORMAL, false);
	
	public static final MapCodec<DifficultySettings> MAP_CODEC = RecordCodecBuilder.mapCodec(
		builder -> builder.group(
	        DimensionDifficulty.SET_CODEC.fieldOf("dimensions").forGetter(DifficultySettings::dimensions),
	        Difficulty.CODEC.fieldOf("difficulty").forGetter(DifficultySettings::difficulty),
	        Codec.BOOL.fieldOf("hardcore").forGetter(DifficultySettings::hardcore)
	    ).apply(builder, DifficultySettings::new)
    );
    
	public static final Codec<DifficultySettings> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, DifficultySettings> STREAM_CODEC = StreamCodec.composite(
        DimensionDifficulty.SET_STREAM, DifficultySettings::dimensions,
        Difficulty.STREAM_CODEC, DifficultySettings::difficulty,
        ByteBufCodecs.BOOL, DifficultySettings::hardcore,
        DifficultySettings::new
    );

    public void update(ResourceKey<Level> level, Player player) {
    	if(this.dimension(level).orElse(null) instanceof DimensionDifficulty dimension)
    		this.update(dimension.difficulty(), dimension.hardcore(), player);
    }

    public void update(Difficulty difficulty, Player player) {
    	this.update(difficulty, hardcore, player);
    }

    public void update(boolean hardcore, Player player) {
    	this.update(difficulty, hardcore, player);
    }

    public void update(Difficulty difficulty, boolean hardcore, Player player) {
    	player.setData(DataAttachments.Difficulty, new DifficultySettings(this.dimensions, difficulty, hardcore));
    }

    public void refresh(Player player) {
    	player.setData(DataAttachments.Difficulty, this);
    }

    public void set(ResourceKey<Level> level, Difficulty difficulty, boolean hardcore) {
    	this.remove(level);
    	this.dimensions.add(new DimensionDifficulty(level, difficulty, hardcore));
    }

    public void remove(ResourceKey<Level> level) {
    	if(this.dimension(level).orElse(null) instanceof DimensionDifficulty dimension)
    		this.dimensions.remove(dimension);
    }

    public Difficulty get() {
    	return this.difficulty;
    }

    public boolean hardcore() {
    	return this.hardcore;
    }

    public Optional<DimensionDifficulty> dimension(ResourceKey<Level> level) {
    	for(DimensionDifficulty levelDifficulty : this.dimensions) {
    		if(levelDifficulty.dimension().equals(level))
    			return Optional.of(levelDifficulty);
    	}

    	return Optional.empty();
    }

    public Difficulty get(ResourceKey<Level> level) {
    	return this.get(level, this.difficulty);
    }

    public Difficulty get(ResourceKey<Level> level, Difficulty fallback) {
    	Optional<DimensionDifficulty> levelDifficulty = this.dimension(level);
    	return levelDifficulty.isPresent() ? levelDifficulty.get().difficulty() : fallback;
    }

    public boolean hardcore(ResourceKey<Level> level) {
    	return this.hardcore(level, this.hardcore);
    }

    public boolean hardcore(ResourceKey<Level> level, boolean fallback) {
    	Optional<DimensionDifficulty> levelDifficulty = this.dimension(level);
    	return levelDifficulty.isPresent() ? levelDifficulty.get().hardcore() : fallback;
    }

	public record DimensionDifficulty(ResourceKey<Level> dimension, Difficulty difficulty, boolean hardcore) {
		public static final Codec<DimensionDifficulty> CODEC = RecordCodecBuilder.create(
	        builder -> builder.group(
	            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DimensionDifficulty::dimension),
	            Difficulty.CODEC.fieldOf("difficulty").forGetter(DimensionDifficulty::difficulty),
	            Codec.BOOL.fieldOf("hardcore").forGetter(DimensionDifficulty::hardcore)
	        ).apply(builder, DimensionDifficulty::new)
	    );
	    
	    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionDifficulty> STREAM_CODEC = StreamCodec.composite(
	        ResourceKey.streamCodec(Registries.DIMENSION),
	        DimensionDifficulty::dimension,
	        Difficulty.STREAM_CODEC,
	        DimensionDifficulty::difficulty,
	        ByteBufCodecs.BOOL,
	        DimensionDifficulty::hardcore,
	        DimensionDifficulty::new
	    );
	    
	    public static final Codec<HashSet<DimensionDifficulty>> SET_CODEC = DimensionDifficulty.CODEC.listOf().xmap(HashSet::new, ArrayList::new);

		public static final StreamCodec<RegistryFriendlyByteBuf, List<DimensionDifficulty>> LIST_STREAM = DimensionDifficulty.STREAM_CODEC.apply(ByteBufCodecs.list());
	    public static final StreamCodec<RegistryFriendlyByteBuf, HashSet<DimensionDifficulty>> SET_STREAM = LIST_STREAM.map(HashSet::new, ArrayList::new);
	}
}