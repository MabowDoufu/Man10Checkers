package me.mabowdoufu;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GameManager {
    // 盤面情報
    // 0:未設置, 1:黒, 2:白
    public int[][] grid = new int[8][8];

    public static void ControlBoard(int x1, int y1, int x2, int y2 ){

        //チェッカーで使用しないマスを選択
        if((((x1%2)+(y1%2)%2)== 1)||(((x2%2)+(y2%2)%2)== 1) ){
            return;
        }

        //yml読み込み
        File folder = new File(configfile.getAbsolutePath() + File.separator + b.name + ".yml");
        YamlConfiguration yml = new YamlConfiguration();        //config作成


    }
    
}
