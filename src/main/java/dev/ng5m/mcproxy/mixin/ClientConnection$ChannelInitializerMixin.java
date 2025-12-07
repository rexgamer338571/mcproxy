package dev.ng5m.mcproxy.mixin;

import dev.ng5m.mcproxy.Proxy;
import dev.ng5m.mcproxy.State;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public abstract class ClientConnection$ChannelInitializerMixin extends ChannelInitializer<Channel> {

    @Inject(method = "initChannel", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelPipeline;addLast(Ljava/lang/String;Lio/netty/channel/ChannelHandler;)Lio/netty/channel/ChannelPipeline;", ordinal = 0),
    remap = false)
    private void initChannel$addProxyHandler(Channel channel, CallbackInfo ci) {
        if (!State.useProxy) return;

        Proxy proxy = State.availableProxies
                .stream()
                .skip(ThreadLocalRandom.current().nextInt(State.availableProxies.size()))
                .findFirst()
                .orElse(null);

        if (proxy == null) return;

        channel.pipeline().addLast(
                proxy.type().handlerFactory.get(
                        new InetSocketAddress(proxy.ip(), proxy.port())
                )
        );
    }

}
