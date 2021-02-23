package net.draycia.sysoutcatcher;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintStream;

public final class SysoutCatcher extends JavaPlugin {

    public SysoutCatcher() {
        PrintStream wrappedOut = new PrintStream(System.out) {
            @Override
            public void println(String line) {
                StackTraceElement element = Thread.currentThread().getStackTrace()[2];
                try {
                    Class<?> clazz = Class.forName(element.getClassName());
                    JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);

                    StringBuilder messageBuilder = new StringBuilder();

                    if (getConfig().getBoolean("IncludeSourceClass", false)) {
                        messageBuilder.append('(').append(element.getClassName()).append(") ");
                    }
                    if (getConfig().getBoolean("IncludeSourceLine", false)) {
                        messageBuilder.append('(').append(new Exception().getStackTrace()[2].getLineNumber()).append(") ");
                    }

                    messageBuilder.append(line);

                    plugin.getLogger().info(messageBuilder.toString());
                } catch (ClassNotFoundException | IllegalArgumentException e) {
                    super.printf("(%s:%d) %s\n", element.getClassName(), element.getLineNumber(), line);
                }
            }
        };

        PrintStream wrappedErr = new PrintStream(System.err) {
            @Override
            public void println(String line) {
                StackTraceElement element = Thread.currentThread().getStackTrace()[2];
                try {
                    Class<?> clazz = Class.forName(element.getClassName());
                    JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);

                    StringBuilder messageBuilder = new StringBuilder();

                    if (getConfig().getBoolean("IncludeSourceClass", false)) {
                        messageBuilder.append('(').append(element.getClassName()).append(") ");
                    }
                    if (getConfig().getBoolean("IncludeSourceLine", false)) {
                        messageBuilder.append('(').append(new Exception().getStackTrace()[2].getLineNumber()).append(") ");
                    }

                    messageBuilder.append(line);

                    plugin.getLogger().info(messageBuilder.toString());
                } catch (ClassNotFoundException | IllegalArgumentException e) {
                    super.printf("(%s:%d) %s\n", element.getClassName(), element.getLineNumber(), line);
                }
            }
        };

        System.setOut(wrappedOut);
        System.setErr(wrappedErr);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
    }

}
