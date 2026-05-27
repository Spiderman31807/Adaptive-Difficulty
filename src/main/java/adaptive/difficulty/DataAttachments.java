package adaptive.difficulty;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DataAttachments {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AdaptiveDifficultyMod.MODID);
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<DifficultySettings>> Difficulty = ATTACHMENT_TYPES.register("difficulty",
			() -> AttachmentType.builder(() -> DifficultySettings.Default).serialize(DifficultySettings.MAP_CODEC).sync(DifficultySettings.STREAM_CODEC).copyOnDeath().build());
}