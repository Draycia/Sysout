package net.draycia.sysoutcatcher;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintStream;

public final class SysoutCatcher extends JavaPlugin {

    public SysoutCatcher() {
        PrintStream myStream = new PrintStream(System.out) {
            @Override
            public void println(String line) {

                try {
                    StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
                    Class<?> caller = stackWalker.getCallerClass();
                    JavaPlugin.getProvidingPlugin(caller).getLogger().info(line);
                } catch (IllegalArgumentException e) {
                    StackTraceElement element = new Exception().getStackTrace()[2];
                    super.printf("(%s:%d) %s\n", element.getClassName(), element.getLineNumber(), line);
                }

            }
        };

        System.setOut(myStream);
    }

}
