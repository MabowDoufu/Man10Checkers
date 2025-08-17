package me.mabowdoufu;

import org.bukkit.plugin.java.JavaPlugin;

public class Man10Checkers extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("This plugin is running");
    }

    @Override
    public void onDisable() {

        getLogger().info("This plugin has stopped running");

    }
}
