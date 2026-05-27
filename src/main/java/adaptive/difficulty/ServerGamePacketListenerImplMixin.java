package adaptive.difficulty.mixins;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import adaptive.difficulty.DifficultySettings;
import adaptive.difficulty.DataAttachments;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	@Redirect(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isHardcore()Z"))
	public boolean handleClientCommandHardcore(MinecraftServer server) {
		return this.player.getExistingDataOrNull(DataAttachments.Difficulty) instanceof DifficultySettings settings ? settings.hardcore() : server.isHardcore();
	}
}