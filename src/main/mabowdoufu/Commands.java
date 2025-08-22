package main.mabowdoufu;

import main.mabowdoufu.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static main.mabowdoufu.Man10Checkers.mcheckers;


public class Commands implements @Nullable CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mcheckers.p")) return true;
        switch (args.length){
            case 1:
                if (args[0].equals("help")){
                    sender.sendMessage(Config.prefix + "§r/mcheckers board list : 開催中のリバーシのリストを表示");
                    sender.sendMessage(Config.prefix + "§r/mcheckers start [ボード名] : リバーシを開始します");
                    sender.sendMessage(Config.prefix + "§r/mcheckers join [ボード名] : リバーシに参加します");
                    if (sender.hasPermission("mcheckers.op")){
                        sender.sendMessage(Config.prefix + "§r=== 管理者コマンド ===");
                        sender.sendMessage(Config.prefix + "§r/mcheckers [on/off] : システムを稼働/停止します");
                        sender.sendMessage(Config.prefix + "§r/mcheckers board create [名前] : ボードを作成します");
                        sender.sendMessage(Config.prefix + "§r/mcheckers end [ボード名] : ゲームを強制終了します");
                    }
                }
                else if (args[0].equals("on") && sender.hasPermission("mcheckers.op")){
                    if (Config.system){
                        sender.sendMessage(Config.prefix + "§r既にONです");
                        return true;
                    }
                    Config.system = true;
                    mcheckers.getConfig().set("system", Config.system);
                    mcheckers.saveConfig();
                    sender.sendMessage(Config.prefix + "§rONにしました");
                    return true;
                }
                else if (args[0].equals("off") && sender.hasPermission("mcheckers.op")){
                    if (!Config.system){
                        sender.sendMessage(Config.prefix + "§r既にOFFです");
                        return true;
                    }
                    Config.system = false;
                    mcheckers.getConfig().set("system", Config.system);
                    mcheckers.saveConfig();
                    sender.sendMessage(Config.prefix + "§rOFFにしました");
                    return true;
                }
                break;

            case 2:
                if (args[0].equals("start")){
                    if (!Config.system){
                        sender.sendMessage(Config.prefix + "§rシステムはOFFです");
                        return true;
                    }
                    if (!sender.hasPermission("mcheckers.open")){
                        sender.sendMessage(Config.prefix + "§r権限がありません");
                        return true;
                    }
                    UUID sender_uuid = ((Player) sender).getUniqueId();
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = new YamlConfiguration();
                    boolean IsJoining = false;
                    boolean ExistBoard = false;
                    for (String boardname : yml.getKeys(false)) {
                        if (args[1] == boardname){
                            ExistBoard = true;
                        }
                        for (String joinning_uuid : yml.getStringList(boardname + ".players"))
                            if(sender_uuid.toString().equals(joinning_uuid)) {
                                IsJoining = true;
                            }
                    }
                    if (IsJoining){
                        sender.sendMessage(Config.prefix + "§r別のボードでゲーム中は参加できません");
                        return true;
                    }
                    if (yml.getBoolean(args[1] +(".DuringGame"))){
                        sender.sendMessage(Config.prefix + "§rそのボードは使用中です");
                        return true;
                    }
                    if (!ExistBoard){
                        sender.sendMessage(Config.prefix + "§rそのボードは存在しません");
                        return true;
                    }
                    return true;
                    //メイン処理

                }
                else if (args[0].equals("join")){
                    if (!Config.system){
                        sender.sendMessage(Config.prefix + "§rシステムはOFFです");
                        return true;
                    }
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = new YamlConfiguration();
                    UUID sender_uuid = ((Player) sender).getUniqueId();
                    boolean IsJoining = false;
                    boolean ExistBoard = false;
                    for (String boardname : yml.getKeys(false)) {
                        if (args[1] == boardname){
                            ExistBoard = true;
                        }
                        for (String joinning_uuid : yml.getStringList(boardname + ".players"))
                            if(sender_uuid.toString().equals(joinning_uuid)) {
                                IsJoining = true;
                            }
                    }
                    if (IsJoining){
                        sender.sendMessage(Config.prefix + "§r別のボードでゲーム中は参加できません");
                        return true;
                    }
                    if (!ExistBoard) {
                        sender.sendMessage(Config.prefix + "§rそのボードのゲームは存在しません");
                        return true;
                    }
                    if (!yml.getBoolean(args[1] +(".DuringGame"))){
                        sender.sendMessage(Config.prefix + "§rそのボードはプレイヤーを募集していません");
                        return true;
                    }
                    //メイン処理
                    yml.set(args[1] +(".players"),sender_uuid);
                    return true;
                }
                else if (args[0].equals("end") && sender.hasPermission("mcheckers.op")){
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = new YamlConfiguration();
                    if (yml.getBoolean(".DuringGame")||yml.getBoolean(".Recruiting")){
                        sender.sendMessage(Config.prefix + "§rそのボードはゲーム中ではありません");
                        return true;
                    }
                    // 8/22作業ここまで　end時のyml処理書く↓
                    games.get(args[1]).ForceEnd();
                    sender.sendMessage(Config.prefix + "§r終了しました");
                    return true;
                }
                break;

            case 3:
                if (args[0].equals("board") && args[1].equals("create") && sender.hasPermission("mcheckers.op")){
                    if (BoardManager.boards.containsKey(args[2])){
                        sender.sendMessage(Config.prefix + "§rその名前のボードはすでに存在します");
                        return true;
                    }
                    BoardManager.tmp_board.put(((Player) sender).getUniqueId(), new BoardManager.TMP_Board(args[2], new BoardManager.Board(args[2], ((Player) sender).getWorld().getName())));
                    sender.sendMessage(Config.prefix + "§r真上から見てボードの左上の端になるブロックを左クリックして下さい");
                    return true;
                }
                break;
        }
        sender.sendMessage(Config.prefix + "§r/mcheckers helpでコマンドを確認");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("mcheckers.p")) return List.of();
        if (args.length == 1){
            if (sender.hasPermission("mcheckers.op")) return Arrays.asList("help", "start", "join", "on", "off", "board", "end", "open", "abilities");
            else return Arrays.asList("start", "join", "board");
        }
        else if (args.length == 2){
            if (args[0].equals("start")){
                return BoardManager.boards.keySet().stream().toList();
            }
            else if (args[0].equals("join") || (args[0].equals("end") && sender.hasPermission("mcheckers.op"))){
                return games.keySet().stream().toList();
            }
            else if (args[0].equals("board")){
                if (sender.hasPermission("mcheckers.op")) return Arrays.asList("list", "create");
                else Collections.singletonList("list");
            }
        }
        else if (args.length == 3){
            if (args[0].equals("board") && args[1].equals("create") && sender.hasPermission("mcheckers.op")){
                Collections.singletonList("[名前]");
            }
        }
        return List.of();
    }
}