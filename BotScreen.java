package com.example.botmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class BotScreen extends Screen {
    private static final Pattern NICK_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
    private static final int FIELD_WIDTH = 220;

    private final Screen parent;
    private TextFieldWidget nickField;
    private TextFieldWidget commandField;
    private ButtonWidget modeButton;
    private Text errorText = Text.empty();

    public BotScreen(Screen parent) {
        super(Text.translatable("gui.botmod.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - FIELD_WIDTH / 2;
        int top = this.height / 2 - 70;

        nickField = new TextFieldWidget(this.textRenderer, left, top + 12, FIELD_WIDTH, 20,
                Text.translatable("gui.botmod.nick"));
        nickField.setMaxLength(16);
        nickField.setPlaceholder(Text.translatable("gui.botmod.nick.placeholder"));
        nickField.setText(BotConfig.nick);
        nickField.setChangedListener(s -> BotConfig.nick = s);
        this.addDrawableChild(nickField);

        commandField = new TextFieldWidget(this.textRenderer, left, top + 52, FIELD_WIDTH, 20,
                Text.translatable("gui.botmod.command"));
        commandField.setMaxLength(256);
        commandField.setPlaceholder(Text.translatable("gui.botmod.command.placeholder"));
        commandField.setText(BotConfig.command);
        commandField.setChangedListener(s -> BotConfig.command = s);
        this.addDrawableChild(commandField);

        modeButton = ButtonWidget.builder(modeLabel(), button -> {
            BotConfig.mode = BotConfig.mode.next();
            button.setMessage(modeLabel());
        }).dimensions(left, top + 80, FIELD_WIDTH, 20).build();
        this.addDrawableChild(modeButton);

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.botmod.start"), button -> onConfirm())
                .dimensions(left, top + 110, FIELD_WIDTH, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.botmod.cancel"), button -> this.close())
                .dimensions(left, top + 134, FIELD_WIDTH, 20).build());

        this.setInitialFocus(nickField);
    }

    private Text modeLabel() {
        return Text.translatable("gui.botmod.mode", BotConfig.mode.label());
    }

    private void onConfirm() {
        String nick = nickField.getText().trim();
        String command = commandField.getText().trim();
        MinecraftClient mc = this.client;

        if (!NICK_PATTERN.matcher(nick).matches()) {
            errorText = Text.translatable("gui.botmod.error.nick");
            return;
        }
        if (command.isEmpty()) {
            errorText = Text.translatable("gui.botmod.error.command");
            return;
        }
        if (mc == null || mc.player == null) {
            errorText = Text.translatable("gui.botmod.error.world");
            return;
        }

        BotConfig.save();
        BotMode mode = BotConfig.mode;

        mc.setScreen(null);
        BotRunner.run(mc, nick, command, mode);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int left = this.width / 2 - FIELD_WIDTH / 2;
        int top = this.height / 2 - 70;

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, top - 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("gui.botmod.nick"), left, top, 0xA0A0A0);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("gui.botmod.command"), left, top + 40, 0xA0A0A0);

        if (!errorText.getString().isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, errorText, this.width / 2, top + 162, 0xFF5555);
        }
    }

    @Override
    public void close() {
        BotConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
