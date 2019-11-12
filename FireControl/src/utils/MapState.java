package utils;

import java.util.Random;
import agents.MapCell;
import launchers.SimulationLauncher;

public class MapState {

    public static MapCell[][] createMapState(SimulationLauncher launcher, int width, int length, int fireX, int fireY) {
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
                        mapCell[i][j] = new MapCell(launcher,i, j, randVeg, randHum, 0, false);
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


                    mapCell[i][j] = new MapCell(launcher, i, j, newVeg, newHum, 0, false);
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
                    boolean onFire = false;
                    if(i == fireX && j == fireY)
                        onFire = true;

                    mapCell[i][j] = new MapCell(launcher, i, j, newVeg, newHum, 0, onFire);
                }
            }
        }

        //Generate Water bodies
        mapCell = generateWaterBodies(mapCell);

        //Generate Houses
        mapCell = generateHouses(mapCell);

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
        int numOfLakes = 0;
        if(map.length * map[0].length > 5000)
            numOfLakes = (int)Math.sqrt(rand.nextInt(map.length * map[0].length / 5000));

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
                        if(m >= 0 && n >= 0 && m < map.length && n < map[0].length){
                            map[m][n].setVegetationDensity(0);
                            map[m][n].setHumidityPercentage(100);
                            map[m][n].setSoilType(3);
                        }
                    }
                    else{
                        int r = rand.nextInt(lakeRadius);
                        if(calculateDist(lakePositions[i][0], lakePositions[i][1], m, n) < r && m >= 0 && n >= 0 && m < map.length && n < map[0].length){
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
        int numOfRivers = 0;
        if(map.length * map[0].length > 10000)
            numOfRivers = (int)Math.cbrt(rand.nextInt(map.length * map[0].length / 10000));
        int riverMaximumWidth = 6;

        int[][] riverPositions = new int[numOfRivers][2];

        for(int i = 0; i < numOfRivers; i++){
            int riverX = rand.nextInt(map.length);
            int riverY = rand.nextInt(map[0].length);

            riverPositions[i][0] = riverX;
            riverPositions[i][1] = riverY;
        }

        for(int i = 0; i < numOfRivers; i++) {
            int riverWidth = rand.nextInt(riverMaximumWidth) + 1;

            //river position
            map[riverPositions[i][0]][riverPositions[i][1]].setVegetationDensity(0);
            map[riverPositions[i][0]][riverPositions[i][1]].setHumidityPercentage(100);
            map[riverPositions[i][0]][riverPositions[i][1]].setSoilType(3);

            //river generation
            int riverMinX = riverPositions[i][0] - (riverWidth - 1)/2;
            int riverMaxX = riverPositions[i][0] + riverWidth/2;
            riverPositions[i][1] = map[0].length - 1;

            int deviation = 0;
            int riverOrientation = 1;//rand.nextInt(3);

            for(int n = riverPositions[i][1]; n >= 0; n--){
                for(int m = riverMinX; m <= riverMaxX; m++){
                    if(m + deviation >= 0 && n >= 0 && m + deviation < map.length && n < map[0].length){
                        map[m + deviation][n].setVegetationDensity(0);
                        map[m + deviation][n].setHumidityPercentage(100);
                        map[m + deviation][n].setSoilType(3);
                    }

                }
                if(riverWidth == 1){
                    deviation += rand.nextInt(riverWidth + 2) - riverWidth;
                }
                else{
                    if(riverOrientation == 0){// NW/SE
                        deviation += rand.nextInt(riverWidth + 1) - riverWidth/2 - 1;
                    }
                    else if(riverOrientation == 2){// NE/SW
                        deviation += rand.nextInt(riverWidth + 1) - riverWidth/2 + 1;
                    }
                    else{
                        deviation += rand.nextInt(riverWidth + 1) - riverWidth/2;
                    }
                }
            }
        }

        return map;
    }

    public static MapCell[][] generateHouses(MapCell[][] map){
        map = generateVillages(map);
        map = generateRemoteHouses(map);

        return map;
    }
    public static MapCell[][] generateVillages(MapCell[][] map){
        Random rand = new Random();
        int numOfVillages = 0;
        if(map.length * map[0].length > 5000)
            numOfVillages = (int)Math.cbrt(rand.nextInt(map.length * map[0].length / 5000));

        int maxHousesPerVillage = 32;
        int[] housesPerVillage = new int[numOfVillages];

        int[][] villagePositions = new int[numOfVillages][2];

        for(int i = 0; i < numOfVillages; i++){
            int villageX = rand.nextInt(map.length - maxHousesPerVillage) + maxHousesPerVillage/2;
            int villageY = rand.nextInt(map[0].length - maxHousesPerVillage) + maxHousesPerVillage/2;

            villagePositions[i][0] = villageX;
            villagePositions[i][1] = villageY;

            if(map[villagePositions[i][0]][villagePositions[i][1]].getSoilType() != 0){ //Ensure center of village isn't in water
                i--;
            }
            else{
                housesPerVillage[i] = rand.nextInt(maxHousesPerVillage - 8) + 8;
            }
        }

        //generate central squares
        map = generateCentralSquares(map, villagePositions, housesPerVillage);

        map = generateSurroundingStreets(map, villagePositions, housesPerVillage);
        map = generateSurroundingHouses(map, villagePositions, housesPerVillage);



        return map;
    }

    public static MapCell[][] generateRemoteHouses(MapCell[][] map){
        //TO DO

        return map;
    }

    public static MapCell[][] generateCentralSquares(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
        int numOfVillages = villagePositions.length;

        for(int i = 0; i < numOfVillages; i++){
            map[villagePositions[i][0]][villagePositions[i][1]].setVegetationDensity(0);
            map[villagePositions[i][0]][villagePositions[i][1]].setHumidityPercentage(100);
            map[villagePositions[i][0]][villagePositions[i][1]].setSoilType(5);

            if(housesPerVillage[i] >= 15){ // 2x1 rectangle
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setSoilType(5);
            }
            if(housesPerVillage[i] >= 20){ // 2x2 square
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setSoilType(5);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setSoilType(5);
            }
            if(housesPerVillage[i] >= 25){ // 3x2 rectangle
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(5);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(5);
            }
            if(housesPerVillage[i] >= 30){ // 3x3 square
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(5);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(5);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(5);
            }

            //generate street around central square
            if(housesPerVillage[i] < 15){
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(1);
            }
            else if(housesPerVillage[i] >= 15 && housesPerVillage[i] < 20){ // 2x1 rectangle
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(1);

            }
            else if(housesPerVillage[i] >= 20 && housesPerVillage[i] < 25){ // 2x2 square
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(1);
            }
            else if(housesPerVillage[i] >= 25 && housesPerVillage[i] < 30){ // 3x2 rectangle
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(1);
            }
            else if(housesPerVillage[i] >= 30){ // 3x3 square
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setSoilType(1);

                map[villagePositions[i][0] - 2][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 2].setSoilType(1);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 2].setSoilType(1);

                map[villagePositions[i][0]][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 2].setSoilType(1);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 2].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setSoilType(1);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(1);
            }

        }

        return map;
    }


    public static MapCell[][] generateSurroundingStreets(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
        int numOfVillages = villagePositions.length;
        Random rand = new Random();

        int[] upperLeftCentralSquare = new int[2];
        int[] upperRightCentralSquare = new int[2];
        int[] downerLeftCentralSquare = new int[2];
        int[] downerRightCentralSquare = new int[2];

        int streetLength;
        int streetOrientation;

        for(int i = 0; i < numOfVillages; i++){
            if(housesPerVillage[i] < 15){
                upperLeftCentralSquare[0] = villagePositions[i][0] - 1;
                upperLeftCentralSquare[1] = villagePositions[i][1] - 1;

                upperRightCentralSquare[0] = villagePositions[i][0] + 1;
                upperRightCentralSquare[1] = villagePositions[i][1] - 1;

                downerLeftCentralSquare[0] = villagePositions[i][0] - 1;
                downerLeftCentralSquare[1] = villagePositions[i][1] + 1;

                downerRightCentralSquare[0] = villagePositions[i][0] + 1;
                downerRightCentralSquare[1] = villagePositions[i][1] + 1;
            }
            else if(housesPerVillage[i] >= 15 && housesPerVillage[i] < 20){
                upperLeftCentralSquare[0] = villagePositions[i][0] - 1;
                upperLeftCentralSquare[1] = villagePositions[i][1] - 1;

                upperRightCentralSquare[0] = villagePositions[i][0] + 2;
                upperRightCentralSquare[1] = villagePositions[i][1] - 1;

                downerLeftCentralSquare[0] = villagePositions[i][0] - 1;
                downerLeftCentralSquare[1] = villagePositions[i][1] + 1;

                downerRightCentralSquare[0] = villagePositions[i][0] + 2;
                downerRightCentralSquare[1] = villagePositions[i][1] + 1;
            }
            else if(housesPerVillage[i] >= 20 && housesPerVillage[i] < 25){
                upperLeftCentralSquare[0] = villagePositions[i][0] - 1;
                upperLeftCentralSquare[1] = villagePositions[i][1] - 1;

                upperRightCentralSquare[0] = villagePositions[i][0] + 2;
                upperRightCentralSquare[1] = villagePositions[i][1] - 1;

                downerLeftCentralSquare[0] = villagePositions[i][0] - 1;
                downerLeftCentralSquare[1] = villagePositions[i][1] + 2;

                downerRightCentralSquare[0] = villagePositions[i][0] + 2;
                downerRightCentralSquare[1] = villagePositions[i][1] + 2;
            }
            else if(housesPerVillage[i] >= 25 && housesPerVillage[i] < 30){
                upperLeftCentralSquare[0] = villagePositions[i][0] - 2;
                upperLeftCentralSquare[1] = villagePositions[i][1] - 1;

                upperRightCentralSquare[0] = villagePositions[i][0] + 2;
                upperRightCentralSquare[1] = villagePositions[i][1] - 1;

                downerLeftCentralSquare[0] = villagePositions[i][0] - 2;
                downerLeftCentralSquare[1] = villagePositions[i][1] + 2;

                downerRightCentralSquare[0] = villagePositions[i][0] + 2;
                downerRightCentralSquare[1] = villagePositions[i][1] + 2;
            }
            else{
                upperLeftCentralSquare[0] = villagePositions[i][0] - 2;
                upperLeftCentralSquare[1] = villagePositions[i][1] - 2;

                upperRightCentralSquare[0] = villagePositions[i][0] + 2;
                upperRightCentralSquare[1] = villagePositions[i][1] - 2;

                downerLeftCentralSquare[0] = villagePositions[i][0] - 2;
                downerLeftCentralSquare[1] = villagePositions[i][1] + 2;

                downerRightCentralSquare[0] = villagePositions[i][0] + 2;
                downerRightCentralSquare[1] = villagePositions[i][1] + 2;
            }

            for(int j = 0; j < 4; j++){
                streetLength = rand.nextInt(housesPerVillage[i]/2);
                streetOrientation = rand.nextInt(2);

                if(j == 0){ //Upper Left
                    for(int n = 0; n < streetLength; n++){
                        if(streetOrientation == 0){//Vertical
                            map[upperLeftCentralSquare[0]][upperLeftCentralSquare[1] - (n + 1)].setVegetationDensity(0);
                            map[upperLeftCentralSquare[0]][upperLeftCentralSquare[1] - (n + 1)].setHumidityPercentage(100);
                            map[upperLeftCentralSquare[0]][upperLeftCentralSquare[1] - (n + 1)].setSoilType(1);
                        }
                        else{//Horizontal
                            map[upperLeftCentralSquare[0] - (n + 1)][upperLeftCentralSquare[1]].setVegetationDensity(0);
                            map[upperLeftCentralSquare[0] - (n + 1)][upperLeftCentralSquare[1]].setHumidityPercentage(100);
                            map[upperLeftCentralSquare[0] - (n + 1)][upperLeftCentralSquare[1]].setSoilType(1);
                        }
                    }
                }
                else if(j == 1){//Upper Right
                    for(int n = 0; n < streetLength; n++){
                        if(streetOrientation == 0){//Vertical
                            map[upperRightCentralSquare[0]][upperRightCentralSquare[1] - (n + 1)].setVegetationDensity(0);
                            map[upperRightCentralSquare[0]][upperRightCentralSquare[1] - (n + 1)].setHumidityPercentage(100);
                            map[upperRightCentralSquare[0]][upperRightCentralSquare[1] - (n + 1)].setSoilType(1);
                        }
                        else{//Horizontal
                            map[upperRightCentralSquare[0] + (n + 1)][upperRightCentralSquare[1]].setVegetationDensity(0);
                            map[upperRightCentralSquare[0] + (n + 1)][upperRightCentralSquare[1]].setHumidityPercentage(100);
                            map[upperRightCentralSquare[0] + (n + 1)][upperRightCentralSquare[1]].setSoilType(1);
                        }
                    }
                }
                else if(j == 2){//Downer Left
                    for(int n = 0; n < streetLength; n++){
                        if(streetOrientation == 0){//Vertical
                            map[downerLeftCentralSquare[0]][downerLeftCentralSquare[1] + (n + 1)].setVegetationDensity(0);
                            map[downerLeftCentralSquare[0]][downerLeftCentralSquare[1] + (n + 1)].setHumidityPercentage(100);
                            map[downerLeftCentralSquare[0]][downerLeftCentralSquare[1] + (n + 1)].setSoilType(1);
                        }
                        else{//Horizontal
                            map[downerLeftCentralSquare[0] - (n + 1)][downerLeftCentralSquare[1]].setVegetationDensity(0);
                            map[downerLeftCentralSquare[0] - (n + 1)][downerLeftCentralSquare[1]].setHumidityPercentage(100);
                            map[downerLeftCentralSquare[0] - (n + 1)][downerLeftCentralSquare[1]].setSoilType(1);
                        }
                    }
                }
                else{//Downer Right
                    for(int n = 0; n < streetLength; n++){
                        if(streetOrientation == 0){//Vertical
                            map[downerRightCentralSquare[0]][downerRightCentralSquare[1] + (n + 1)].setVegetationDensity(0);
                            map[downerRightCentralSquare[0]][downerRightCentralSquare[1] + (n + 1)].setHumidityPercentage(100);
                            map[downerRightCentralSquare[0]][downerRightCentralSquare[1] + (n + 1)].setSoilType(1);
                        }
                        else{//Horizontal
                            map[downerRightCentralSquare[0] + (n + 1)][downerRightCentralSquare[1]].setVegetationDensity(0);
                            map[downerRightCentralSquare[0] + (n + 1)][downerRightCentralSquare[1]].setHumidityPercentage(100);
                            map[downerRightCentralSquare[0] + (n + 1)][downerRightCentralSquare[1]].setSoilType(1);
                        }
                    }
                }
            }
        }

        map = generateMainRoads(map, villagePositions, housesPerVillage);

        return map;
    }

    public static MapCell[][] generateSurroundingHouses(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
        //TO DO

        return map;
    }

    public static MapCell[][] generateMainRoads(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
        int numOfVillages = villagePositions.length;
        Random rand = new Random();

        int[] villageUpperLeft = new int[2];
        int[] villageDownerRight = new int[2];
        int[] mainRoadConnection = new int[2];
        int mainRoadMinimumDistanceToCentralSquareX = 1, mainRoadMinimumDistanceToCentralSquareY = 1;

        for(int i = 0; i < numOfVillages; i++){
            villageUpperLeft[0] = villagePositions[i][0] - housesPerVillage[i]/2 + 1;
            villageUpperLeft[1] = villagePositions[i][1] - housesPerVillage[i]/2 + 1;

            villageDownerRight[0] = villagePositions[i][0] + housesPerVillage[i]/2 + 1;
            villageDownerRight[1] = villagePositions[i][1] + housesPerVillage[i]/2 + 1;

            if(housesPerVillage[i] < 15){
                mainRoadMinimumDistanceToCentralSquareX = 1;
                mainRoadMinimumDistanceToCentralSquareY = 1;
            }
            else if(housesPerVillage[i] >= 15 && housesPerVillage[i] < 20){
                mainRoadMinimumDistanceToCentralSquareX = 2;
                mainRoadMinimumDistanceToCentralSquareY = 1;
            }
            else{
                mainRoadMinimumDistanceToCentralSquareX = 2;
                mainRoadMinimumDistanceToCentralSquareY = 2;
            }

            while(mainRoadConnection[0] == 0 && mainRoadConnection[1] == 0){
                for(int n = villageUpperLeft[0] - 1; n < villageDownerRight[0]; n++){
                    for(int m = villageUpperLeft[1] - 1; m < villageDownerRight[1]; m++){
                        if(((n <= villagePositions[i][0] - mainRoadMinimumDistanceToCentralSquareX && m >= villagePositions[i][1] + mainRoadMinimumDistanceToCentralSquareY) || (n >= villagePositions[i][0] + mainRoadMinimumDistanceToCentralSquareX && m >= villagePositions[i][1] + mainRoadMinimumDistanceToCentralSquareY) || (m <= villagePositions[i][1] - mainRoadMinimumDistanceToCentralSquareY && n <= villagePositions[i][0] - mainRoadMinimumDistanceToCentralSquareX) || (m <= villagePositions[i][1] - mainRoadMinimumDistanceToCentralSquareY && n >= villagePositions[i][0] + mainRoadMinimumDistanceToCentralSquareX)) && rand.nextInt(housesPerVillage[i]) == 0){
                            mainRoadConnection[0] = n;
                            mainRoadConnection[1] = m;
                        }
                    }
                }
            }

            System.out.println("Main Road Connection = " + mainRoadConnection[0] + " : " + mainRoadConnection[1]);

            map = generateAsphaltRoad(map, mainRoadConnection);


        }

        return map;
    }

    public static MapCell[][] generateAsphaltRoad(MapCell[][] map, int[] mainRoadConnection){


        return map;
    }


    public static int calculateDist(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
