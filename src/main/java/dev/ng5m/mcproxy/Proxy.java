package dev.ng5m.mcproxy;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;

import java.net.SocketAddress;

public record Proxy(Type type, String ip, int port) {

    public enum Type {
        SOCKS4(Socks4ProxyHandler::new),
        SOCKS5(Socks5ProxyHandler::new),
        HTTP(HttpProxyHandler::new);

        public final HandlerFactory handlerFactory;

        Type(HandlerFactory handlerFactory) {
            this.handlerFactory = handlerFactory;
        }
    }

    public interface HandlerFactory {
        ProxyHandler get(SocketAddress address);
    }
}
