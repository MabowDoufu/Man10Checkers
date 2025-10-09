package main.mabowdoufu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.spi.AbstractResourceBundleProvider;
import static java.lang.Math.*;
import static org.bukkit.Material.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class BoardGameSys {
    // 盤面情報
    // 0:未設置, 1:黒 2:白
    public static File f;
    public static YamlConfiguration yml;
    public static int[][] Board = new int[8][8];
    public static boolean[][] IsKing = new boolean[8][8];
    public static int PlayerPiece;
    public static boolean DuringGame;
    public static boolean Recruiting;
    public static int Turn;
    public static int Click;
    public static List<Player> Players;
    public static int ErrorType;

    //のちのちsysから切り離し
    public static void ResetYml(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        Board[1][0] = 1;
        Board[3][0] = 1;
        Board[5][0] = 1;
        Board[7][0] = 1;
        Board[1][1] = 1;
        Board[3][1] = 1;
        Board[5][1] = 1;
        Board[7][1] = 1;
        Board[1][2] = 1;
        Board[3][2] = 1;
        Board[5][2] = 1;
        Board[7][2] = 1;
        Board[1][5] = 2;
        Board[3][5] = 2;
        Board[5][5] = 2;
        Board[7][5] = 2;
        Board[1][6] = 2;
        Board[3][6] = 2;
        Board[5][6] = 2;
        Board[7][6] = 2;
        Board[1][7] = 2;
        Board[3][7] = 2;
        Board[5][7] = 2;
        Board[7][7] = 2;
        yml.set(Boardname+ ".Board", Board);
        yml.set(Boardname+ ".IsKing", IsKing);
        yml.set(Boardname+ ".DuringGame", false);
        yml.set(Boardname+ ".Recruiting", false);
        yml.set(Boardname+ ".Turn", 1);
        yml.set(Boardname+ ".Click", 1);
        PlayerPiece =1;
        try {
            yml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void LoadData(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        Board = (int[][]) yml.get(Boardname+".Board");
        IsKing = (boolean[][]) yml.get(Boardname+".IsKing");
        DuringGame = yml.getBoolean(Boardname+".DuringGame");
        Recruiting = yml.getBoolean(Boardname+".Recruiting");
        Turn = yml.getInt(Boardname+".Turn");
        Click = yml.getInt(Boardname+".Click");
        Players = (List<Player>) yml.get(Boardname+".Players");

    }

    public static void saveData(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        yml.set(Boardname+".Board", Board);
        yml.set(Boardname+".IsKing", IsKing);
        yml.set(Boardname+".DuringGame", DuringGame);
        yml.set(Boardname+".Recruiting", Recruiting);
        yml.set(Boardname+".Turn", Turn);
        yml.set(Boardname+".Click", Click);
        yml.set(Boardname+".Players", Players);
        try {
            yml.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteData(String Boardname) {
        f = new File("plugins/Man10Checkers/game.yml");
        yml = YamlConfiguration.loadConfiguration(f);
        yml.set(Boardname, null);
        try {
            yml.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static ItemStack createGUIItem(final Material material, final String name, final String... lore){
        final ItemStack item=new ItemStack(material,1);
        final ItemMeta meta=item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    ///テキサスホールデムプラグインより
    public static String getBoard(UUID uuid){
        for (String BoardName : yml.getKeys(false)) {
            for (String joinning_uuid : yml.getStringList(BoardName + ".Players"))
                if(uuid.toString().equals(joinning_uuid)) {
                    return BoardName;
                }
        }
        return null;
    }
    public static Inventory getInv(String title){
        Inventory inv;

        inv= Bukkit.createInventory(null,54,Config.prefix + title);
        int i =0;
        int j =0;
        for(int[] BoardRow : Board){
            for (int Men : BoardRow){
                int slot = (i%6 -1) + (int) floor((double) i /6) +1;
                if(Men == 1 && !IsKing[j][i]) inv.setItem(slot,createGUIItem(BLACK_CONCRETE,"黒の駒",""));
                if(Men == 2 && !IsKing[j][i]) inv.setItem(slot,createGUIItem(WHITE_CONCRETE,"白の駒",""));
                if(Men == 1 && IsKing[j][i]) inv.setItem(slot,createGUIItem(BLACK_GLAZED_TERRACOTTA,"黒のキング",""));
                if(Men == 2 && IsKing[j][i]) inv.setItem(slot,createGUIItem(WHITE_GLAZED_TERRACOTTA,"白のキング",""));
                i++;
            }
            j++;
        }
        return inv;
    }

    public static boolean IsMyMen(String Boardname,int x1, int y1) {
        LoadData(Boardname);
        int selectpiece = Board[x1][y1];
        //相手の駒選択しているかどうか
        if (PlayerPiece == 1 && ((selectpiece == 3) || (selectpiece == 4))) {
            return false;
        }
        if (PlayerPiece == 2 && ((selectpiece == 1) || (selectpiece == 2))) {
            return false;
        }
        return true;
    }

    //directionは1または-1のみをとる
    private static boolean IsJumpable(int x1, int y1, int xdirection, int ydirection) {
        int enemypiece;
        if (PlayerPiece == 1) {
            enemypiece = 2;
        } else {
            enemypiece = 1;
        }
        try {
            if (Board[x1 + xdirection][y1 + ydirection] == enemypiece && Board[x1 + 2 * xdirection][y1 + 2 * ydirection] == 0) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void ContinuousMove(int x1, int y1) {
        List<Integer> Movable = new ArrayList<Integer>();
        if (PlayerPiece == 1 || (PlayerPiece == 2 && IsKing[x1][y1])) {
            try {
                if (SelectCorrectMove(x1, y1, x1 + 2, y1 + 2)) {
                    Movable.add(1);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            try {
                if (SelectCorrectMove(x1, y1, x1 - 2, y1 + 2)) {
                    Movable.add(2);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        if (PlayerPiece == 2 || (PlayerPiece == 1 && IsKing[x1][y1])) {
            try {
                if (SelectCorrectMove(x1, y1, x1 + 2, y1 - 2)) {
                    Movable.add(3);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            try {
                if (SelectCorrectMove(x1, y1, x1 - 2, y1 - 2)) {
                    Movable.add(4);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        Random random = new Random();
        int movePattern = Movable.get(random.nextInt(Movable.size() - 1)); // 0からMovable.size()-1までの整数を生成
        if (movePattern == 1) {
            IsKing[x1 + 2][y1 + 2] = IsKing[x1][y1];
            Board[x1][y1] = 0;
            Board[x1 + 2][y1 + 2] = PlayerPiece;
            IsKing[x1][y1] = false;

            Board[x1+1][y1+1] = 0;
            IsKing[x1+1][y1+1] = false;

            ContinuousMove(x1 + 2, y1 + 2);
        } else if (movePattern == 2) {
            IsKing[x1 + 2][y1 + 2] = IsKing[x1][y1];
            Board[x1][y1] = 0;
            Board[x1 - 2][y1 + 2] = PlayerPiece;
            IsKing[x1][y1] = false;

            Board[x1-1][y1+1] = 0;
            IsKing[x1-1][y1+1] = false;

            ContinuousMove(x1 - 2, y1 + 2);
        } else if (movePattern == 3) {
            IsKing[x1 + 2][y1 + 2] = IsKing[x1][y1];
            Board[x1][y1] = 0;
            Board[x1 + 2][y1 - 2] = PlayerPiece;
            IsKing[x1][y1] = false;

            Board[x1+1][y1-1] = 0;
            IsKing[x1+1][y1-1] = false;

            ContinuousMove(x1 + 2, y1 - 2);
        } else if (movePattern == 4) {
            IsKing[x1 + 2][y1 + 2] = IsKing[x1][y1];
            Board[x1][y1] = 0;
            Board[x1 - 2][y1 - 2] = PlayerPiece;
            IsKing[x1][y1] = false;

            Board[x1-1][y1-1] = 0;
            IsKing[x1-1][y1-1] = false;

            ContinuousMove(x1 - 2, y1 - 2);
        }
    }

    //相手の駒をジャンプ可能な手があり、その場合にその手を選択しているかどうか
    private static boolean SelectCorrectMove(int x1, int y1, int x2, int y2) {
        //yml読み込み
        int enemypiece;
        if (PlayerPiece == 1) {
            enemypiece = 2;
        } else {
            enemypiece = 1;
        }
        //placeableチェック
        int checkX = 0;
        int checkY = 0;
        boolean exist = false;
        for (int[] row : Board) {
            for (int piece : row) {
                if (piece == PlayerPiece) {
                    if (IsJumpable(checkX, checkY, 1, 1)) {
                        exist = true;
                        if ((checkX == x1) && (checkY == y1)) {
                            return true;
                        }
                    }
                    if (IsJumpable(checkX, checkY, -1, 1)) {
                        exist = true;
                        if ((checkX == x1) && (checkY == y1)) {
                            return true;
                        }
                    }
                    if (IsJumpable(checkX, checkY, 1, -1)) {
                        exist = true;
                        if ((checkX == x1) && (checkY == y1)) {
                            return true;
                        }
                    }
                    if (IsJumpable(checkX, checkY, -1, -1)) {
                        exist = true;
                        if ((checkX == x1) && (checkY == y1)) {
                            return true;
                        }
                    }
                }
                checkY++;
            }
            checkX++;
        }
        return !exist;
    }
    private static boolean ExistJumpMove(int PlayerPiece2) {
        int checkX = 0;
        int checkY = 0;
        for (int[] row : Board) {
            for (int piece : row) {
                if (piece == PlayerPiece2) {
                    if (IsJumpable(checkX, checkY, 1, 1)) {
                        return true;
                    }
                    if (IsJumpable(checkX, checkY, -1, 1)) {
                        return true;
                    }
                    if (IsJumpable(checkX, checkY, 1, -1)) {
                        return true;
                    }
                    if (IsJumpable(checkX, checkY, -1, -1)) {
                        return true;
                    }
                }
                checkY++;
            }
            checkX++;
        }
        return false;
    }

    //駒をおけるか
    public static void BoardInput(String Boardname,int x1, int y1, int x2, int y2) {
        ErrorType =0;
        LoadData(Boardname);
        PlayerPiece = yml.getInt(Boardname +".Turn");

        //チェッカーで使用しないマスを選択
        if ((((x1 % 2) + (y1 % 2) % 2) == 1) || (((x2 % 2) + (y2 % 2) % 2) == 1)) {
            ErrorType =1;
            return;
        }

        int selectpiece = Board[x1][y1];

        //破壊可能な手があるのにも関わらずその手を選択していない場合を除外
        if (!SelectCorrectMove(x1, y1, x2, y2)) {
            //相手の駒を飛び越えられる手が存在します。飛び越えられる手を選択してください。
            ErrorType =2;
            return;
        }

        //
        if (abs(x1 - x2) == 1 && abs(y1 - y2) == 1) {
            if (Board[x2][y2] == PlayerPiece) {
                Board[x1][y1] = 0;
                IsKing[x2][y2] = IsKing[x1][y1];
                Board[x2][y2] = PlayerPiece;
                IsKing[x1][y1] = false;
            }
        } else if (abs(x1 - x2) == 2 && abs(y1 - y2) == 2) {
            if (IsJumpable(x1, y1, x2 - x1, y2 - y1)) {
                Board[x1][y1] = 0;
                IsKing[x2][y2] = IsKing[x1][y1];
                Board[x2][y2] = PlayerPiece;
                IsKing[x1][y1] = false;

                Board[x1+(x2 - x1)/2][y1+(y1 - y2)/2] = 0;
                IsKing[x1+(x2 - x1)/2][y1+(y1 - y2)/2] = false;

                ContinuousMove(x2, y2);
            }
        } else {
            //最初に選択した駒の一つ斜めの駒か、相手の駒を飛び越えられる場合は二つ斜め前の駒を選択してください。
            ErrorType =3;
            return;
        }

        saveData(Boardname);
        if(PlayerPiece ==1) {
            PlayerPiece =2;
        }else {
            PlayerPiece =1;
        }
        CreateKing();
    }
    private static void CreateKing() {
        if(Board[1][0]==2) IsKing[1][0] = true;
        if(Board[3][0]==2) IsKing[3][0] = true;
        if(Board[5][0]==2) IsKing[5][0] = true;
        if(Board[7][0]==2) IsKing[7][0] = true;
        if(Board[1][7]==1) IsKing[1][0] = true;
        if(Board[3][7]==1) IsKing[3][0] = true;
        if(Board[5][7]==1) IsKing[5][0] = true;
        if(Board[7][7]==1) IsKing[7][0] = true;
    }

    public static int WinCheck(String Boardname) {
        LoadData(Boardname);
        boolean ExistBlackMen = false;
        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[i].length; j++) {
                if (Board[i][j] == 1) {
                    ExistBlackMen = true;
                    break; // 内側のループを抜ける
                }
            }
            if (ExistBlackMen) {
                break; // 外側のループも抜ける
            }
        }
        if (!ExistBlackMen) return 2;

        boolean ExistWhiteMen = false;
        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[i].length; j++) {
                if (Board[i][j] == 2) {
                    ExistWhiteMen = true;
                    break; // 内側のループを抜ける
                }
            }
            if (ExistWhiteMen) {
                break; // 外側のループも抜ける
            }
        }
        if (!ExistWhiteMen) return 1;

        if(!ExistJumpMove(1)) return 2;
        if(!ExistJumpMove(2)) return 1;

        return 0;
    }
}