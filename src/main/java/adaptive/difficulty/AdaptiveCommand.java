package adaptive.difficulty;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;

@EventBusSubscriber
public class AdaptiveCommand {
	private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType(difficulty -> Component.translatableEscape("commands.difficulty.failure", difficulty));
	private static final Component disabled = Component.translatable("addServer.resourcePack.disabled");
	private static final Component enabled = Component.translatable("addServer.resourcePack.enabled");

	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("adaptive");
		for (Difficulty difficulty : Difficulty.values()) {
			builder.then(Commands.literal(difficulty.getKey()).executes(arguments -> setDifficulty(arguments.getSource(), difficulty)));

			builder.then(Commands.literal("preferences").then(Commands.argument("dimension", DimensionArgument.dimension()).then(Commands.literal(difficulty.getKey()).then(Commands.argument("hardcore", BoolArgumentType.bool()).executes(arguments -> {
				if (arguments.getSource().getEntity() instanceof ServerPlayer player) {
					boolean hardcore = BoolArgumentType.getBool(arguments, "hardcore");
					ResourceKey<Level> dimension = DimensionArgument.getDimension(arguments, "dimension").dimension();
					PacketDistributor.sendToPlayer(player, new ClientBoundPreferencePacket(dimension, difficulty, hardcore));
					arguments.getSource().sendSuccess(() -> Component.translatable("commands.adaptive.preference", dimension.location().getPath(), difficulty.getKey(), (hardcore ? enabled : disabled).getString()), false);
					return 15;
				}
				return 0;
			})))));
		}
		builder.then(Commands.literal("hardcore").then(Commands.argument("enabled", BoolArgumentType.bool()).executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof Player player && player instanceof DifficultyHolder holder) {
				boolean enable = BoolArgumentType.getBool(arguments, "enabled");
				if (holder.isHardcoreEnabled() == enable) {
					arguments.getSource().sendFailure(Component.translatable("commands.adaptive.already.hardcore", (enable ? enabled : disabled).getString()));
					return 0;
				}
				holder.setHardcoreEnabled(enable);
				arguments.getSource().sendSuccess(() -> Component.translatable("commands.adaptive.hardcore", (enable ? enabled : disabled).getString()), false);
				return 15;
			}
			return 0;
		})).executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof Player player && player instanceof DifficultyHolder holder) {
				boolean isHardcore = holder.isHardcoreEnabled();
				Component message = Component.translatable("options.difficulty.hardcore").append(" ").append((isHardcore ? enabled : disabled).getString());
				arguments.getSource().sendSuccess(() -> message, false);
				return isHardcore ? 15 : 0;
			}
			return 0;
		}));
		builder.executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof Player player && player instanceof DifficultyHolder holder) {
				if (!holder.hasAdaptiveDifficulty()) {
					arguments.getSource().sendFailure(Component.translatable("commands.adaptive.query.fail"));
					return 0;
				}
				Difficulty difficulty = holder.getDifficulty(arguments.getSource().getLevel().getDifficulty());
				arguments.getSource().sendSuccess(() -> Component.translatable("commands.difficulty.query", difficulty.getDisplayName()), false);
				return difficulty.getId();
			}
			return 0;
		});
		event.getDispatcher().register(builder);
	}

	public static int setDifficulty(CommandSourceStack source, Difficulty difficulty) throws CommandSyntaxException {
		if (source.getEntity() instanceof Player player && player instanceof DifficultyHolder holder) {
			if (holder.hasAdaptiveDifficulty() && holder.getAdaptiveDifficulty() == difficulty) {
				throw ERROR_ALREADY_DIFFICULT.create(difficulty.getKey());
			} else {
				holder.setAdaptiveDifficulty(difficulty);
				source.sendSuccess(() -> Component.translatable("commands.difficulty.success", difficulty.getDisplayName()), true);
			}
		}
		return 0;
	}
}
