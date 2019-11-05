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
        Random rand = new Random();

        int randVeg = rand.nextInt(101);
        int randHum = rand.nextInt(101);

        int newVeg;
        int newHum;

        int vegMax;
        int vegMin;

        int humMax;
        int humMin;

        int range = 15;

        //Generate vegetation
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                if(i == 0 && j == 0){ // first column &  first row
                        mapCell[i][j] = new MapCell(launcher,i, j, randVeg, randHum, 0);
                }

                else if(j == 0){ // first row
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
                    if(i == 0){
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
                    }
                    else{
                        if((mapCell[i-1][j].getVegetationDensity() + mapCell[i][j-1].getVegetationDensity())/2 + range > 100){
                            vegMax = 100;
                            vegMin = (mapCell[i-1][j].getVegetationDensity() + mapCell[i][j-1].getVegetationDensity())/2 - range;
                        }
                        else if((mapCell[i-1][j].getVegetationDensity() + mapCell[i][j-1].getVegetationDensity())/2 - range < 0){
                            vegMin = 0;
                            vegMax = (mapCell[i-1][j].getVegetationDensity() + mapCell[i][j-1].getVegetationDensity())/2 + range;
                        }
                        else{
                            vegMin = (mapCell[i-1][j].getVegetationDensity() + mapCell[i][j-1].getVegetationDensity())/2 - range;
                            vegMax = (mapCell[i-1][j].getVegetationDensity() + mapCell[i][j-1].getVegetationDensity())/2 + range;
                        }


                        if((mapCell[i-1][j].getHumidityPercentage() + mapCell[i][j-1].getHumidityPercentage())/2 + range > 100){
                            humMax = 100;
                            humMin = (mapCell[i-1][j].getHumidityPercentage() + mapCell[i][j-1].getHumidityPercentage())/2 - range;
                        }
                        else if((mapCell[i-1][j].getHumidityPercentage() + mapCell[i][j-1].getHumidityPercentage())/2 - range < 0){
                            humMin = 0;
                            humMax = (mapCell[i-1][j].getHumidityPercentage() + mapCell[i][j-1].getHumidityPercentage())/2 + range;
                        }
                        else{
                            humMin = (mapCell[i-1][j].getHumidityPercentage() + mapCell[i][j-1].getHumidityPercentage())/2 - range;
                            humMax = (mapCell[i-1][j].getHumidityPercentage() + mapCell[i][j-1].getHumidityPercentage())/2 + range;
                        }
                    }


                    newVeg = rand.nextInt(vegMax - vegMin) + vegMin;
                    newHum = rand.nextInt(humMax - humMin) + humMin;


                    mapCell[i][j] = new MapCell(launcher, i, j, newVeg, newHum, 0);
                }
            }
        }

        //Generate Water bodies
        mapCell = generateWaterBodies(mapCell);

        //Generate Houses
            //Generate Villages

            //Generate Remote Houses

        //Generate Roads
            //Generate Asphalt Roads

            //Generate Dirt Roads




        return mapCell;

    }

    public static MapCell[][] generateWaterBodies(MapCell[][] map){
        map = generateLakes(map);
        map = generateRivers(map);

        return map;
    }

    public static MapCell[][] generateLakes(MapCell[][] map){
        Random rand = new Random();

        int numOfLakes = rand.nextInt(map.length * map[0].length / 5000);
        int lakeMaximumRadius = 12;

        int[][] lakePositions = new int[numOfLakes][2];

        for(int i = 0; i < numOfLakes; i++){
            int lakeX = rand.nextInt(map.length);
            int lakeY = rand.nextInt(map[0].length);

            lakePositions[i][0] = lakeX;
            lakePositions[i][1] = lakeY;
        }

        for(int i = 0; i < numOfLakes; i++){
            int lakeRadius = rand.nextInt(lakeMaximumRadius);
            int minX, maxX, minY, maxY;
            //lake center
            map[lakePositions[i][0]][lakePositions[i][1]].setVegetationDensity(0);
            map[lakePositions[i][0]][lakePositions[i][1]].setHumidityPercentage(100);
            map[lakePositions[i][0]][lakePositions[i][1]].setSoilType(3);

            //lake size
            if(lakePositions[i][0] - lakeRadius < 0){
                minX = 0;
                maxX = lakePositions[i][0] + lakeRadius;
            }
            else if(lakePositions[i][0] + lakeRadius > map.length){
                minX = lakePositions[i][0] - lakeRadius;
                maxX = map.length;
            }
            else{
                minX = lakePositions[i][0] - lakeRadius;
                maxX = lakePositions[i][0] + lakeRadius;
            }

            if(lakePositions[i][1] - lakeRadius < 0){
                minY = 0;
                maxY = lakePositions[i][1] + lakeRadius - 1;
            }
            else if(lakePositions[i][1] + lakeRadius > map[0].length){
                minY = lakePositions[i][1] - lakeRadius;
                maxY = map.length;
            }
            else{
                minY = lakePositions[i][1] - lakeRadius;
                maxY = lakePositions[i][1] + lakeRadius - 1;
            }


            for(int m = minX; m < maxX; m++){
                for(int n = minY; n < maxY; n++){
                    if(calculateDist(lakePositions[i][0], lakePositions[i][1], m, n) < lakeRadius/2){//perto do centro
                        map[m][n].setVegetationDensity(0);
                        map[m][n].setHumidityPercentage(100);
                        map[m][n].setSoilType(3);
                    }
                    else{
                        int r = rand.nextInt(lakeRadius);
                        if(calculateDist(lakePositions[i][0], lakePositions[i][1], m, n) < r){
                            map[m][n].setVegetationDensity(0);
                            map[m][n].setHumidityPercentage(100);
                            map[m][n].setSoilType(3);
                        }
                    }
                }
            }
        }

        return map;
    }

    public static MapCell[][] generateRivers(MapCell[][] map){
        Random rand = new Random();

        int numOfRivers = rand.nextInt(map.length * map[0].length / 10000);
        int riverMaximumWidth = 6;

        System.out.println("Number of Rivers = " + numOfRivers);

        int[][] riverPositions = new int[numOfRivers][2];

        for(int i = 0; i < numOfRivers; i++){
            int riverX = rand.nextInt(map.length);
            int riverY = rand.nextInt(map[0].length);

            riverPositions[i][0] = riverX;
            riverPositions[i][1] = riverY;
        }

        for(int i = 0; i < numOfRivers; i++) {
            int riverWidth = rand.nextInt(riverMaximumWidth) + 1;

            System.out.println("River Width: " + riverWidth);

            System.out.println("River Center X = " + riverPositions[i][0]);
            System.out.println("River Center Y = " + riverPositions[i][1]);

            //river position
            map[riverPositions[i][0]][riverPositions[i][1]].setVegetationDensity(0);
            map[riverPositions[i][0]][riverPositions[i][1]].setHumidityPercentage(100);
            map[riverPositions[i][0]][riverPositions[i][1]].setSoilType(3);

            //river generation

            //South/North
                //river width

            //2 problems
                //riverwidth = 1, rio reto

                //river going out of map

            int riverMinX = riverPositions[i][0] - (riverWidth - 1)/2;   // 1:0 , 2:0 , 3:1 , 4:1 , 5:2 , 6:2
            int riverMaxX = riverPositions[i][0] + riverWidth/2;         // 1:0 , 2:1 , 3:1 , 4:2 , 5:2 , 6:3
            riverPositions[i][1] = map[0].length - 1;

            int deviation = 0;

            for(int n = riverPositions[i][1]; n >= 0; n--){
                for(int m = riverMinX; m <= riverMaxX; m++){


                    map[m + deviation][n].setVegetationDensity(0);
                    map[m + deviation][n].setHumidityPercentage(100);
                    map[m + deviation][n].setSoilType(3);


                }
                if(riverWidth == 1){
                    deviation += rand.nextInt(riverWidth + 2) - riverWidth;
                }
                else{
                    deviation += rand.nextInt(riverWidth + 1) - riverWidth/2;
                }

                //System.out.println(deviation);

            }

        }

            return map;
    }

    public static int calculateDist(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
