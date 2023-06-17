package dev.rvbsm.fsit.packet;

import dev.rvbsm.fsit.FSitMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class RidePlayerC2SPacket {

	public static final Identifier RIDE_PLAYER_PACKET = new Identifier(FSitMod.getModId(), "ride_player");
	public static final Identifier RIDE_ACCEPT_PACKET = new Identifier(FSitMod.getModId(), "ride_accept");

	public static void receiveRequest(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		final UUID targetUid = new UUID(buf.readLong(), buf.readLong());
		if (!FSitMod.isModdedPlayer(targetUid)) return;

		final ServerPlayerEntity target = server.getPlayerManager().getPlayer(targetUid);
		if (target == null) return;

		final PacketByteBuf requestBuf = PacketByteBufs.create();
		final UUID issuerUid = player.getUuid();
		requestBuf.writeLong(issuerUid.getMostSignificantBits());
		requestBuf.writeLong(issuerUid.getLeastSignificantBits());

		ServerPlayNetworking.send(target, RIDE_PLAYER_PACKET, requestBuf);
	}

	public static void receiveAccept(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		final UUID issuerUid = new UUID(buf.readLong(), buf.readLong());
		final ServerPlayerEntity issuer = server.getPlayerManager().getPlayer(issuerUid);

		if (issuer != null && player.distanceTo(issuer) <= 3) issuer.startRiding(player);
	}

	public static void sendRequest(PlayerEntity target, PlayerEntity issuer) {
		final PacketByteBuf buf = PacketByteBufs.create();

		final UUID issuerUid = issuer.getUuid();
		buf.writeLong(issuerUid.getMostSignificantBits());
		buf.writeLong(issuerUid.getLeastSignificantBits());

		ServerPlayNetworking.send((ServerPlayerEntity) target, RIDE_PLAYER_PACKET, buf);
	}
}
