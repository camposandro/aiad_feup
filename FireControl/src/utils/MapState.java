package utils;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import agents.MapCell;
import launchers.SimulationLauncher;

public class MapState {
    private MapCell[][] grid;

    private int envWidth;
    private int envHeight;

    private HashSet<MapCell> fireCell = new HashSet<>();

    public MapState(SimulationLauncher launcher, int envWidth, int envHeight) {
        this.envWidth = envWidth;
        this.envHeight = envHeight;
        this.grid = createMapState(launcher,envWidth,envHeight);
    }

    public MapCell[][] getGrid() {
        return grid;
    }

    public MapCell getGridPos(int x, int y) {
        if(x < 0 || y < 0 || x >= envWidth || y >= envHeight)
            return null;
        return grid[x][y];
    }

    public void setGrid(MapCell[][] grid) {
        this.grid = grid;
    }

    public HashSet<MapCell> getFirecells() {
        return fireCell;
    }

    public MapCell[][] createMapState(SimulationLauncher launcher, int width, int length) {
        MapCell[][] mapCell = new MapCell[width][length];
        Random rand = new Random();

        int fireX = ThreadLocalRandom.current().nextInt(0, width);
        int fireY = ThreadLocalRandom.current().nextInt(0, length);

        int randVeg = rand.nextInt(99) + 1;
        int randHum = rand.nextInt(99);

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
                        vegMin = 1;
                        vegMax = mapCell[i-1][j].getVegetationDensity() + range;
                    }
                    else{
                        vegMin = mapCell[i-1][j].getVegetationDensity() - range;
                        vegMax = mapCell[i-1][j].getVegetationDensity() + range;
                    }

                    if(mapCell[i-1][j].getHumidityPercentage() + range > 100){
                        humMax = 99;
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
                    if(newVeg == 0)
                        newVeg = 1;

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
        mapCell = generateHousesAndRoads(mapCell);

        //Generate Roads
        //Generate Asphalt Roads
        //Generate Dirt Roads

        // Generate Fire cell
        fireCell.add(mapCell[fireX][fireY]);

        return mapCell;
    }

    public MapCell[][] generateWaterBodies(MapCell[][] map){
        map = generateLakes(map);
        map = generateRivers(map);
        return map;
    }

