package utils;

import agents.MapCell;
import launchers.SimulationLauncher;

public class MapState {
    /*
    public static final MapCell[][] initialState = new MapCell[][] {
            { new MapCell(0,0,0,0), new MapCell(1,0, 33,0),new MapCell(2,0,66,0), new MapCell(3,0, 100,0) },
            { new MapCell(0,1,0,33), new MapCell(1,1, 33,33),new MapCell(2,1,66,33), new MapCell(3,1, 100,33) },
            { new MapCell(0,2,0,66), new MapCell(1,2, 33,66),new MapCell(2,2,66,66), new MapCell(3,2, 100,66) },
            { new MapCell(0,3,0,100), new MapCell(1,3, 33,100),new MapCell(2,3,66,100), new MapCell(3,3, 100,100) }
    };*/

    public static MapCell[][] createMapState(SimulationLauncher launcher, int width, int length) {
        MapCell[][] mapCell = new MapCell[width][length];
        for(int i = 0; i < width; i++)
            for(int j = 0; j < length; j++)
                if(j == 3)
                    mapCell[i][j] = new MapCell(launcher,i,j,0,0, 1);
                else if(i == 7){
                    mapCell[i][j] = new MapCell(launcher,i,j,0,0, 2);
                }
                else if(i > 10 && i < 20 && j > 30 && j < 40){
                    mapCell[i][j] = new MapCell(launcher,i,j,0,0, 3);
                }
                else{
                    mapCell[i][j] = new MapCell(launcher,i,j,100/width*i,100/length*j, 0);

                }

            return mapCell;

    }
}
