package net.draycia.sysoutcatcher;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintStream;

public final class SysoutCatcher extends JavaPlugin {

    private static final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public SysoutCatcher() {
        PrintStream myStream = new PrintStream(System.out) {
            @Override
            public void println(String line) {

                try {
                    Class<?> caller = stackWalker.getCallerClass();

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

        System.setOut(myStream);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
    }

}