    public MapCell[][] generateLakes(MapCell[][] map){
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

    public MapCell[][] generateRivers(MapCell[][] map){
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
            int riverOrientation = rand.nextInt(3);

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

    public MapCell[][] generateHousesAndRoads(MapCell[][] map){
        map = generateVillages(map);
        map = generateRemoteHouses(map);
        return map;
    }

    public MapCell[][] generateVillages(MapCell[][] map){
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

    public MapCell[][] generateRemoteHouses(MapCell[][] map){
        Random rand = new Random();
        int numberOfRemoteHouses = (int)Math.cbrt(rand.nextInt(map.length * map[0].length / 10000)) + 1;

        int[] roadConnection = new int[2];
        int dirtRoadLength = rand.nextInt(15) + 5;
        int dirtRoadDirection = rand.nextInt(4); // 0:Down / 1:Left / 2:Up / 3:Right
        boolean validRoadDirection = false;
        int houseX, houseY;

        for(int i = 0; i < numberOfRemoteHouses; i++) {
            roadConnection = getDirtRoadConnection(map);

            dirtRoadLength = rand.nextInt(15) + 5;
            validRoadDirection = false;

            if (roadConnection[0] == 0 && roadConnection[1] == 0) { //There are no asphalt roads
                houseX = rand.nextInt(map.length);
                houseY = rand.nextInt(map[0].length);

                while(map[houseX][houseY].getSoilType() != 0){
                    houseX = rand.nextInt(map.length);
                    houseY = rand.nextInt(map[0].length);
                }


                map[houseX][houseY].setVegetationDensity(0);
                map[houseX][houseY].setHumidityPercentage(100);
                map[houseX][houseY].setSoilType(4);

                map = generateDirtRoad(map, houseX, houseY);
            }
            else {
                while(!validRoadDirection){
                    dirtRoadDirection = rand.nextInt(4);

                    if(dirtRoadDirection == 0 && !nextToRoad(map, roadConnection[0], roadConnection[1] - 2)){
                        validRoadDirection = true;
                    }
                    else if(dirtRoadDirection == 1 && !nextToRoad(map, roadConnection[0] - 2, roadConnection[1])){
                        validRoadDirection = true;
                    }
                    else if(dirtRoadDirection == 2 && !nextToRoad(map, roadConnection[0], roadConnection[1] + 2)){
                        validRoadDirection = true;
                    }
                    else if(dirtRoadDirection == 3 && !nextToRoad(map, roadConnection[0] + 2, roadConnection[1])){
                        validRoadDirection = true;
                    }
                }

                for (int j = 1; j <= dirtRoadLength; j++) {
                    if (dirtRoadDirection == 0 && map[roadConnection[0]][roadConnection[1] - j].getSoilType() == 0) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0]][roadConnection[1] - j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] - j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] - j].setSoilType(4);
                        }
                        else{
                            map[roadConnection[0]][roadConnection[1] - j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] - j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] - j].setSoilType(2);
                        }
                    }
                    else if (dirtRoadDirection == 1 && map[roadConnection[0] - j][roadConnection[1]].getSoilType() == 0) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0] - j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] - j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] - j][roadConnection[1]].setSoilType(4);
                        }
                        else{
                            map[roadConnection[0] - j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] - j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] - j][roadConnection[1]].setSoilType(2);
                        }

                    }
                    else if (dirtRoadDirection == 2 && map[roadConnection[0]][roadConnection[1] + j].getSoilType() == 0) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0]][roadConnection[1] + j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] + j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] + j].setSoilType(4);
                        }
                        else{
                            map[roadConnection[0]][roadConnection[1] + j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] + j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] + j].setSoilType(2);
                        }
                    }
                    else if (dirtRoadDirection == 3 && map[roadConnection[0] + j][roadConnection[1]].getSoilType() == 0) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0] + j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] + j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] + j][roadConnection[1]].setSoilType(4);
                        }
                        else{
                            map[roadConnection[0] + j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] + j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] + j][roadConnection[1]].setSoilType(2);
                        }
                    }

                }
            }
        }



        return map;
    }

    public MapCell[][] generateCentralSquares(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
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
   
    public MapCell[][] generateSurroundingStreets(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
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

    public MapCell[][] generateSurroundingHouses(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
        int numOfVillages = villagePositions.length;
        Random rand = new Random();
        int numberOfHouses;

        int[] villageUpperLeft = new int[2];
        int[] villageDownerRight = new int[2];

        for(int i = 0; i < numOfVillages; i++){
            villageUpperLeft[0] = villagePositions[i][0] - (housesPerVillage[i]/2);
            villageUpperLeft[1] = villagePositions[i][1] - (housesPerVillage[i]/2);

            villageDownerRight[0] = villagePositions[i][0] + housesPerVillage[i]/2;
            villageDownerRight[1] = villagePositions[i][1] + housesPerVillage[i]/2;

            numberOfHouses = 0;

            while(numberOfHouses < housesPerVillage[i]) {
                for (int n = villageUpperLeft[0] + 1; n < villageDownerRight[0] - 1; n++) {
                    for (int m = villageUpperLeft[1] + 1; m < villageDownerRight[1] - 1; m++) {
                        if (housesPerVillage[i] >= numberOfHouses && map[n][m].getSoilType() == 0 && nextToRoad(map, n, m) && rand.nextInt(10) == 0) {
                            map[n][m].setVegetationDensity(0);
                            map[n][m].setHumidityPercentage(100);
                            map[n][m].setSoilType(4);

                            numberOfHouses++;
                        }
                    }
                }
            }



        }
        return map;
    }

    public MapCell[][] generateMainRoads(MapCell[][] map, int[][] villagePositions, int[] housesPerVillage){
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

            mainRoadConnection[0] = 0;
            mainRoadConnection[1] = 0;

            while(mainRoadConnection[0] == 0 && mainRoadConnection[1] == 0){
                for(int n = villageUpperLeft[0] - 1; n < villageDownerRight[0]; n++){
                    for(int m = villageUpperLeft[1] - 1; m < villageDownerRight[1]; m++){
                        if(map[n][m].getSoilType() == 1 && ((n <= villagePositions[i][0] - mainRoadMinimumDistanceToCentralSquareX && m >= villagePositions[i][1] + mainRoadMinimumDistanceToCentralSquareY) || (n >= villagePositions[i][0] + mainRoadMinimumDistanceToCentralSquareX && m >= villagePositions[i][1] + mainRoadMinimumDistanceToCentralSquareY) || (m <= villagePositions[i][1] - mainRoadMinimumDistanceToCentralSquareY && n <= villagePositions[i][0] - mainRoadMinimumDistanceToCentralSquareX) || (m <= villagePositions[i][1] - mainRoadMinimumDistanceToCentralSquareY && n >= villagePositions[i][0] + mainRoadMinimumDistanceToCentralSquareX)) && rand.nextInt(housesPerVillage[i]) == 0){
                            mainRoadConnection[0] = n;
                            mainRoadConnection[1] = m;
                        }
                    }
                }
            }

            map = generateAsphaltRoad(map, mainRoadConnection);
        }

        return map;
    }

    public MapCell[][] generateAsphaltRoad(MapCell[][] map, int[] mainRoadConnection){
        Random rand = new Random();
        boolean roadGeneralDirection = rand.nextBoolean();
        boolean currDirectionVertical = false;
        int nextStraightLength = 0;
        int x = mainRoadConnection[0], y = mainRoadConnection[1];

        if(roadGeneralDirection) {
            //North/West
            while (x > 0 && y > 0) {
                if (nextStraightLength == 0) {
                    currDirectionVertical = rand.nextBoolean();
                    nextStraightLength = rand.nextInt((int) Math.sqrt((map.length + map[0].length) / 2) - 5) + 5;
                }

                if (currDirectionVertical) {
                    y--;
                } else {
                    x--;
                }

                map[x][y].setVegetationDensity(0);
                map[x][y].setHumidityPercentage(100);
                map[x][y].setSoilType(1);

                nextStraightLength--;
            }

            x = mainRoadConnection[0];
            y = mainRoadConnection[1];

            //South/East
            while (x < map.length - 1 && y < map[0].length - 1) {
                if (nextStraightLength == 0) {
                    currDirectionVertical = rand.nextBoolean();
                    nextStraightLength = rand.nextInt((int) Math.sqrt((map.length + map[0].length) / 2) - 5) + 5;
                }

                if (currDirectionVertical) {
                    y++;
                } else {
                    x++;
                }

                map[x][y].setVegetationDensity(0);
                map[x][y].setHumidityPercentage(100);
                map[x][y].setSoilType(1);

                nextStraightLength--;
            }
        }
        else{
            //North/East
            while (x < map.length - 1 && y > 0) {
                if (nextStraightLength == 0) {
                    currDirectionVertical = rand.nextBoolean();
                    nextStraightLength = rand.nextInt((int) Math.sqrt((map.length + map[0].length) / 2) - 5) + 5;
                }

                if (currDirectionVertical) {
                    y--;
                } else {
                    x++;
                }

                map[x][y].setVegetationDensity(0);
                map[x][y].setHumidityPercentage(100);
                map[x][y].setSoilType(1);

                nextStraightLength--;
            }

            x = mainRoadConnection[0];
            y = mainRoadConnection[1];

            //South/West
            while (x > 0 && y < map[0].length - 1) {
                if (nextStraightLength == 0) {
                    currDirectionVertical = rand.nextBoolean();
                    nextStraightLength = rand.nextInt((int) Math.sqrt((map.length + map[0].length) / 2) - 5) + 5;
                }

                if (currDirectionVertical) {
                    y++;
                } else {
                    x--;
                }

                map[x][y].setVegetationDensity(0);
                map[x][y].setHumidityPercentage(100);
                map[x][y].setSoilType(1);

                nextStraightLength--;
            }
        }

        return map;
    }

    public MapCell[][] generateDirtRoad(MapCell[][] map, int houseX, int houseY){
        int x=houseX, y=houseY;

        if(houseX < map.length/2){//left side
            if(houseY < houseX){//up
                y--;
                while(y >= 0){
                    if(map[houseX][y].getSoilType() == 0  && (map[0].length - houseY) > houseY ){
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(2);
                    }
                    y--;
                }
            }
            else if((map[0].length - houseY) < houseX){//down
                y++;
                while(y < map[0].length){
                    if(map[houseX][y].getSoilType() == 0) {
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(2);
                    }
                    y++;
                }
            }
            else{//left
                x--;
                while(x >= 0){
                    if(map[x][houseY].getSoilType() == 0){
                        map[x][houseY].setVegetationDensity(0);
                        map[x][houseY].setHumidityPercentage(100);
                        map[x][houseY].setSoilType(2);
                    }
                    x--;
                }
            }
        }
        else{//right side
            if(houseY < (map.length - houseX) && (map[0].length - houseY) > houseY ){//up
                y--;
                while(y >= 0){
                    if(map[houseX][y].getSoilType() == 0){
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(2);
                    }
                    y--;
                }
            }
            else if((map[0].length - houseY) < (map.length - houseX)){//down
                y++;
                while(y < map[0].length){
                    if(map[houseX][y].getSoilType() == 0){
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(2);
                    }
                    y++;
                }
            }
            else{//right
                x++;
                while(x < map.length){
                    if(map[x][houseY].getSoilType() == 0){
                        map[x][houseY].setVegetationDensity(0);
                        map[x][houseY].setHumidityPercentage(100);
                        map[x][houseY].setSoilType(2);
                    }
                    x++;
                }
            }
        }

        return map;
    }

    public int[] getDirtRoadConnection(MapCell[][] map){
        Random rand = new Random();
        int[] roadConnection = new int[2];

        for(int i = 20; i < map.length - 20; i++){
            for(int j = 20; j < map[0].length - 20; j++){
                if(map[i][j].getSoilType() == 1 && rand.nextInt(map.length * map[0].length / 500) == 0){
                    roadConnection[0] = i;
                    roadConnection[1] = j;
                }
            }
        }

        return roadConnection;
    }

    public boolean nextToRoad(MapCell[][] map, int x, int y){
        if(map[x-1][y].getSoilType() == 1  || map[x+1][y].getSoilType() == 1 || map[x][y-1].getSoilType() == 1 || map[x][y+1].getSoilType() == 1){
            return true;
        }

        return false;
    }

    public int calculateDist(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
