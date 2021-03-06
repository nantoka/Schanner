package de.haidozo.sudoku.game;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import de.haidozo.sudoku.R;

public class Game {

    private static String TAG = "Sudoku";

    private final int boardSize = 9;

    private int puzzle[];
    boolean[] predefined;

    private final int used[][][] = new int[boardSize][boardSize][];

    private BoardView boardView;

    public Game(String puzzle) {
        predefined = new boolean[boardSize*boardSize];
        this.puzzle = fromPuzzleString(puzzle);
        calculateUsedTiles();
    }

    public void showKeypadOrError(int selX, int selY) {
        int tiles[] = getUsedTiles(selX, selY);
        if(tiles.length == boardSize) {
            Toast toast = Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Log.d(TAG, "showKeypad: used = " + toPuzzleString(tiles));
            Dialog v = new Keypad(this, tiles, boardView);
            v.show();
        }

    }

    public boolean setTileIfValid(int selX, int selY, int tile) {
        int tiles[] = getUsedTiles(selX, selY);
        if(tile != 0) {
            for(int t : tiles) {
                if(tile == t) {
                    return false;
                }
            }
        }
        setTile(selX, selY, tile);
        calculateUsedTiles();
        return true;
    }

    public boolean isTileValid(int selX, int selY, int tile) {
        int tiles[] = getUsedTiles(selX, selY);
        if(tile != 0) {
            for(int t : tiles) {
                if(tile == t) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getTile(int selX, int selY) {
        return puzzle[boardSize * selY + selX];
    }

    public void setTile(int selX, int selY, int tile) {
        puzzle[boardSize * selY + selX] = tile;
    }

    public int[] getUsedTiles(int selX, int selY) {
        return used[selX][selY];
    }

    public int[] getUnusedTiles(int selX, int selY) {
        int[] unused = new int[boardSize];

        for(int i=1; i<=9; i++) {
            boolean isUsed = false;
            for(int u : used[selX][selY]) {
                if(u == i || getTile(selX, selY) == i)  {
                    isUsed = true;
                    break;
                }
            }
            unused[i-1] = isUsed ? 0 : i;
        }


        return unused;
    }

    public void calculateUsedTiles() {
        for(int x=0; x < boardSize; x++) {
            for(int y=0; y < boardSize; y++) {
                used[x][y] = calculateUsedTiles(x,y);
            }
        }
    }

    private int[] calculateUsedTiles(int x, int y) {
        ArrayList<Integer> used = new ArrayList<Integer>();

        // search in a row
        for(int i=0; i<boardSize; i++) {
            int t = getTile(i, y);
            if(i != x && t != 0) {
                used.add(t);
            }
        }

        // search in a column
        for(int i=0; i<boardSize; i++) {
            int t = getTile(x, i);
            if(i != y && t != 0 && !used.contains(t)) {
                used.add(t);
            }
        }

        // search in the current box
        int startX = (x / 3) * 3;
        int startY = (y / 3) * 3;

        for(int i=startX; i<startX+Math.sqrt(boardSize); i++) {
            for(int j=startY; j<startY+Math.sqrt(boardSize); j++) {
                int t = getTile(i, j);
                if(i != x && j != y && t != 0 && !used.contains(t)) {
                    used.add(t);
                }
            }
        }

        // convert ArrayList to array
        int[] u = new int[used.size()];
        for(int i=0; i<used.size(); i++) {
            u[i] = used.get(i);
        }
        return u;
    }

    private int[] getPuzzle(int diff) {

        //return gridGenerator.generateGrid(diff);

        String puzzle;
        //To do: continue last game
        switch(diff) {
            case DIFFICULTY_CONTINUE:
                puzzle = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE, easyPuzzle);
                break;
            case DIFFICULTY_HARD:
                puzzle = hardPuzzle;
                break;
            case DIFFICULTY_MEDIUM:
                puzzle = mediumPuzzle;
                break;
            case DIFFICULTY_EASY:
                puzzle = easyPuzzle;
                break;
            default:
                puzzle = easyPuzzle;
                break;
        }

        return fromPuzzleString(puzzle);
    }

    static private String toPuzzleString(int[] puz) {
        StringBuilder buf = new StringBuilder();
        for(int e : puz) {
            buf.append(e);
        }

        return buf.toString();
    }

    static protected int[] fromPuzzleString(String puzzle) {
        int puz[] = new int[puzzle.length()];
        for(int i=0; i<puzzle.length(); i++) {
            puz[i] = puzzle.charAt(i) - '0';
        }
        return puz;
    }

    public String getTileString(int x, int y) {
        int v = getTile(x, y);
        if(v == 0) {
            return "";
        } else {
            return String.valueOf(v);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, GridPrefs.class));
                return true;
        }
        return false;
    }

    public boolean solvePuzzle() {
        return solver.solve();
    }

    public void calculatePredefined() {
        for(int i=0; i<puzzle.length; i++) {
            if(puzzle[i] == 0) {
                predefined[i] = false;
            } else {
                predefined[i] = true;
            }
        }
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public boolean[] getPredefined() {
        return predefined;
    }

}
