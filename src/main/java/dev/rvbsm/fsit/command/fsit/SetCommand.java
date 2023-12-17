package dev.rvbsm.fsit.command.fsit;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.rvbsm.fsit.FSitMod;
import dev.rvbsm.fsit.command.CommandArgument;
import dev.rvbsm.fsit.command.Commandish;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public class SetCommand implements Commandish<ServerCommandSource> {

	@Override
	public String name() {
		return "set";
	}

	@Override
	public List<CommandArgument> arguments() {
		return List.of(CommandArgument.CONFIG_KEY, CommandArgument.CONFIG_VALUE);
	}

	@Override
	public boolean requires(ServerCommandSource src) {
		return src.hasPermissionLevel(2);
	}

	@Override
	public int executes(CommandContext<ServerCommandSource> ctx) {
		final ServerCommandSource src = ctx.getSource();
		final String key = ctx.getArgument(CommandArgument.CONFIG_KEY.getName(), String.class);
		final String value = ctx.getArgument(CommandArgument.CONFIG_VALUE.getName(), String.class);
		final Object cfgValue = FSitMod.getConfigManager().getByFlat(key);

		final Object newCfgValue;
		try {
			newCfgValue = new Gson().fromJson(value, cfgValue.getClass());
		} catch (JsonSyntaxException e) {
			src.sendError(Text.of(e.getMessage()));
			return -1;
		}

		FSitMod.getConfigManager().updateByFlat(key, newCfgValue);
		src.sendMessage(Text.of("Updated %s ➡ %s".formatted(key, newCfgValue)));

		return Command.SINGLE_SUCCESS;
	}
}
