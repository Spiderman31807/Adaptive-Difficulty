package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;

import adaptive.difficulty.DifficultyHolder;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Redirect(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V"))
	private void modifyHardcoreHeartRendering(Gui instance, GuiGraphics graphics, Gui.HeartType heartType, int x, int y, boolean hardcore, boolean blinking, boolean halfHeart) {
		Player player = Minecraft.getInstance().player;
		boolean modifiedHardcore = player instanceof DifficultyHolder holder ? holder.isHardcoreEnabled() : hardcore;
		this.renderHeart(graphics, heartType, x, y, modifiedHardcore, blinking, halfHeart);
	}

	@Shadow
	abstract void renderHeart(GuiGraphics graphics, Gui.HeartType heartType, int x, int y, boolean hardcore, boolean blinking, boolean halfHeart);
}
