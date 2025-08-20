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
import java.util.logging.Logger;

import static main.mabowdoufu.Man10Checkers.bgs;

public class Commands implements @Nullable CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage("§a§l/mcheckers help §r§a: §r§aヘルプを表示します。");
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage("§a§lAdvancedBartender §r§aのコマンド一覧");
            player.sendMessage("§a§l/mcheckers help §r§a: §r§aこのヘルプを表示します。");
            player.sendMessage("§a§l/mcheckers list §r§a: §r§aバーテンダーの一覧を表示します。");
            player.sendMessage("§a§l/mcheckers info <バーテンダー名> §r§a: §r§aバーテンダーの情報を表示します。");
            player.sendMessage("§a§l/mcheckers hire <バーテンダー名> §r§a: §r§aバーテンダーを雇います。");
            player.sendMessage("§a§l/mcheckers fire <バーテンダー名> §r§a: §r§aバーテンダーを解雇します。");
            player.sendMessage("§a§l/mcheckers set <バーテンダー名> <アクション> §r§a: §r§aバーテンダーのアクションを設定します。");
            player.sendMessage("§a§l/mcheckers reset <バーテンダー名> §r§a: §r§aバーテンダーのアクションをリセットします。");
            player.sendMessage("§a§l/mcheckers reload §r§a: §r§aAdvancedBartenderをリロードします。");
            return true;
        }
        if (args[0].equalsIgnoreCase("creategame")) {
            bgs.CreateGame("test");
            Logger.getLogger("テスト開始");
            return true;
        }
        if (args[0].equalsIgnoreCase("men")) {
            bgs.CreateGame("test");
            bgs.BoardInput(args[1],Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]),Integer.parseInt(args[5]));
            Logger.getLogger("テスト開始");
            return true;
        }
        if (args[0].equalsIgnoreCase("creategame")) {
            bgs.CreateGame("test");
            Logger.getLogger("テスト開始");
            return true;
        }
        if (args[0].equalsIgnoreCase("creategame")) {
            bgs.CreateGame("test");
            Logger.getLogger("テスト開始");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
