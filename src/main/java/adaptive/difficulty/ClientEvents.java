package adaptive.difficulty;

import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void openGUI(ScreenEvent.Opening event) {
		if (event.getNewScreen() instanceof DeathScreenAccessor accessor && Minecraft.getInstance().player instanceof DifficultyHolder holder) {
			boolean playingHardcore = holder.isHardcoreEnabled();
			if (accessor.isHardcore() != playingHardcore)
				event.setNewScreen(new DeathScreen(accessor.getDeathMessage(), playingHardcore));
		}
	}
}
