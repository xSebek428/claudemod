package com.example.botmod;

import net.minecraft.text.Text;

/** Dostepne tryby dzialania bota. */
public enum BotMode {
    GUI("gui.botmod.mode.gui"),
    SCREEN("gui.botmod.mode.screen"),
    SERVER("gui.botmod.mode.server");

    private final String translationKey;

    BotMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public Text label() {
        return Text.translatable(translationKey);
    }

    /** Nastepny tryb (cyklicznie) - uzywane przez przycisk wyboru. */
    public BotMode next() {
        BotMode[] all = values();
        return all[(ordinal() + 1) % all.length];
    }
}
