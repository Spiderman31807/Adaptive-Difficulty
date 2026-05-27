package adaptive.difficulty;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.Minecraft;

import java.util.HashSet;

@EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void openGUI(ScreenEvent.Opening event) {
		if (event.getNewScreen() instanceof DeathScreenAccessor accessor && Minecraft.getInstance().player instanceof Player player) {
			boolean hardcore = player.getData(DataAttachments.Difficulty).hardcore();
			if (accessor.isHardcore() != hardcore)
				event.setNewScreen(new DeathScreen(accessor.getDeathMessage(), hardcore));
		}
	}

	@SubscribeEvent
	public static void login(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity().hasData(DataAttachments.Difficulty))
			return;
		WorldData data = ServerLifecycleHooks.getCurrentServer().getWorldData();
		DifficultySettings settings = new DifficultySettings(new HashSet(), data.getDifficulty(), data.isHardcore());
		event.getEntity().setData(DataAttachments.Difficulty, settings);
	}

	@SubscribeEvent
	public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity().getExistingDataOrNull(DataAttachments.Difficulty) instanceof DifficultySettings settings)
			settings.update(event.getTo(), event.getEntity());
	}
}