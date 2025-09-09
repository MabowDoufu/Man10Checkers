package main.mabowdoufu;

import org.bukkit.plugin.java.JavaPlugin;

public final class Man10Checkers extends JavaPlugin {

    /// board createを誰でもできるようにする
    ///yml:ボード名は主催者のユーザー名を自動設定
    /// Recruiting状態に応じたjoinコマンド補完
    /// join処理
    ///ボード開始時の両者gyi表示
    ///gui中断時の再オープンコマンド
    /// ---ここまでやった
    ///gui上でのインプットアウトプット
    ///
    public static JavaPlugin mcheckers;
    public static main.mabowdoufu.BoardGameSys bgs;
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
