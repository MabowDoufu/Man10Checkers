package main.mabowdoufu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class Commands implements @Nullable CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        File playerdata = new File("plugins/Man10Checkers/playerdata/" + player.getUniqueId() + ".yml");
        YamlConfiguration playerdataYml = YamlConfiguration.loadConfiguration(playerdata);
        File[] bartenderlist = new File("plugins/Man10Checkers/bartender/").listFiles();
        if (args.length == 0) {
            player.sendMessage("§a§l/che help §r§a: §r§aヘルプを表示します。");
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage("§a§lAdvancedBartender §r§aのコマンド一覧");
            player.sendMessage("§a§l/abt help §r§a: §r§aこのヘルプを表示します。");
            player.sendMessage("§a§l/abt list §r§a: §r§aバーテンダーの一覧を表示します。");
            player.sendMessage("§a§l/abt info <バーテンダー名> §r§a: §r§aバーテンダーの情報を表示します。");
            player.sendMessage("§a§l/abt hire <バーテンダー名> §r§a: §r§aバーテンダーを雇います。");
            player.sendMessage("§a§l/abt fire <バーテンダー名> §r§a: §r§aバーテンダーを解雇します。");
            player.sendMessage("§a§l/abt set <バーテンダー名> <アクション> §r§a: §r§aバーテンダーのアクションを設定します。");
            player.sendMessage("§a§l/abt reset <バーテンダー名> §r§a: §r§aバーテンダーのアクションをリセットします。");
            player.sendMessage("§a§l/abt reload §r§a: §r§aAdvancedBartenderをリロードします。");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage("§a§lAdvancedBartender §r§aのバーテンダー一覧");
            for (File bartender : bartenderlist) {
                player.sendMessage("§a§l" + bartender.getName().replace(".yml", ""));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
