package main.mabowdoufu;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class BoardGameSys {
    // 盤面情報
    // 0:未設置, 1:黒 2:白
    public static int[][] grid = new int[8][8];

    //のちのちsysから切り離し
    public static void CreateGame(String boardname){
        File gameyml = new File("plugins/Man10Checkers/game/"+boardname+".yml");
        YamlConfiguration yml = new YamlConfiguration();
        grid[1][0] = 1;
        grid[3][0] = 1;
        grid[5][0] = 1;
        grid[7][0] = 1;
        grid[1][1] = 1;
        grid[3][1] = 1;
        grid[5][1] = 1;
        grid[7][1] = 1;
        grid[1][2] = 1;
        grid[3][2] = 1;
        grid[5][2] = 1;
        grid[7][2] = 1;
        grid[1][5] = 2;
        grid[3][5] = 2;
        grid[5][5] = 2;
        grid[7][5] = 2;
        grid[1][6] = 2;
        grid[3][6] = 2;
        grid[5][6] = 2;
        grid[7][6] = 2;
        grid[1][7] = 2;
        grid[3][7] = 2;
        grid[5][7] = 2;
        grid[7][7] = 2;
        yml.set("board",grid);
        try {
            yml.save(gameyml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] getBoard(String boardname){
        File gameyml = new File("plugins/Man10Checkers/game/"+boardname+".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
        return (int[][]) yml.get("board");
    }

    public static void saveBoard(String boardname, int[][] board){
        File gameyml = new File("plugins/Man10Checkers/game/"+boardname+".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(gameyml);
        yml.set("board",board);
        try {
            yml.save(gameyml);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean IsMyMen(int[][] board,int playermen,int x1, int y1, int x2, int y2 ){

        int selectmen = board[x1][y1];
        //相手の駒選択しているかどうか
        if(playermen==1 && ((selectmen==3)||(selectmen==4))){
            return false;
        }
        if(playermen==2 && ((selectmen==1)||(selectmen==2))){
            return false;
        }
        return true;
    }
    //相手の駒を破壊可能な手があり、その場合にその手を選択しているかどうか
    public static boolean IsBreakable(int[][] board,int playermen,int x1,int y1,int x2,int y2){
        //yml読み込み
        int enemymen;
        if(playermen==1){
            enemymen= 2;
        }else{
            enemymen=1;
        }
        //placeableチェック
        int checkX=0;
        int checkY=0;
        boolean isBreakable=false;
        for(int[] row : board ) {
            for(int men : row) {
                if(men == playermen) {
                    if( x2-x1==2 &&y2-y1 == 2) {
                        try {
                            if (board[checkX + 1][checkY + 1] == enemymen && board[checkX + 2][checkY + 2] == 0) {
                                isBreakable = true;
                                if ((checkX == x1) && (checkY == y1)) {
                                    return true;
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    if( x2-x1==-2 &&y2-y1 == 2) {
                        try {
                            if (board[checkX - 1][checkY + 1] == enemymen && board[checkX - 2][checkY + 2] == 0) {
                                isBreakable = true;
                                if ((checkX == x1) && (checkY == y1)) {
                                    return true;
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    if( x2-x1==2 &&y2-y1 == -2) {
                        try {
                            if (board[checkX + 1][checkY - 1] == enemymen && board[checkX + 2][checkY - 2] == 0) {
                                isBreakable = true;
                                if ((checkX == x1) && (checkY == y1)) {
                                    return true;
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    if( x2-x1==-2 &&y2-y1 == -2) {
                        try {
                            if (board[checkX - 1][checkY - 1] == enemymen && board[checkX - 2][checkY - 2] == 0) {
                                isBreakable = true;
                                if ((checkX == x1) && (checkY == y1)) {
                                    return true;
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }
                checkY++;
            }
            checkX++;
        }
        return !isBreakable;
    }
    //駒をおけるか
    public static void BoardInput(String boardname,int playermen,int x1, int y1, int x2, int y2 ){

        //チェッカーで使用しないマスを選択
        if((((x1%2)+(y1%2)%2)== 1)||(((x2%2)+(y2%2)%2)== 1) ){
            return;
        }

        //yml読み込み
        int[][] board = getBoard(boardname);
        int selectmen = board[x1][y1];

        //破壊可能な手があるのにも関わらずその手を選択していない場合を除外
        if(!IsBreakable(board,playermen,x1,y1,x2,y2)){
            return;
        }

        //


        saveBoard(boardname,board);
    }
    
}
