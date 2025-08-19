package main.mabowdoufu;

import org.bukkit.Material;

import java.io.File;

import static main.mabowdoufu.Man10Checkers.mcheckers;

public class Config {
    public static String prefix = "";
    public static File configfile;
    public static boolean system;
    public static int recruitment_time;
    public static int recruitment_interval;
    public static int max_thinking;
    public static int max_ability;

    public static void LoadConfig() {
        mcheckers.saveDefaultConfig();
        system = mcheckers.getConfig().getBoolean("system");
        prefix = mcheckers.getConfig().getString("prefix");
        recruitment_time = mcheckers.getConfig().getInt("recruitment.time");
        recruitment_interval = mcheckers.getConfig().getInt("recruitment.messageInterval");
        max_thinking = mcheckers.getConfig().getInt("game.maxThinking");
    }
}
