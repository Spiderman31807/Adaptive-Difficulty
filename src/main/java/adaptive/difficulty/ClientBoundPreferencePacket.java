package adaptive.difficulty;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.Difficulty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.registries.Registries;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record ClientBoundPreferencePacket(ResourceKey<Level> dimension, Difficulty difficulty, boolean hardcore) implements CustomPacketPayload {

	public static final Type<ClientBoundPreferencePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdaptiveDifficultyMod.MODID, "preference_packet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundPreferencePacket> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ClientBoundPreferencePacket update) -> {
		buffer.writeUtf(update.dimension.location().getPath());
		buffer.writeInt(update.difficulty.getId());
		buffer.writeBoolean(update.hardcore);
	}, (RegistryFriendlyByteBuf buffer) -> new ClientBoundPreferencePacket(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(buffer.readUtf())), Difficulty.byId(buffer.readInt()), buffer.readBoolean()));
	@Override
	public Type<ClientBoundPreferencePacket> type() {
		return TYPE;
	}

	public static void handleData(final ClientBoundPreferencePacket update, final IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND) {
			context.enqueueWork(() -> {
				ClientPref.setDimensionPref(update.dimension, update.difficulty, update.hardcore, false);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AdaptiveDifficultyMod.addNetworkMessage(ClientBoundPreferencePacket.TYPE, ClientBoundPreferencePacket.STREAM_CODEC, ClientBoundPreferencePacket::handleData);
	}
}
