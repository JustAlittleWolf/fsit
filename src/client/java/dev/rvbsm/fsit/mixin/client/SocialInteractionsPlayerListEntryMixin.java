package dev.rvbsm.fsit.mixin.client;

import dev.rvbsm.fsit.FSitMod;
import dev.rvbsm.fsit.FSitModClient;
import dev.rvbsm.fsit.packet.RidePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mixin(SocialInteractionsPlayerListEntry.class)
public abstract class SocialInteractionsPlayerListEntryMixin {

	@Unique
	private static final Identifier BLOCKED_TEXTURE = new Identifier("fsit", "textures/gui/blocked_button.png");
	@Unique
	private static final Text BLOCK_BUTTON_TEXT = FSitMod.getTranslation("gui", "socialInteractions.block");
	@Unique
	private static final Text UNBLOCK_BUTTON_TEXT = FSitMod.getTranslation("gui", "socialInteractions.unblock");
	@Unique
	private static final Text DISABLED_BUTTON_TEXT = FSitMod.getTranslation("gui", "socialInteractions.disabled");

	@Shadow
	@Final
	private List<ClickableWidget> buttons;
	@Unique
	private ButtonWidget unblockButton;
	@Unique
	private ButtonWidget blockButton;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/SocialInteractionsPlayerListEntry;setShowButtonVisible(Z)V"))
	public void init(MinecraftClient client, SocialInteractionsScreen parent, UUID uuid, String name, Supplier<Identifier> skinTexture, boolean reportable, CallbackInfo ci) {
		this.blockButton = new TexturedButtonWidget(0, 0, 20, 20, 0, 0, 20, BLOCKED_TEXTURE, 64, 64, button -> {
			FSitModClient.addBlockedRider(uuid);
			setBlockButtonVisible(false);
			if (client.player != null && client.player.hasPassenger(entity -> entity.getUuid() == uuid))
				ClientPlayNetworking.send(new RidePacket(RidePacket.ActionType.REFUSE, uuid));
		}, BLOCK_BUTTON_TEXT);
		this.blockButton.setTooltip(Tooltip.of(this.blockButton.active ? BLOCK_BUTTON_TEXT : DISABLED_BUTTON_TEXT));
		this.blockButton.setTooltipDelay(10);
		this.blockButton.visible = !FSitModClient.isBlockedRider(uuid);
		this.restrictButton.active = FSitMod.getConfig().getRiding().isEnabled();

		this.unblockButton = new TexturedButtonWidget(0, 0, 20, 20, 20, 0, 20, BLOCKED_TEXTURE, 64, 64, button -> {
			FSitModClient.removeBlockedRider(uuid);
			setBlockButtonVisible(true);
		}, UNBLOCK_BUTTON_TEXT);
		this.unblockButton.setTooltip(Tooltip.of(this.unblockButton.active ? UNBLOCK_BUTTON_TEXT : DISABLED_BUTTON_TEXT));
		this.unblockButton.setTooltipDelay(10);
		this.unblockButton.visible = FSitModClient.isBlockedRider(uuid);
		this.allowButton.active = FSitMod.getConfig().getRiding().isEnabled();

		this.buttons.add(this.blockButton);
		this.buttons.add(this.unblockButton);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
		if (this.blockButton != null && this.unblockButton != null) {
			this.blockButton.setX(x + (entryWidth - this.blockButton.getWidth() - 4) - 24 * (this.buttons.size() - 2));
			this.blockButton.setY(y + (entryHeight - this.blockButton.getHeight()) / 2);
			this.blockButton.render(context, mouseX, mouseY, tickDelta);
			this.unblockButton.setX(x + (entryWidth - this.unblockButton.getWidth() - 4) - 24 * (this.buttons.size() - 2));
			this.unblockButton.setY(y + (entryHeight - this.unblockButton.getHeight()) / 2);
			this.unblockButton.render(context, mouseX, mouseY, tickDelta);
		}
	}

	@Unique
	private void setBlockButtonVisible(boolean blockButtonVisible) {
		this.blockButton.visible = blockButtonVisible;
		this.unblockButton.visible = !blockButtonVisible;
	}
}
