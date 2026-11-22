package com.breakalarm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BreakAlarmClient implements ClientModInitializer {
	/** Ab dieser Balkenanzahl (von 13) schlägt der Alarm an. 1 = letzter Balken. */
	private static final int WARN_BAR_STEP = 1;

	private static boolean inDanger = false;
	private static AlarmSound alarm = null;

	public static boolean isInDanger() {
		return inDanger;
	}

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);

		HudElementRegistry.attachElementAfter(
				VanillaHudElements.MISC_OVERLAYS,
				Identifier.of("breakalarm", "warning"),
				(context, tickCounter) -> renderWarning(context));
	}

	private void onTick(MinecraftClient client) {
		inDanger = false;
		if (client.player != null && client.world != null) {
			ItemStack stack = client.player.getMainHandStack();
			inDanger = !stack.isEmpty()
					&& stack.isDamageable()
					&& stack.getItemBarStep() <= WARN_BAR_STEP;
		}

		SoundManager sounds = client.getSoundManager();
		if (inDanger) {
			if (alarm == null || !sounds.isPlaying(alarm)) {
				alarm = new AlarmSound();
				sounds.play(alarm);
			}
		} else if (alarm != null) {
			sounds.stop(alarm);
			alarm = null;
		}
	}

	private void renderWarning(DrawContext context) {
		if (!inDanger) return;

		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer font = client.textRenderer;

		// Blinken: 400 ms an / 400 ms aus
		if ((System.currentTimeMillis() / 400) % 2 == 0) return;

		float scale = 3.0f;
		int w = context.getScaledWindowWidth();
		int h = context.getScaledWindowHeight();

		context.getMatrices().pushMatrix();
		context.getMatrices().translate(w / 2.0f, h / 4.0f);
		context.getMatrices().scale(scale, scale);
		context.drawCenteredTextWithShadow(font, Text.literal("YOUR TOOL IS ABOUT TO BREAK"), 0, 0, 0xFFFF5555);
		context.getMatrices().popMatrix();
	}
}
