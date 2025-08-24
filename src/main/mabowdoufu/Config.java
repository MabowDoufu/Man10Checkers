package main.mabowdoufu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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

    public static void SaveBoards(BoardManager.Board b){
        File folder = new File(configfile.getAbsolutePath() + File.separator + b.name + ".yml");
        YamlConfiguration yml = new YamlConfiguration();        //config作成
        yml.set("name", b.name);
        yml.set("x1", b.x1);
        yml.set("x2", b.x2);
        yml.set("z1", b.z1);
        yml.set("z2", b.z2);
        yml.set("y", b.y);
        yml.set("world", b.world);
        try {
            yml.save(folder);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.broadcast(Config.prefix + "§r" + b.name + "の保存に失敗しました","mreversi.op");
        }
    }
}
