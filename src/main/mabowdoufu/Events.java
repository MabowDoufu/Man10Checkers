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


public class Events implements Listener {
    public Events(Plugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void BlockDamage(BlockDamageEvent e){
        if (!e.getPlayer().hasPermission("mreversi.op")) return;
        if (e.getPlayer().hasPermission("mreversi.op") && BoardManager.tmp_board.containsKey(e.getPlayer().getUniqueId())){
            String result = BoardManager.tmp_board.get(e.getPlayer().getUniqueId()).SetLocation(e.getBlock(), e.getPlayer());
            e.getPlayer().sendMessage(result);
            e.setCancelled(true);

        String boardname = BoardManager.tmp_board.get(e.getPlayer().getUniqueId()).name;
        BoardGameSys.LoadBoard(boardname);
        ///koko Helperとは何か?
        }else if (BoardGameSys.DuringGame){
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

    @EventHandler
    public void InventoryClick(InventoryClickEvent e){
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