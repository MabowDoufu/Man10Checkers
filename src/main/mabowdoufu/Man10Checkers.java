package main.mabowdoufu;

import org.bukkit.plugin.java.JavaPlugin;

public final class Man10Checkers extends JavaPlugin {

    public static JavaPlugin mcheckers;
    @Override
    public void onEnable() {
        mcheckers = this;
        Config.LoadConfig();
        getCommand("mreversi").setExecutor(new Commands());
        getLogger().info("This plugin is running");
    }

    @Override
    public void onDisable() {

        getLogger().info("This plugin has stopped running");

    }
}
