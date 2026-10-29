package com.example.botmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

/** Wykonuje "akcje bota" zaleznie od wybranego trybu. */
public final class BotRunner {
    private BotRunner() {}

    public static void run(MinecraftClient mc, String nick, String command, BotMode mode) {
        switch (mode) {
            case GUI -> mc.setScreen(new ConfirmScreen(
                    confirmed -> {
                        mc.setScreen(null);
                        if (confirmed) send(mc, command);
                    },
                    Text.translatable("gui.botmod.confirm.title"),
                    Text.translatable("gui.botmod.confirm.text", nick, command)
            ));
            case SCREEN -> {
                mc.inGameHud.setOverlayMessage(
                        Text.translatable("gui.botmod.screen.msg", nick, command), false);
                send(mc, command);
            }
            case SERVER -> {
                send(mc, command);
                if (mc.player != null) {
                    mc.player.sendMessage(
                            Text.translatable("gui.botmod.server.msg", nick, command), false);
                }
            }
        }
    }

    /** Wysyla komende (zaczynajaca sie od '/') lub zwykla wiadomosc czatu. */
    private static void send(MinecraftClient mc, String raw) {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null || mc.player == null) return;

        String text = raw.trim();
        if (text.startsWith("/")) {
            handler.sendChatCommand(text.substring(1));
        } else {
            handler.sendChatMessage(text);
        }
    }
}
