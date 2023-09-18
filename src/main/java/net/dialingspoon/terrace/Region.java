package net.dialingspoon.terrace;

import java.util.ArrayList;
import java.util.List;

class Region {
    private List<int[]> tiles; // Store positions of tiles in the region

    public Region() {
        tiles = new ArrayList<>();
    }

    public void addTile(int row, int col) {
        tiles.add(new int[]{row, col});
    }

    public List<int[]> getTiles() {
        return tiles;
    }
}
