package net.dialingspoon.terrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private Tile[][] board;
    private int numRows;
    private int numCols;
    private List<Region> regions;

    public Board(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Tile[numRows][numCols];
        regions = new ArrayList<>();
        // Initialize each tile with default values
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                board[i][j] = new Tile(false, 0, 0);
            }
        }
    }

    public Board(Tile[][] board, int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        regions = new ArrayList<>();
    }

    public void setTile(int row, int col, Tile tile) {
        if (row >= 0 && row < numRows && col >= 0 && col < numCols) {
            board[row][col] = tile;
        }
    }

    public Tile getTile(int row, int col) {
        if (row >= 0 && row < numRows && col >= 0 && col < numCols) {
            return board[row][col];
        }
        return null;
    }

    public void printBoard() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (board[i][j].isOn()) {
                    System.out.print("1");
                } else {
                    System.out.print("0");
                }
                System.out.print(board[i][j].getColor());
                System.out.print(board[i][j].getNumber());
                System.out.print(board[i][j].mustBeOn());
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public boolean isValid() {
        // Check if tiles with mustBeOn property match their current state
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Tile tile = getTile(i, j);
                if (tile.mustBeOn() != -1 && (tile.mustBeOn() == 1) != tile.isOn()) {
                    return false; // Invalid state, mustBeOn doesn't match isOn
                }
            }
        }

        // Memoization for region validation
        Map<Region, Boolean> regionValidationCache = new HashMap<>();

        // Check if tiles with the same region have matching colors and non-zero numbers
        identifyRegions();
        for (Region region : regions) {
            boolean validationData;
            if (regionValidationCache.containsKey(region)) validationData = regionValidationCache.get(region);
            else{
                validationData = validateRegion(region);
                regionValidationCache.put(region, validationData);
            }

            if (!validationData) {
                return false; // Early termination if region is invalid
            }
        }

        return true;
    }

    private boolean validateRegion(Region region) {
        int regionColor = -1; // Initialize region color
        Map<Integer, Integer> colorCount = new HashMap<>(); // Map to count colors
        int sumOfNumbers = 0; // Sum of numbers in the region

        // Store frequently used values to minimize recalculation
        List<int[]> tilesInRegion = region.getTiles();
        int regionSize = tilesInRegion.size();
        Tile[] regionTiles = new Tile[regionSize];
        for (int i = 0; i < regionSize; i++) {
            int[] tilePos = tilesInRegion.get(i);
            regionTiles[i] = getTile(tilePos[0], tilePos[1]);
        }

        for (int i = 0; i < regionSize; i++) {
            Tile tile = regionTiles[i];
            int touching = tile.getTouching();

            if (tile.getNumber() != 0) {
                if (regionColor == -1) {
                    regionColor = tile.getColor();
                } else if (regionColor != tile.getColor()) {
                    return false; // Early termination if different color in the same region
                }
                sumOfNumbers += tile.getNumber();

                // Check if other tiles with the same color and no number exist
                for (int j = 0; j < regionSize; j++) {
                    if (i != j) {
                        Tile otherTile = regionTiles[j];
                        if (otherTile.getColor() == tile.getColor() && otherTile.getNumber() == 0 && otherTile.getTouching() == -1) {
                            colorCount.put(tile.getColor(), colorCount.getOrDefault(tile.getColor(), 0) + 1);
                            break; // No need to check further for this color
                        }
                    }
                }
            } else if (tile.getColor() != 0 && touching == -1) {
                colorCount.put(tile.getColor(), colorCount.getOrDefault(tile.getColor(), 0) + 1);
            }

            // Check adjacent tiles if tile.getTouching() is not -1
            if (touching != -1) {
                int adjacentOnCount = countAdjacentOnTiles(tilesInRegion, i, tile.isOn());
                if (adjacentOnCount != touching) {
                    return false; // Early termination if invalid number of adjacent tiles
                }
                System.out.println(tile.getColor());
                if (tile.getColor() != 0) {
                    // Check if other tiles with the same color and no number exist
                    for (int j = 0; j < regionSize; j++) {
                        if (i != j) {
                            Tile otherTile = regionTiles[j];
                            if (otherTile.getColor() == tile.getColor() && otherTile.getNumber() == 0 && otherTile.getTouching() == -1) {
                                colorCount.put(tile.getColor(), colorCount.getOrDefault(tile.getColor(), 0) + 1);
                                break; // No need to check further for this color
                            }
                        }
                    }
                }
            }
        }

        for (int count : colorCount.values()) {
            if (count == 1 || count >= 3) {
                return false; // Early termination if invalid color count
            }
        }

        return sumOfNumbers == regionSize || sumOfNumbers == 0; // Early termination if sum of numbers is not equal to the number of tiles
    }

    // Helper method to count adjacent on tiles
    private int countAdjacentOnTiles(List<int[]> tilesInRegion, int currentIndex, boolean isOn) {
        int count = 0;
        int[] currentIndexPos = tilesInRegion.get(currentIndex);
        int numRows = this.numRows;
        int numCols = this.numCols;

        int[][] adjacentOffsets = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (int[] offset : adjacentOffsets) {
            int newRow = currentIndexPos[0] + offset[0];
            int newCol = currentIndexPos[1] + offset[1];

            if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
                Tile adjacentTile = getTile(newRow, newCol);
                if (adjacentTile.isOn() == isOn) {
                    count++;
                }
            }
        }

        return count;
    }


    // Recursive method to generate all possible board states
    public List<Board> generateAllStates() {
        List<Board> validStates = new ArrayList<>();
        generateStates(0, 0, board, validStates);
        return validStates;
    }

    private void generateStates(int row, int col, Tile[][] board, List<Board> validStates) {
        if (row == numRows) {
            Board copy = new Board(numRows, numCols);
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    Tile originalTile = board[i][j];
                    Tile copyTile = new Tile(
                            originalTile.isOn(),
                            originalTile.getColor(),
                            originalTile.getNumber(),
                            originalTile.mustBeOn(),
                            originalTile.getTouching()// Preserve properties
                    );
                    copy.setTile(i, j, copyTile);
                }
            }
            if (copy.isValid()) {
                validStates.add(copy);
            }
            return;
        }

        // Generate all possible board states recursively for on/off tiles
        for (boolean isOn : new boolean[]{true, false}) {
            setTile(row, col, new Tile(isOn, board[row][col].getColor(), board[row][col].getNumber(), board[row][col].mustBeOn(), board[row][col].getTouching())); // Preserve color and number
            int nextRow = col == numCols - 1 ? row + 1 : row;
            int nextCol = col == numCols - 1 ? 0 : col + 1;
            generateStates(nextRow, nextCol, board, validStates);
        }
    }

    public void identifyRegions() {
        boolean[][] visited = new boolean[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (!visited[i][j]) {
                    Region region = new Region();
                    dfs(i, j, region, visited, getTile(i, j).isOn());
                    regions.add(region);
                }
            }
        }
    }

    // Depth-first search to identify a region
    private void dfs(int row, int col, Region region, boolean[][] visited, boolean on) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols ||
                visited[row][col] || getTile(row, col).isOn() != on) {
            return;
        }

        visited[row][col] = true;
        region.addTile(row, col);

        // Explore adjacent tiles (not diagonally)
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int k = 0; k < 4; k++) {
            int newRow = row + dx[k];
            int newCol = col + dy[k];
            dfs(newRow, newCol, region, visited, on);
        }
    }

    public List<Region> getRegions() {
        return regions;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }
}