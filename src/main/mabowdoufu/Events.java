package main.mabowdoufu;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Currency;
import java.util.UUID;

import static main.mabowdoufu.BoardGameSys.*;

/// マイクラ内reversi動作確認メモ
/// ボードの土台ブロックの設置はゲーム開始直後に行われる
/// ボードcreate時の二番目に指定した座標は、ゲーム開始時の土台ブロックの設置範囲と、ゲームのボードの向きの決定の身に使われる
///設置できるマスには紫のパーティクルが表示される
/// 二人がゲームに参加した時点でゲームが開始される
/// 土台ブロックにダメージを与えることによって設置する

public class Events implements Listener {
    public Events(Plugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    /*保留
    @EventHandler
    public void BlockDamage(BlockDamageEvent e){
        if (!e.getPlayer().hasPermission("mreversi.op")) return;
        if (e.getPlayer().hasPermission("mreversi.op") && BoardManager.tmp_board.containsKey(e.getPlayer().getUniqueId())){
            String result = BoardManager.tmp_board.get(e.getPlayer().getUniqueId()).SetLocation(e.getBlock(), e.getPlayer());
            e.getPlayer().sendMessage(result);
            e.setCancelled(true);

        String boardname = BoardManager.tmp_board.get(e.getPlayer().getUniqueId()).name;
        BoardGameSys.LoadData(boardname);
        }else if (BoardGameSys.DuringGame){
            ///Game中にopがある人が何かをする処理？
            GameManager g = Helper.GetGameForUUID(games.values(), e.getPlayer().getUniqueId());
            if (g != null && (g.state == GameManager.GameState.THINKING || g.state == GameManager.GameState.ABILITY) && Helper.BlockInBoard(e.getBlock(), g.board)){
                g.Place(e.getBlock(), e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e){
        if (!BoardGameSys.DuringGame) return;
        for (GameManager g: games.values()){
            if (g.state != GameManager.GameState.RECRUITMENT && Helper.BlockInBoard(e.getBlock(), g.board)){
                e.getPlayer().sendMessage(Component.text(Config.prefix + "§rゲーム中のボード内のブロックは破壊できません"));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void BlockPlace(BlockPlaceEvent e){
        if (!BoardGameSys.DuringGame) return;
        for (GameManager g: games.values()){
            if (g.state != GameManager.GameState.RECRUITMENT && Helper.BlockInBoard(e.getBlock(), g.board)){
                e.getPlayer().sendMessage(Component.text(Config.prefix + "§rゲーム中のボード内にはブロックを設置できません"));
                e.setCancelled(true);
            }
        }
    }
    */
    public static int x1;
    public static int y1;
    public static int x2;
    public static int y2;
    @EventHandler
    public void InventoryClick(InventoryClickEvent e){
        Player Clicker = (Player) e.getWhoClicked();
        BoardGameSys.LoadData(getBoard(e.getWhoClicked().getUniqueId()));
        if(e.getView().getTitle().equals(Config.prefix)){
            int PlayerTurn;
            if(BoardGameSys.Players.get(1) == e.getWhoClicked()){
                PlayerTurn = 1;
            }else{
                PlayerTurn = 2;
            }
            if(PlayerTurn != Turn){
                return;
            }
            //クリックしたマス獲得
            //gamesysにマスの情報ぶちこむ
            ///再度gui開く処理

            File gameyml = new File("plugins/Man10Checkers/game.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
            String CurremtBoard ="";
            for (String BoardName : yml.getKeys(false)) {
                for (String joinning_uuid : yml.getStringList(BoardName + ".Players")) {
                    UUID sender_uuid = (Clicker).getUniqueId();
                    if (sender_uuid.toString().equals(joinning_uuid)) {
                        CurremtBoard = BoardName;
                    }
                }
            }
            LoadData(CurremtBoard);

            if(Click == 0){
                Click++;
                x1 = e.getRawSlot() % 9;
                y1 = (e.getRawSlot() - x1)/9;
                saveData(CurremtBoard);
                return;
            }else if(Click == 1){
                Click =0;
                x2 = e.getRawSlot() % 9;
                y2 = (e.getRawSlot() - x2)/9;
                BoardInput(CurremtBoard,x1,y1,x2,y2);
                saveData(CurremtBoard);
                switch (ErrorType){
                    case 1:
                        Clicker.sendMessage(Config.prefix + "§rシステムはOFFです");
                        break;
                    case 2:
                        Clicker.sendMessage(Config.prefix + "§rシステムはOFFです");
                        break;
                    case 3:
                        Clicker.sendMessage(Config.prefix + "§rシステムはOFFです");
                        break;
                }
                //invのタイトル変更方法(pass
                //ゲーム終了時の判定
                Clicker.openInventory(BoardGameSys.getInv());
                //インベントリへのアウトプット(マスに置くアイテムなど　完了

                return;
            }


        }


        if (e.getView().title().equals(Component.text("[Man10Reversi] 特殊効果選択"))){
            e.setCancelled(true);
            GameManager g = Helper.GetGameForUUID(games.values(), e.getWhoClicked().getUniqueId());
            if (g == null){
                e.getWhoClicked().sendMessage(Config.prefix + "§r参加中のゲームが見つかりません");
                e.getWhoClicked().closeInventory();
                return;
            }
            if (g.state == GameManager.GameState.THINKING){
                if (e.getCurrentItem() == null) return;
                e.getWhoClicked().closeInventory();
                String item_name = "";
                if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
                    item_name = LegacyComponentSerializer.legacySection().serialize(e.getCurrentItem().getItemMeta().displayName());
                }
                Data.Ability ability = Data.Ability.fromLabel(item_name);
                if (ability == null){
                    e.getWhoClicked().sendMessage(Config.prefix + "§r特殊効果が見つかりませんでした");
                    return;
                }
                g.SelectAbility((Player) e.getWhoClicked(), ability);
            }
            else {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(Config.prefix + "§r現在は選択できません");
            }
        }
        else if (e.getView().title().equals(Component.text("[Man10Reversi] 特殊効果一覧"))){
            e.setCancelled(true);
        }
    }
}