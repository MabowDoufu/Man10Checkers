package main.mabowdoufu;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.spi.AbstractResourceBundleProvider;
import static java.lang.Math.*;

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

    public static boolean IsMyMen(int[][] board,int playermen,int x1, int y1){

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

    //directionは1または-1のみをとる
    public static boolean IsJumpable(int[][] board,int playermen,int x1,int y1,int xdirection,int ydirection){
        int enemymen;
        if(playermen==1){
            enemymen= 2;
        }else{
            enemymen=1;
        }
        try {
            if (board[x1 + xdirection][y1 +ydirection] == enemymen && board[x1 + 2*xdirection][y1 + 2*ydirection] == 0) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static void ContinuousMove(int[][] board,int playermen, int x1,int y1,boolean IsKing){
        List<Integer> Movable = new ArrayList<Integer>();
        if(playermen==1 ||(playermen==2 && IsKing)) {
            try {
                if(ExistJumpMove(board, playermen, x1, y1, x1 + 2, y1 + 2)){
                    Movable.add(1);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            try {
                if(ExistJumpMove(board, playermen, x1, y1, x1 - 2, y1 + 2)){
                    Movable.add(2);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        if(playermen==2 ||(playermen==1 && IsKing)) {
            try {
                if(ExistJumpMove(board, playermen, x1, y1, x1 + 2, y1 - 2)){
                    Movable.add(3);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            try {
                if(ExistJumpMove(board, playermen, x1, y1, x1 - 2, y1 - 2)){
                    Movable.add(4);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        Random random = new Random();
        int movePattern = Movable.get(random.nextInt(Movable.size()-1)); // 0からMovable.size()-1までの整数を生成
        if(movePattern ==1){
            board[x1][y1]= 0;
            board[x1+2][y1+2]= playermen;
            ContinuousMove(board,playermen,x1+2,y1+2,IsKing);
        }else if(movePattern ==2){
            board[x1][y1]= 0;
            board[x1-2][y1+2]= playermen;
            ContinuousMove(board,playermen,x1-2,y1+2,IsKing);
        }else if(movePattern ==3) {
            board[x1][y1] = 0;
            board[x1 + 2][y1 - 2] = playermen;
            ContinuousMove(board,playermen,x1+2,y1-2,IsKing);
        }else if(movePattern ==4) {
            board[x1][y1] = 0;
            board[x1 - 2][y1 - 2] = playermen;
            ContinuousMove(board,playermen,x1-2,y1-2,IsKing);
        }
    }
    //相手の駒をジャンプ可能な手があり、その場合にその手を選択しているかどうか
    public static boolean ExistJumpMove(int[][] board,int playermen,int x1,int y1,int x2,int y2){
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
        boolean exist=false;
        for(int[] row : board ) {
            for(int men : row) {
                if(men == playermen) {
                    if(IsJumpable(board,playermen,checkX,checkY,1,1)){
                        exist = true;
                        if ((checkX == x1) && (checkY == y1)) {
                            return true;
                        }
                    }
                    if(IsJumpable(board,playermen,checkX,checkY,-1,1)){
                        exist = true;
                        if ((checkX == x1) && (checkY == y1)) {
                            return true;
                        }
                    }
                    if(IsJumpable(board,playermen,checkX,checkY,1,-1)){
                        exist = true;
                        if ((checkX == x1) && (checkY == y1)) {
                            return true;
                        }
                    }
                    if(IsJumpable(board,playermen,checkX,checkY,-1,-1)){
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
        if(!ExistJumpMove(board,playermen,x1,y1,x2,y2)){
            //相手の駒を飛び越えられる手が存在します。飛び越えられる手を選択してください。
            return;
        }

        //
        if(abs(x1-x2)==1 && abs(y1-y2)==1){

        } else if(abs(x1-x2)==2 && abs(y1-y2)==2){
            if(IsJumpable(board,playermen,x1,y1,x2-x1,y2-y1)){
                board[x1][y1]= 0;
                board[x2][y2]= playermen;

            }
        }else{
            //最初に選択した駒の一つ斜めの駒か、相手の駒を飛び越えられる場合は二つ斜め前の駒を選択してください。
            return;
        }


        saveBoard(boardname,board);
    }
    
}
