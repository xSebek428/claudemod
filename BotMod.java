package com.example.botmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotMod implements ClientModInitializer {
    public static final String MOD_ID = "botmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        BotConfig.load();

        // Klawisz domyslnie B; do zmiany w Opcje > Sterowanie
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.botmod.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "key.category.botmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new BotScreen(null));
                }
            }
        });

        LOGGER.info("Bot Panel zaladowany.");
    }
}
