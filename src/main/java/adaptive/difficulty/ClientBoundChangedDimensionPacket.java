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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Minecraft;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record ClientBoundChangedDimensionPacket(ResourceKey<Level> oldDimension, ResourceKey<Level> newDimension) implements CustomPacketPayload {
	public static final Type<ClientBoundChangedDimensionPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdaptiveDifficultyMod.MODID, "changed_dimension_packet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundChangedDimensionPacket> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ClientBoundChangedDimensionPacket update) -> {
		buffer.writeUtf(update.oldDimension.location().getPath());
		buffer.writeUtf(update.newDimension.location().getPath());
	}, (RegistryFriendlyByteBuf buffer) -> new ClientBoundChangedDimensionPacket(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(buffer.readUtf())), ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(buffer.readUtf()))));

	@Override
	public Type<ClientBoundChangedDimensionPacket> type() {
		return TYPE;
	}

	public static void handleData(final ClientBoundChangedDimensionPacket update, final IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND) {
			context.enqueueWork(() -> {
				if (ClientPref.hasDimensionPref(update.newDimension())) {
					Difficulty difficulty = ClientPref.getDifficultyPref(update.newDimension());
					boolean hardcore = ClientPref.getHardcorePref(update.newDimension());
					updateDifficulty(update.newDimension(), difficulty, hardcore);
				}
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void updateDifficulty(ResourceKey<Level> newDimension, Difficulty difficulty, boolean hardcore) {
		if (Minecraft.getInstance().player instanceof LocalPlayer player) {
			if (player.level().dimension() != newDimension) {
				AdaptiveDifficultyMod.queueServerWork(1, () -> {
					updateDifficulty(newDimension, difficulty, hardcore);
				});
				return;
			}
			player.connection.sendCommand("adaptive " + difficulty.getKey());
			player.connection.sendCommand("adaptive hardcore " + hardcore);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AdaptiveDifficultyMod.addNetworkMessage(ClientBoundChangedDimensionPacket.TYPE, ClientBoundChangedDimensionPacket.STREAM_CODEC, ClientBoundChangedDimensionPacket::handleData);
	}
}
