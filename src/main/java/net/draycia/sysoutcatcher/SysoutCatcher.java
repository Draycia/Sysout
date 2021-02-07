package net.draycia.sysoutcatcher;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintStream;
import java.lang.invoke.MethodHandles;

public final class SysoutCatcher extends JavaPlugin {

    public SysoutCatcher() {
        PrintStream wrappedOut = new PrintStream(System.out) {
            @Override
            public void println(String line) {

                try {
                    Class<?> caller = MethodHandles.lookup().lookupClass();

                    StringBuilder messageBuilder = new StringBuilder();

                    if (getConfig().getBoolean("IncludeSourceClass", false)) {
                        messageBuilder.append('(').append(caller.getName()).append(") ");
                    }
                    if (getConfig().getBoolean("IncludeSourceLine", false)) {
                        messageBuilder.append('(').append(new Exception().getStackTrace()[2].getLineNumber()).append(") ");
                    }

                    messageBuilder.append(line);

                    JavaPlugin.getProvidingPlugin(caller).getLogger().info(messageBuilder.toString());
                } catch (IllegalArgumentException e) {
                    StackTraceElement element = new Exception().getStackTrace()[2];
                    super.printf("(%s:%d) %s\n", element.getClassName(), element.getLineNumber(), line);
                }

            }
        };

        PrintStream wrappedErr = new PrintStream(System.err) {
            @Override
            public void println(String line) {

                try {
                    Class<?> caller = MethodHandles.lookup().lookupClass();

                    StringBuilder messageBuilder = new StringBuilder();

                    if (getConfig().getBoolean("IncludeSourceClass", false)) {
                        messageBuilder.append('(').append(caller.getName()).append(") ");
                    }
                    if (getConfig().getBoolean("IncludeSourceLine", false)) {
                        messageBuilder.append('(').append(new Exception().getStackTrace()[2].getLineNumber()).append(") ");
                    }

                    messageBuilder.append(line);

                    JavaPlugin.getProvidingPlugin(caller).getLogger().severe(messageBuilder.toString());
                } catch (IllegalArgumentException e) {
                    StackTraceElement element = Thread.currentThread().getStackTrace()[2];
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
