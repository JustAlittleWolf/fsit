package dev.rvbsm.fsit;

import dev.rvbsm.fsit.config.FSitConfig;
import dev.rvbsm.fsit.config.FSitConfigManager;
import dev.rvbsm.fsit.entity.SeatEntity;
import dev.rvbsm.fsit.event.InteractBlockCallback;
import dev.rvbsm.fsit.event.InteractPlayerCallback;
import dev.rvbsm.fsit.event.PlayerConnectionCallbacks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FSitMod implements ModInitializer {

	private static final String MOD_ID = "fsit";
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final Map<UUID, ScheduledFuture<Boolean>> scheduledTasks = new LinkedHashMap<>();
	private static final Set<UUID> sneakedPlayers = new LinkedHashSet<>();
	private static boolean sneakDetect = false;

	@Contract(pure = true)
	public static @NotNull String getTranslationKey(String type, String id) {
		return String.join(".", type, FSitMod.MOD_ID, id);
	}

	public static String getModId() {
		return FSitMod.MOD_ID;
	}

	public static boolean isNeedSeat(@NotNull PlayerEntity player) {
		return sneakedPlayers.contains(player.getUuid()) && player.getPitch() >= FSitConfig.minAngle.getValue();
	}

	public static void setSneakDetect() {
		FSitMod.sneakDetect = true;
		FSitMod.scheduledTasks.put(UUID.randomUUID(), scheduler.schedule(() -> FSitMod.sneakDetect = false,
						FSitConfig.sneakDelay.getValue(), TimeUnit.MILLISECONDS));
	}

	public static void addSneaked(@NotNull PlayerEntity player) {
		if (!FSitConfig.sneakSit.getValue()) return;
		if (!FSitMod.sneakDetect) return;
		final UUID playerUid = player.getUuid();
		if (!FSitMod.sneakedPlayers.contains(playerUid) && player.getPitch() >= FSitConfig.minAngle.getValue()) {
			FSitMod.sneakedPlayers.add(playerUid);
			FSitMod.scheduledTasks.put(playerUid, scheduler.schedule(() -> FSitMod.sneakedPlayers.remove(playerUid),
							FSitConfig.sneakDelay.getValue(), TimeUnit.MILLISECONDS));
		}
	}

	private static void clearSneaked(@NotNull PlayerEntity player) {
		final UUID playerUid = player.getUuid();
		final ScheduledFuture<Boolean> task = FSitMod.scheduledTasks.get(playerUid);
		if (task != null) task.cancel(true);

		FSitMod.sneakedPlayers.remove(playerUid);
		FSitMod.scheduledTasks.remove(playerUid);
	}

	public static void spawnSeat(@NotNull PlayerEntity player, @NotNull World world, Vec3d pos) {
		final SeatEntity seatEntity = new SeatEntity(world, pos);

		world.spawnEntity(seatEntity);
		player.startRiding(seatEntity, true);
		FSitMod.clearSneaked(player);
	}

	@Override
	public void onInitialize() {
		FSitConfigManager.load();

		UseBlockCallback.EVENT.register(InteractBlockCallback::interactBlock);
		UseEntityCallback.EVENT.register(InteractPlayerCallback::interactPlayer);
		ServerPlayConnectionEvents.DISCONNECT.register(PlayerConnectionCallbacks::onDisconnect);
	}
}
