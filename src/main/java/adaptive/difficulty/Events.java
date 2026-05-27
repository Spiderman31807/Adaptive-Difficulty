package adaptive.difficulty;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.server.level.ServerPlayer;

@EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new ClientBoundChangedDimensionPacket(event.getFrom(), event.getTo()));
	}
}
