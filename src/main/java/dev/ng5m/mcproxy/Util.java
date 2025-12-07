package dev.ng5m.mcproxy;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {

    public static @Nullable File chooseFile(String description, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                description, extensions
        );
        chooser.setFileFilter(filter);
        int ret = chooser.showOpenDialog(null);


        if (ret == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public static Set<Proxy> loadProxies(Path path) throws IOException, ProxyListParseException {
        List<String> lines = Files.readAllLines(path);
        Set<Proxy> proxies = new HashSet<>();

        int lineN = 0;
        for (String line : lines) {
            String[] protocolSplit = line.split("://");
            Proxy.Type protocol = Proxy.Type.HTTP;
            String ip = protocolSplit[protocolSplit.length - 1];

            if (protocolSplit.length > 1) {
                protocol = Proxy.Type.valueOf(protocolSplit[0].toUpperCase());
            }


            String[] ipSplit = ip.split(":");
            if (ipSplit.length == 1) {
                throw new ProxyListParseException("No port supplied at line " + lineN);
            }

            int port = Integer.parseInt(ipSplit[1]);

            proxies.add(new Proxy(protocol, ipSplit[0], port));

            lineN++;
        }

        return proxies;
    }

    public static Text error(String s) {
        return Text.literal(s).withColor(0xffff0000);
    }

    public static Text success(String s) {
        return Text.literal(s).withColor(0xff00ff00);
    }
}
