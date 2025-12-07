package dev.ng5m.mcproxy.client;

import dev.ng5m.mcproxy.Proxy;
import dev.ng5m.mcproxy.ProxyListParseException;
import dev.ng5m.mcproxy.State;
import dev.ng5m.mcproxy.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static dev.ng5m.mcproxy.Util.error;
import static dev.ng5m.mcproxy.Util.success;

public class SelectProxyScreen extends Screen {
    private final Screen parent;
    private Text message;

    public SelectProxyScreen(Screen parent) {
        super(Text.literal("Select Proxy"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        TextFieldWidget filePathInput = new TextFieldWidget(textRenderer, width / 2 - 100, 50, 200, 20, Text.literal("File path"));
        AtomicReference<Path> pathRef = new AtomicReference<>();
        filePathInput.setChangedListener(s -> {
            pathRef.set(Path.of(s));
        });

        addDrawableChild(filePathInput);

        addDrawableChild(
                ButtonWidget.builder(Text.literal("Choose File"), button -> {
                            @Nullable File file;
                            try {
                                file = Util.chooseFile("Text Files", "txt");
                            } catch (HeadlessException x) {
                                message = error("Unable to open file dialog");
                                return;
                            }

                            if (file == null) {
                                message = error("Invalid file");
                                return;
                            }

                            Set<Proxy> loadedProxies;
                            try {
                                loadedProxies = Util.loadProxies(file.toPath());
                            } catch (IOException e) {
                                message = error("Failed to read file");
                                return;
                            } catch (ProxyListParseException e) {
                                message = error("Failed to parse proxy list: " + e.getMessage());
                                return;
                            }

                            State.availableProxies.addAll(loadedProxies);
                            message = success("Loaded %d proxies".formatted(loadedProxies.size()));
                        })
                        .dimensions(width / 2 - 100, 70, 100, 20)
                        .build()
        );

        addDrawableChild(ButtonWidget.builder(Text.literal("Load File"), button -> {
            Path path = pathRef.get();
            if (path == null || !Files.exists(path)) {
                message = error("Invalid file path");
                return;
            }

            Set<Proxy> loadedProxies;
            try {
                loadedProxies = Util.loadProxies(path);
            } catch (IOException e) {
                message = error("Failed to read file");
                return;
            } catch (ProxyListParseException e) {
                message = error("Failed to parse proxy list: " + e.getMessage());
                return;
            }

            State.availableProxies.addAll(loadedProxies);
            message = success("Loaded %d proxies".formatted(loadedProxies.size()));
        })
                        .dimensions(width / 2, 70, 100, 20)
                        .build()
        );

        addDrawableChild(CheckboxWidget.builder(Text.literal("Proxy enabled"), textRenderer)
                .callback((checkbox, checked) -> {
                    State.useProxy = checked;
                })
                .pos(width / 2 - 100, 90)
                .build()
        );

        addDrawableChild(
                ButtonWidget.builder(Text.literal("Done"), button -> {
                    assert client != null;
                    client.setScreen(parent);
                }).dimensions(width / 2 - 100, 140, 200, 20).build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (message != null)
            context.drawText(textRenderer, message, width / 2 - textRenderer.getWidth(message) / 2, 5, 0xffff0000, true);
    }
}
