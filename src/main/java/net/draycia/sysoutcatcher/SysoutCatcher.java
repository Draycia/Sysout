package net.draycia.sysoutcatcher;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintStream;

public final class SysoutCatcher extends JavaPlugin implements Listener {

    public SysoutCatcher() {
        // Listen to System.out.println
        PrintStream wrappedOut = new PrintStream(System.out) {
            @Override
            public void println(String line) {
                // Get the current stack trace and the calling method (index 2)
                StackTraceElement element = Thread.currentThread().getStackTrace()[2];
                try {
                    // Get the class name at that index and the JavaPlugin that "owns" it
                    Class<?> clazz = Class.forName(element.getClassName());
                    JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);

                    StringBuilder messageBuilder = new StringBuilder();

                    // Optional information that may be added to the logged line
                    if (getConfig().getBoolean("IncludeSourceClass", false)) {
                        messageBuilder.append('(').append(element.getClassName()).append(") ");
                    }
                    if (getConfig().getBoolean("IncludeSourceLine", false)) {
                        messageBuilder.append('(').append(new Exception().getStackTrace()[2].getLineNumber()).append(") ");
                    }

                    messageBuilder.append(line);

                    // Instead of just printing the message, send it to the plugin's logger
                    plugin.getLogger().info(messageBuilder.toString());
                } catch (ClassNotFoundException | IllegalArgumentException e) {
                    // If anything happens, the calling class doesn't exist, there is no JavaPlugin that "owns" the calling class, etc
                    // Just print out normally, with some added information
                    getLogger().info(String.format("(%s:%d) %s\n", element.getClassName(), element.getLineNumber(), line));
                }
            }
        };

        // Listen to System.err.println
        PrintStream wrappedErr = new PrintStream(System.err) {
            @Override
            public void println(String line) {
                // Get the current stack trace and the calling method (index 2)
                StackTraceElement element = Thread.currentThread().getStackTrace()[2];
                try {
                    // Get the class name at that index and the JavaPlugin that "owns" it
                    Class<?> clazz = Class.forName(element.getClassName());
                    JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);

                    StringBuilder messageBuilder = new StringBuilder();

                    // Optional information that may be added to the logged line
                    if (getConfig().getBoolean("IncludeSourceClass", false)) {
                        messageBuilder.append('(').append(element.getClassName()).append(") ");
                    }
                    if (getConfig().getBoolean("IncludeSourceLine", false)) {
                        messageBuilder.append('(').append(new Exception().getStackTrace()[2].getLineNumber()).append(") ");
                    }

                    messageBuilder.append(line);

                    // Instead of just printing the message, send it to the plugin's logger
                    plugin.getLogger().severe(messageBuilder.toString());
                } catch (ClassNotFoundException | IllegalArgumentException e) {
                    // If anything happens, the calling class doesn't exist, there is no JavaPlugin that "owns" the calling class, etc
                    // Just print out normally, with some added information
                    getLogger().severe(String.format("(%s:%d) %s\n", element.getClassName(), element.getLineNumber(), line));
                }
            }
        };

        // Set the two PrintStreams so they're used
        System.setOut(wrappedOut);
        System.setErr(wrappedErr);

        Bukkit.getLogger().setUseParentHandlers(false);

        Bukkit.getLogger().addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record == null) return;

                try {
                    // Get the class name at that index and the JavaPlugin that "owns" it
                    Class<?> clazz = Class.forName(record.getSourceClassName());
                    JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);

                    StringBuilder messageBuilder = new StringBuilder();

                    // Optional information that may be added to the logged line
                    if (getConfig().getBoolean("IncludeSourceClass", false)) {
                        messageBuilder.append('(').append(record.getSourceClassName()).append(") ");
                    }
                    if (getConfig().getBoolean("IncludeSourceLine", false)) {
                        messageBuilder.append('(').append(new Exception().getStackTrace()[2].getLineNumber()).append(") ");
                    }

                    messageBuilder.append(record.getMessage());

                    // Instead of just printing the message, send it to the plugin's logger
                    plugin.getLogger().log(record.getLevel(), messageBuilder.toString());
                } catch (ClassNotFoundException | IllegalArgumentException e) {
                    // If anything happens, the calling class doesn't exist, there is no JavaPlugin that "owns" the calling class, etc
                    getLogger().log(record);
                }
            }

            @Override
            public void flush() { }

            @Override
            public void close() throws SecurityException { }
        });
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
    }

}
