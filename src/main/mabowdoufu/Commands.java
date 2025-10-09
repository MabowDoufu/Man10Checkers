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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.io.File;
import java.util.*;

import static main.mabowdoufu.Man10Checkers.mcheckers;

public class Commands implements @Nullable CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mcheckers.op")) return true;
        switch (args.length){
            case 1:
                if (args[0].equals("help")){
                    sender.sendMessage(Config.prefix + "§r/mcheckers Board list : 開催中のリバーシのリストを表示");
                    sender.sendMessage(Config.prefix + "§r/mcheckers start : リバーシを開始します");
                    sender.sendMessage(Config.prefix + "§r/mcheckers join [ボード名] : リバーシに参加します");
                    sender.sendMessage(Config.prefix + "§r/mcheckers open : リバーシの画面を再表示します（ゲーム参加時のみ有効）");
                    if (sender.hasPermission("mcheckers.op")){
                        sender.sendMessage(Config.prefix + "§r=== 管理者コマンド ===");
                        sender.sendMessage(Config.prefix + "§r/mcheckers [on/off] : システムを稼働/停止します");
                        sender.sendMessage(Config.prefix + "§r/mcheckers Board create [名前] : ボードを作成します");
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
                else if (args[0].equals("start")){
                    //finish
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
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    boolean ExistBoard = false;
                    boolean IsJoining = false;
                    for (String BoardName : yml.getKeys(false)) {
                        if (args[1].equals(BoardName)){
                            ExistBoard = true;
                        }
                        for (String joinning_uuid : yml.getStringList(BoardName + ".Players")) {
                            if (sender_uuid.toString().equals(joinning_uuid)) {
                                IsJoining = true;
                            }
                        }
                    }
                    if (IsJoining){
                        sender.sendMessage(Config.prefix + "§r別のボードでゲーム中は参加できません");
                        return true;
                    }
                    if (ExistBoard){
                        sender.sendMessage(Config.prefix + "§rあなたはすでにボードを開いています");
                        return true;
                    }
                    BoardGameSys.ResetYml((sender).getName());
                    BoardGameSys.Recruiting = true;
                    BoardGameSys.Players.add((Player) sender);
                    BoardGameSys.saveData((sender).getName());
                    return true;
                }
                else if (args[0].equals("open")){
                    ///再度gui開く処理
                    UUID sender_uuid = ((Player) sender).getUniqueId();
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    for (String BoardName : yml.getKeys(false)) {
                        for (String joinning_uuid : yml.getStringList(BoardName + ".Players")) {
                            if (sender_uuid.toString().equals(joinning_uuid)) {
                                BoardGameSys.LoadData(BoardName);
                                ((Player) sender).openInventory(BoardGameSys.getInv());
                                return true;
                            }
                        }
                    }
                    return true;
                }
                break;
            case 2:
                if (args[0].equals("join")){
                    if (!Config.system){
                        sender.sendMessage(Config.prefix + "§rシステムはOFFです");
                        return true;
                    }
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    UUID sender_uuid = ((Player) sender).getUniqueId();
                    boolean IsJoining = false;
                    boolean ExistBoard = false;
                    for (String BoardName : yml.getKeys(false)) {
                        if (args[1] == BoardName){
                            ExistBoard = true;
                        }
                        for (String joinning_uuid : yml.getStringList(BoardName + ".Players"))
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
                    BoardGameSys.LoadData(args[1]);
                    if (!BoardGameSys.DuringGame){
                        sender.sendMessage(Config.prefix + "§rそのボードはプレイヤーを募集していません");
                        return true;
                    }
                    ///ゲーム参加処理
                    BoardGameSys.Players.add((Player) sender);
                    BoardGameSys.Recruiting = false;
                    BoardGameSys.DuringGame = true;
                    BoardGameSys.saveData(args[1]);
                    //gui開く処理
                    Inventory inv = BoardGameSys.getInv();
                    (BoardGameSys.Players.get(1)).openInventory(inv);
                    (BoardGameSys.Players.get(2)).openInventory(inv);
                    return true;
                }
                else if (args[0].equals("end") && sender.hasPermission("mcheckers.op")){
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    if (yml.getBoolean(".DuringGame")||yml.getBoolean(".Recruiting")){
                        sender.sendMessage(Config.prefix + "§rそのボードはゲーム中ではありません");
                        return true;
                    }
                    try{
                        yml.set(args[1]+".Player",null);
                        yml.set(args[1]+".DuringGame",null);
                        yml.set(args[1]+".Recruiting",null);
                        yml.set(args[1]+".Board",null);
                        yml.set(args[1]+".IsKing",null);
                        ///ゲーム強制終了処理を書く(events終わった後に書く)
                        sender.sendMessage(Config.prefix + "§r終了しました");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(Config.prefix + "§rエラーが発生しました");
                    }
                    return true;
                }
                break;
            case 3:
                if (args[0].equals("Board") && args[1].equals("create") && sender.hasPermission("mcheckers.op")){
                    File gameyml = new File("plugins/Man10Checkers/game.yml");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
                    boolean ExistBoard = false;
                    for (String BoardName : yml.getKeys(false)) {
                        if (args[1] == BoardName){
                            ExistBoard = true;
                        }
                    }
                    if (ExistBoard){
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("mcheckers.op")) return List.of();
        if (args.length == 1){
            if (sender.hasPermission("mcheckers.op")) return Arrays.asList("help", "start", "join", "on", "off", "Board", "end", "open", "abilities");
            else return Arrays.asList("start", "join", "Board");
        }
        else if (args.length == 2){
            File gameyml = new File("plugins/Man10Checkers/game.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
            List<String> Boardlist = null;
            try{
                Boardlist.addAll(yml.getKeys(false));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Config.prefix + "§rエラーが発生しました");
            }
            if (args[0].equals("join") || (args[0].equals("end") && sender.hasPermission("mcheckers.op"))){
                //Recruiting
                List<String> Joinable_Boards = new ArrayList<String>();
                for (String BoardName : yml.getKeys(false)) {
                    for (Boolean recruiting : yml.getBooleanList(BoardName + ".Recruiting"))
                        if(recruiting) {
                            Joinable_Boards.add(BoardName);
                        }
                }
                return Joinable_Boards;
            }
            else if (args[0].equals("Board")){
                if (sender.hasPermission("mcheckers.op")) return Arrays.asList("list", "create");
                else Collections.singletonList("list");
            }
        }
        else if (args.length == 3){
            if (args[0].equals("Board") && args[1].equals("create") && sender.hasPermission("mcheckers.op")){
                Collections.singletonList("[名前]");
            }
        }
        return List.of();
    }
}