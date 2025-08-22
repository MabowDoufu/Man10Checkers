package main.mabowdoufu;

import org.bukkit.configuration.ConfigurationSection;
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
    public static int[][] board = new int[8][8];
    public static boolean[][] isking = new boolean[8][8];
    public static int playerpiece;
    //のちのちsysから切り離し
    public static void CreateGame(String boardname) {
        File f = new File("plugins/Man10Checkers/game.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        board[1][0] = 1;
        board[3][0] = 1;
        board[5][0] = 1;
        board[7][0] = 1;
        board[1][1] = 1;
        board[3][1] = 1;
        board[5][1] = 1;
        board[7][1] = 1;
        board[1][2] = 1;
        board[3][2] = 1;
        board[5][2] = 1;
        board[7][2] = 1;
        board[1][5] = 2;
        board[3][5] = 2;
        board[5][5] = 2;
        board[7][5] = 2;
        board[1][6] = 2;
        board[3][6] = 2;
        board[5][6] = 2;
        board[7][6] = 2;
        board[1][7] = 2;
        board[3][7] = 2;
        board[5][7] = 2;
        board[7][7] = 2;
        yml.getKeys(true);
        yml.set(boardname+ ".board", board);
        yml.set(boardname+ ".IsKing", isking);
        yml.set(boardname+ ".DuringGame", false);
        yml.set(boardname+ ".Recruiting", false);


        playerpiece =1;

        try {
            yml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void LoadBoard(String boardname) {
        File f = new File("plugins/Man10Checkers/game.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        board = (int[][]) yml.get("board");
        isking = (boolean[][]) yml.get("isking");
    }

    public static void saveBoard(String boardname, int[][] board) {
        File f = new File("plugins/Man10Checkers/game.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        yml.set("board", board);
        yml.set("isking", isking);
        try {
            yml.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean IsMyMen(int x1, int y1) {

        int selectpiece = board[x1][y1];
        //相手の駒選択しているかどうか
        if (playerpiece == 1 && ((selectpiece == 3) || (selectpiece == 4))) {
            return false;
        }
        if (playerpiece == 2 && ((selectpiece == 1) || (selectpiece == 2))) {
            return false;
        }
        return true;
    }

    //directionは1または-1のみをとる
    private static boolean IsJumpable(int x1, int y1, int xdirection, int ydirection) {
        int enemypiece;
        if (playerpiece == 1) {
            enemypiece = 2;
        } else {
            enemypiece = 1;
        }
        try {
            if (board[x1 + xdirection][y1 + ydirection] == enemypiece && board[x1 + 2 * xdirection][y1 + 2 * ydirection] == 0) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void ContinuousMove(int x1, int y1) {
        List<Integer> Movable = new ArrayList<Integer>();
        if (playerpiece == 1 || (playerpiece == 2 && isking[x1][y1])) {
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
        if (playerpiece == 2 || (playerpiece == 1 && isking[x1][y1])) {
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
            isking[x1 + 2][y1 + 2] = isking[x1][y1];
            board[x1][y1] = 0;
            board[x1 + 2][y1 + 2] = playerpiece;
            isking[x1][y1] = false;
            ContinuousMove(x1 + 2, y1 + 2);
        } else if (movePattern == 2) {
            isking[x1 + 2][y1 + 2] = isking[x1][y1];
            board[x1][y1] = 0;
            board[x1 - 2][y1 + 2] = playerpiece;
            isking[x1][y1] = false;
            ContinuousMove(x1 - 2, y1 + 2);
        } else if (movePattern == 3) {
            isking[x1 + 2][y1 + 2] = isking[x1][y1];
            board[x1][y1] = 0;
            board[x1 + 2][y1 - 2] = playerpiece;
            isking[x1][y1] = false;
            ContinuousMove(x1 + 2, y1 - 2);
        } else if (movePattern == 4) {
            isking[x1 + 2][y1 + 2] = isking[x1][y1];
            board[x1][y1] = 0;
            board[x1 - 2][y1 - 2] = playerpiece;
            isking[x1][y1] = false;
            ContinuousMove(x1 - 2, y1 - 2);
        }
    }

    //相手の駒をジャンプ可能な手があり、その場合にその手を選択しているかどうか
    private static boolean SelectCorrectMove(int x1, int y1, int x2, int y2) {
        //yml読み込み
        int enemypiece;
        if (playerpiece == 1) {
            enemypiece = 2;
        } else {
            enemypiece = 1;
        }
        //placeableチェック
        int checkX = 0;
        int checkY = 0;
        boolean exist = false;
        for (int[] row : board) {
            for (int piece : row) {
                if (piece == playerpiece) {
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
    private static boolean ExistJumpMove(int playerpiece2) {
        int checkX = 0;
        int checkY = 0;
        for (int[] row : board) {
            for (int piece : row) {
                if (piece == playerpiece2) {
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
    public static void BoardInput(String boardname,int x1, int y1, int x2, int y2) {

        //チェッカーで使用しないマスを選択
        if ((((x1 % 2) + (y1 % 2) % 2) == 1) || (((x2 % 2) + (y2 % 2) % 2) == 1)) {
            return;
        }

        int selectpiece = board[x1][y1];

        //破壊可能な手があるのにも関わらずその手を選択していない場合を除外
        if (!SelectCorrectMove(x1, y1, x2, y2)) {
            //相手の駒を飛び越えられる手が存在します。飛び越えられる手を選択してください。
            return;
        }

        //
        if (abs(x1 - x2) == 1 && abs(y1 - y2) == 1) {
            if (board[x2][y2] == playerpiece) {
                board[x1][y1] = 0;
                isking[x2][y2] = isking[x1][y1];
                board[x2][y2] = playerpiece;
                isking[x1][y1] = false;
            }
        } else if (abs(x1 - x2) == 2 && abs(y1 - y2) == 2) {
            if (IsJumpable(x1, y1, x2 - x1, y2 - y1)) {
                board[x1][y1] = 0;
                isking[x2][y2] = isking[x1][y1];
                board[x2][y2] = playerpiece;
                isking[x1][y1] = false;
                ContinuousMove(x2, y2);
            }
        } else {
            //最初に選択した駒の一つ斜めの駒か、相手の駒を飛び越えられる場合は二つ斜め前の駒を選択してください。
            return;
        }

        saveBoard(boardname, board);
        if(playerpiece ==1) {
            playerpiece =2;
        }else {
            playerpiece =1;
        }
    }
    private void CreateKing(int[][] board, boolean[][] IsKing) {
        if(board[1][0]==2) isking[1][0] = true;
        if(board[3][0]==2) isking[3][0] = true;
        if(board[5][0]==2) isking[5][0] = true;
        if(board[7][0]==2) isking[7][0] = true;
        if(board[1][7]==1) isking[1][0] = true;
        if(board[3][7]==1) isking[3][0] = true;
        if(board[5][7]==1) isking[5][0] = true;
        if(board[7][7]==1) isking[7][0] = true;
    }

    public static int WinCheck() {
        boolean ExistBlackMen = false;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 1) {
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
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 2) {
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