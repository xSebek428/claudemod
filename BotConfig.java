package com.example.botmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Prosta konfiguracja zapisywana w config/botmod.json (pamieta ostatnie ustawienia). */
public final class BotConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("botmod.json");

    public static String nick = "";
    public static String command = "";
    public static BotMode mode = BotMode.GUI;

    private static class Data {
        String nick = "";
        String command = "";
        String mode = BotMode.GUI.name();
    }

    private BotConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) return;
        try {
            Data d = GSON.fromJson(Files.readString(FILE), Data.class);
            if (d == null) return;
            nick = d.nick == null ? "" : d.nick;
            command = d.command == null ? "" : d.command;
            mode = BotMode.valueOf(d.mode);
        } catch (IOException | IllegalArgumentException e) {
            BotMod.LOGGER.warn("Nie udalo sie wczytac konfiguracji, uzywam domyslnej.", e);
        }
    }

    public static void save() {
        Data d = new Data();
        d.nick = nick;
        d.command = command;
        d.mode = mode.name();
        try {
            Files.writeString(FILE, GSON.toJson(d));
        } catch (IOException e) {
            BotMod.LOGGER.warn("Nie udalo sie zapisac konfiguracji.", e);
        }
    }
}
