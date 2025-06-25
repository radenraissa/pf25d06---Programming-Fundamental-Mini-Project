package FPPROJECT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotPlayer {

    public int[] makeMove (Board board) {
        List<int[]> emptyCells = new ArrayList<>();

        for(int row = 0; row<Board.ROWS; row++){
            for(int col = 0; col < Board.COLS; ++col){
                if(board.cells[row][col].content == Seed.NO_SEED){
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        Random random = new Random();
        int[] chosenMove = emptyCells.get(random.nextInt(emptyCells.size()));
        return chosenMove;

    }

}
