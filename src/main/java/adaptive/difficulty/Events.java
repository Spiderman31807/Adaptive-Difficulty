package adaptive.difficulty;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;

@EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void openGUI(ScreenEvent.Opening event) {
		if(event.getNewScreen() instanceof DeathScreenAccessor accessor && Minecraft.getInstance().player instanceof DifficultyHolder holder) {
			boolean playingHardcore = holder.isHardcoreEnabled();
			if(accessor.isHardcore() != playingHardcore)
				event.setNewScreen(new DeathScreen(accessor.getDeathMessage(), playingHardcore));
		}
	}

	@SubscribeEvent
	public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if(event.getEntity() instanceof ServerPlayer player)
			PacketDistributor.sendToPlayer(player, new ClientBoundChangedDimensionPacket(event.getFrom(), event.getTo()));
	}
}
