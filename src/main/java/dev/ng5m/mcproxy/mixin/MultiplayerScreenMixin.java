package dev.ng5m.mcproxy.mixin;

import dev.ng5m.mcproxy.client.SelectProxyScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init$addSelectProxyButton(CallbackInfo ci) {
        addDrawableChild(
                ButtonWidget.builder(Text.literal("MCProxy"), button -> {
                    assert client != null;
                    client.setScreen(new SelectProxyScreen((Screen) (Object) this));
                }).dimensions(5, 50, 50, 20)
                        .build()
        );
    }

}
