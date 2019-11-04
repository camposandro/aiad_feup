package utils;

import java.util.Random;
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
        /*for(int i = 0; i < width; i++)
            for(int j = 0; j < length; j++)
                if(j == 3)
                    mapCell[i][j] = new MapCell(launcher,i,j,0,0, 1);
                else if(i == 7){
                    mapCell[i][j] = new MapCell(launcher,i,j,0,0, 2);
                }
                else if(i > 10 && i < 20 && j > 30 && j < 40){
                    mapCell[i][j] = new MapCell(launcher,i,j,0,0, 3);
                }
                else if( (i == 55 && j == 55) || (i >= 58 && i <= 60 && j >= 58 && j <= 60) ){
                    mapCell[i][j] = new MapCell(launcher,i,j,0,0, 4);
                }
                else{
                    mapCell[i][j] = new MapCell(launcher,i,j,100/width*i,100/length*j, 0);

                }*/


        Random rand = new Random();

        int randVeg = rand.nextInt(101);
        int randHum = rand.nextInt(101);

        int newVeg;
        int newHum;

        int vegMax;
        int vegMin;

        int humMax;
        int humMin;

        int range = 8;

        //Generate vegetation
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                if(i == 0){ // first column
                    if(j == 0){ // first row
                        mapCell[i][j] = new MapCell(launcher,i, j, randVeg, randHum, 0);
                    }
                    else{ // other columns
                        if(mapCell[i][j-1].getVegetationDensity() + range > 100){
                            vegMax = 100;
                            vegMin = mapCell[i][j-1].getVegetationDensity() - range;
                        }
                        else if(mapCell[i][j-1].getVegetationDensity() - range < 0){
                            vegMin = 0;
                            vegMax = mapCell[i][j-1].getVegetationDensity() + range;
                        }
                        else{
                            vegMin = mapCell[i][j-1].getVegetationDensity() - range;
                            vegMax = mapCell[i][j-1].getVegetationDensity() + range;
                        }


                        if(mapCell[i][j-1].getHumidityPercentage() + range > 100){
                            humMax = 100;
                            humMin = mapCell[i][j-1].getHumidityPercentage() - range;
                        }
                        else if(mapCell[i][j-1].getHumidityPercentage() - range < 0){
                            humMin = 0;
                            humMax = mapCell[i][j-1].getHumidityPercentage() + range;
                        }
                        else{
                            humMin = mapCell[i][j-1].getHumidityPercentage() - range;
                            humMax = mapCell[i][j-1].getHumidityPercentage() + range;
                        }


                        newVeg = rand.nextInt(vegMax - vegMin) + vegMin;
                        newHum = rand.nextInt(humMax - humMin) + humMin;


                        mapCell[i][j] = new MapCell(launcher, i, j, newVeg, newHum, 0);
                    }
                }
                else{ // other rows
                    if(j == 0){ // first row
                        if(mapCell[i-1][j].getVegetationDensity() + range > 100){
                            vegMax = 100;
                            vegMin = mapCell[i-1][j].getVegetationDensity() - range;
                        }
                        else if(mapCell[i-1][j].getVegetationDensity() - range < 0){
                            vegMin = 0;
                            vegMax = mapCell[i-1][j].getVegetationDensity() + range;
                        }
                        else{
                            vegMin = mapCell[i-1][j].getVegetationDensity() - range;
                            vegMax = mapCell[i-1][j].getVegetationDensity() + range;
                        }


                        if(mapCell[i-1][j].getHumidityPercentage() + range > 100){
                            humMax = 100;
                            humMin = mapCell[i-1][j].getHumidityPercentage() - range;
                        }
                        else if(mapCell[i-1][j].getHumidityPercentage() - range < 0){
                            humMin = 0;
                            humMax = mapCell[i-1][j].getHumidityPercentage() + range;
                        }
                        else{
                            humMin = mapCell[i-1][j].getHumidityPercentage() - range;
                            humMax = mapCell[i-1][j].getHumidityPercentage() + range;
                        }


                        newVeg = rand.nextInt(vegMax - vegMin) + vegMin;
                        newHum = rand.nextInt(humMax - humMin) + humMin;


                        mapCell[i][j] = new MapCell(launcher, i, j, newVeg, newHum, 0);
                    }
                    else{ // other columns
                        if(mapCell[i][j-1].getVegetationDensity() + range > 100){
                            vegMax = 100;
                            vegMin = mapCell[i][j-1].getVegetationDensity() - range;
                        }
                        else if(mapCell[i][j-1].getVegetationDensity() - range < 0){
                            vegMin = 0;
                            vegMax = mapCell[i][j-1].getVegetationDensity() + range;
                        }
                        else{
                            vegMin = mapCell[i][j-1].getVegetationDensity() - range;
                            vegMax = mapCell[i][j-1].getVegetationDensity() + range;
                        }


                        if(mapCell[i][j-1].getHumidityPercentage() + range > 100){
                            humMax = 100;
                            humMin = mapCell[i][j-1].getHumidityPercentage() - range;
                        }
                        else if(mapCell[i][j-1].getHumidityPercentage() - range < 0){
                            humMin = 0;
                            humMax = mapCell[i][j-1].getHumidityPercentage() + range;
                        }
                        else{
                            humMin = mapCell[i][j-1].getHumidityPercentage() - range;
                            humMax = mapCell[i][j-1].getHumidityPercentage() + range;
                        }


                        newVeg = rand.nextInt(vegMax - vegMin) + vegMin;
                        newHum = rand.nextInt(humMax - humMin) + humMin;


                        mapCell[i][j] = new MapCell(launcher, i, j, newVeg, newHum, 0);
                    }
                }
            }
        }

        //Generate Water bodies
            //Generate Lakes

            //Generate Rivers

        //Generate Houses
            //Generate Villages

            //Generate Remote Houses

        //Generate Roads
            //Generate Asphalt Roads

            //Generate Dirt Roads




        return mapCell;

    }
}
