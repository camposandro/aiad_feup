package utils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import agents.Firefighter;
import agents.MapCell;
import launchers.SimulationLauncher;

public class MapState {
    private static MapCell[][] grid;

    public static int envWidth;
    public static int envHeight;

    private static List<MapCell> fires = new ArrayList<>();

    private static HashSet<MapCell> fireCell = new HashSet<>();
    private static HashSet<MapCell> waterCell = new HashSet<>();

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public MapState(SimulationLauncher launcher, int envWidth, int envHeight) {
        this.envWidth = envWidth;
        this.envHeight = envHeight;

        // Generate fires
        Random rand = new Random();
        double numFires = launcher.getNumFires();
        for (int i = 0; i < numFires; i++) {
            MapCell newFireCell;
            do {
                int fireX = rand.nextInt(envWidth);
                int fireY = rand.nextInt(envHeight);
                newFireCell = new MapCell(fireX,fireY);
            } while (fires.contains(newFireCell));
            fires.add(newFireCell);
        }

        this.grid = createMapState(launcher,envWidth,envHeight);

        // Schedule world update
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HashSet<MapCell> fireCells = MapState.getFireCells();
                HashSet<MapCell> a = (HashSet) fireCells.clone();
                Iterator<MapCell> i = a.iterator();
                while (i.hasNext())
                    i.next().update();
            }
        }, 0, SimulationLauncher.WORLD_UPDATE_RATE, TimeUnit.MILLISECONDS);
    }

    public MapCell[][] getGrid() {
        return grid;
    }

    public static MapCell getGridPos(int x, int y) {
        if(x < 0 || y < 0 || x >= envWidth || y >= envHeight)
            return null;
        return grid[x][y];
    }

    public void setGrid(MapCell[][] grid) {
        this.grid = grid;
    }

    public static List<MapCell> getFires() {
        return fires;
    }

    public static HashSet<MapCell> getFireCells() {
        return fireCell;
    }

    public static HashSet<MapCell> getWaterCells() {
        return waterCell;
    }

    public MapCell[][] createMapState(SimulationLauncher launcher, int width, int length) {
        MapCell[][] mapCell = new MapCell[width][length];
        Random rand = new Random();

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
                        mapCell[i][j] = new MapCell(i, j, randVeg, randHum, MapCell.SoilType.VEGETATION);
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

                    mapCell[i][j] = new MapCell(i, j, newVeg, newHum, MapCell.SoilType.VEGETATION);
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

                    mapCell[i][j] = new MapCell(i, j, newVeg, newHum, MapCell.SoilType.VEGETATION);
                }
            }
        }

        //Generate Water bodies
        mapCell = generateWaterBodies(mapCell);
        for (int i = 0; i < envWidth; i++) {
            for (int j = 0; j < envHeight; j++) {
                MapCell cell = mapCell[i][j];
                if (cell.getSoilType() == MapCell.SoilType.WATER) {
                    waterCell.add(cell);
                }
            }
        }

        //Generate Houses
        mapCell = generateHousesAndRoads(mapCell);

        //Generate Roads
        //Generate Asphalt Roads
        //Generate Dirt Roads

        // Generate Fire cells
        for (int i = 0; i < fires.size(); i++) {
            MapCell f = fires.get(i);
            MapCell cell = mapCell[f.getX()][f.getY()];
            cell.setOnFire(true);
            fireCell.add(cell);
        };

        return mapCell;
    }

    public MapCell[][] generateWaterBodies(MapCell[][] map){
        map = generateLakes(map);
        map = generateRivers(map);
        return map;
    }

    public MapCell[][] generateLakes(MapCell[][] map){
        Random rand = new Random();
        int numOfLakes = SimulationLauncher.NUM_LAKES;
        if(SimulationLauncher.RANDOMWORLD && map.length * map[0].length > 5000)
            numOfLakes = (int)Math.sqrt(rand.nextInt(map.length * map[0].length / 5000));

        int lakeMaximumRadius = SimulationLauncher.LAKE_MAX_RADIUS;

        int[][] lakePositions = new int[numOfLakes][2];

        for(int i = 0; i < numOfLakes; i++){
            int lakeX = rand.nextInt(map.length);
            int lakeY = rand.nextInt(map[0].length);

            lakePositions[i][0] = lakeX;
            lakePositions[i][1] = lakeY;
        }

        for(int i = 0; i < numOfLakes; i++){
            int lakeRadius = lakeMaximumRadius;//rand.nextInt(lakeMaximumRadius);
            int minX, maxX, minY, maxY;
            //lake center
            map[lakePositions[i][0]][lakePositions[i][1]].setVegetationDensity(0);
            map[lakePositions[i][0]][lakePositions[i][1]].setHumidityPercentage(100);
            map[lakePositions[i][0]][lakePositions[i][1]].setSoilType(MapCell.SoilType.WATER);

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
                            map[m][n].setSoilType(MapCell.SoilType.WATER);
                        }
                    }
                    else{
                        int r = rand.nextInt(lakeRadius);
                        if(calculateDist(lakePositions[i][0], lakePositions[i][1], m, n) < r && m >= 0 && n >= 0 && m < map.length && n < map[0].length){
                            map[m][n].setVegetationDensity(0);
                            map[m][n].setHumidityPercentage(100);
                            map[m][n].setSoilType(MapCell.SoilType.WATER);
                        }
                    }
                }
            }
        }

        return map;
    }

    public MapCell[][] generateRivers(MapCell[][] map){
        Random rand = new Random();
        int numOfRivers = SimulationLauncher.NUM_RIVERS;
        if(SimulationLauncher.RANDOMWORLD && map.length * map[0].length > 10000)
            numOfRivers = (int)Math.cbrt(rand.nextInt(map.length * map[0].length / 10000));
        int riverMaximumWidth = SimulationLauncher.RIVER_MAX_WIDTH;

        int[][] riverPositions = new int[numOfRivers][2];

        for(int i = 0; i < numOfRivers; i++){
            int riverX = rand.nextInt(map.length);
            int riverY = rand.nextInt(map[0].length);

            riverPositions[i][0] = riverX;
            riverPositions[i][1] = riverY;
        }

        for(int i = 0; i < numOfRivers; i++) {
            int riverWidth = riverMaximumWidth; //rand.nextInt(riverMaximumWidth) + 1;

            //river position
            map[riverPositions[i][0]][riverPositions[i][1]].setVegetationDensity(0);
            map[riverPositions[i][0]][riverPositions[i][1]].setHumidityPercentage(100);
            map[riverPositions[i][0]][riverPositions[i][1]].setSoilType(MapCell.SoilType.WATER);

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
                        map[m + deviation][n].setSoilType(MapCell.SoilType.WATER);
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
        int numOfVillages = SimulationLauncher.NUM_VILLAGES;
        if(SimulationLauncher.RANDOMWORLD && map.length * map[0].length > 5000)
            numOfVillages = (int)Math.cbrt(rand.nextInt(map.length * map[0].length / 5000));

        int maxHousesPerVillage;
        if(SimulationLauncher.RANDOMWORLD){
            maxHousesPerVillage = 32;
        }
        else{
            maxHousesPerVillage = SimulationLauncher.VILLAGE_MAX_HOUSES;
        }

        int[] housesPerVillage = new int[numOfVillages];

        int[][] villagePositions = new int[numOfVillages][2];

        for(int i = 0; i < numOfVillages; i++){
            int villageX = rand.nextInt(map.length - maxHousesPerVillage) + maxHousesPerVillage/2;
            int villageY = rand.nextInt(map[0].length - maxHousesPerVillage) + maxHousesPerVillage/2;

            villagePositions[i][0] = villageX;
            villagePositions[i][1] = villageY;

            if(map[villagePositions[i][0]][villagePositions[i][1]].getSoilType() != MapCell.SoilType.WATER){ //Ensure center of village isn't in water
                i--;
            }
            else{
                housesPerVillage[i] = maxHousesPerVillage;//rand.nextInt(maxHousesPerVillage - 8) + 8;
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
        int numberOfRemoteHouses;

        if(SimulationLauncher.RANDOMWORLD){
            numberOfRemoteHouses = (int)Math.cbrt(rand.nextInt(map.length * map[0].length / 10000)) + 1;
        }
        else{
            numberOfRemoteHouses = SimulationLauncher.NUM_REMOTE_HOUSES;
        }

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

                while(map[houseX][houseY].getSoilType() != MapCell.SoilType.VEGETATION){
                    houseX = rand.nextInt(map.length);
                    houseY = rand.nextInt(map[0].length);
                }


                map[houseX][houseY].setVegetationDensity(0);
                map[houseX][houseY].setHumidityPercentage(100);
                map[houseX][houseY].setSoilType(MapCell.SoilType.HOUSE);

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
                    if (dirtRoadDirection == 0 && map[roadConnection[0]][roadConnection[1] - j].getSoilType() == MapCell.SoilType.VEGETATION) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0]][roadConnection[1] - j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] - j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] - j].setSoilType(MapCell.SoilType.HOUSE);
                        }
                        else{
                            map[roadConnection[0]][roadConnection[1] - j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] - j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] - j].setSoilType(MapCell.SoilType.DIRT);
                        }
                    }
                    else if (dirtRoadDirection == 1 && map[roadConnection[0] - j][roadConnection[1]].getSoilType() == MapCell.SoilType.VEGETATION) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0] - j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] - j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] - j][roadConnection[1]].setSoilType(MapCell.SoilType.HOUSE);
                        }
                        else{
                            map[roadConnection[0] - j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] - j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] - j][roadConnection[1]].setSoilType(MapCell.SoilType.DIRT);
                        }

                    }
                    else if (dirtRoadDirection == 2 && map[roadConnection[0]][roadConnection[1] + j].getSoilType() == MapCell.SoilType.VEGETATION) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0]][roadConnection[1] + j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] + j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] + j].setSoilType(MapCell.SoilType.HOUSE);
                        }
                        else{
                            map[roadConnection[0]][roadConnection[1] + j].setVegetationDensity(0);
                            map[roadConnection[0]][roadConnection[1] + j].setHumidityPercentage(100);
                            map[roadConnection[0]][roadConnection[1] + j].setSoilType(MapCell.SoilType.DIRT);
                        }
                    }
                    else if (dirtRoadDirection == 3 && map[roadConnection[0] + j][roadConnection[1]].getSoilType() == MapCell.SoilType.VEGETATION) {
                        if(j == dirtRoadLength){
                            map[roadConnection[0] + j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] + j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] + j][roadConnection[1]].setSoilType(MapCell.SoilType.HOUSE);
                        }
                        else{
                            map[roadConnection[0] + j][roadConnection[1]].setVegetationDensity(0);
                            map[roadConnection[0] + j][roadConnection[1]].setHumidityPercentage(100);
                            map[roadConnection[0] + j][roadConnection[1]].setSoilType(MapCell.SoilType.DIRT);
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
            map[villagePositions[i][0]][villagePositions[i][1]].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);

            if(housesPerVillage[i] >= 15){ // 2x1 rectangle
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);
            }
            if(housesPerVillage[i] >= 20){ // 2x2 square
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);
            }
            if(housesPerVillage[i] >= 25){ // 3x2 rectangle
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);
            }
            if(housesPerVillage[i] >= 30){ // 3x3 square
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.CENTRAL_SQUARE);
            }

            //generate street around central square
            if(housesPerVillage[i] < 15){
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);
            }
            else if(housesPerVillage[i] >= 15 && housesPerVillage[i] < 20){ // 2x1 rectangle
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

            }
            else if(housesPerVillage[i] >= 20 && housesPerVillage[i] < 25){ // 2x2 square
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);
            }
            else if(housesPerVillage[i] >= 25 && housesPerVillage[i] < 30){ // 3x2 rectangle
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);
            }
            else if(housesPerVillage[i] >= 30){ // 3x3 square
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] + 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1]].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1]].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 2][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 2][villagePositions[i][1] - 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] - 1][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] - 1][villagePositions[i][1] - 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0]][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0]][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0]][villagePositions[i][1] - 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 1][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 1][villagePositions[i][1] - 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 2].setSoilType(MapCell.SoilType.ASPHALT);

                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setVegetationDensity(0);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setHumidityPercentage(100);
                map[villagePositions[i][0] + 2][villagePositions[i][1] - 1].setSoilType(MapCell.SoilType.ASPHALT);
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
                            map[upperLeftCentralSquare[0]][upperLeftCentralSquare[1] - (n + 1)].setSoilType(MapCell.SoilType.ASPHALT);
                        }
                        else{//Horizontal
                            map[upperLeftCentralSquare[0] - (n + 1)][upperLeftCentralSquare[1]].setVegetationDensity(0);
                            map[upperLeftCentralSquare[0] - (n + 1)][upperLeftCentralSquare[1]].setHumidityPercentage(100);
                            map[upperLeftCentralSquare[0] - (n + 1)][upperLeftCentralSquare[1]].setSoilType(MapCell.SoilType.ASPHALT);
                        }
                    }
                }
                else if(j == 1){//Upper Right
                    for(int n = 0; n < streetLength; n++){
                        if(streetOrientation == 0){//Vertical
                            map[upperRightCentralSquare[0]][upperRightCentralSquare[1] - (n + 1)].setVegetationDensity(0);
                            map[upperRightCentralSquare[0]][upperRightCentralSquare[1] - (n + 1)].setHumidityPercentage(100);
                            map[upperRightCentralSquare[0]][upperRightCentralSquare[1] - (n + 1)].setSoilType(MapCell.SoilType.ASPHALT);
                        }
                        else{//Horizontal
                            map[upperRightCentralSquare[0] + (n + 1)][upperRightCentralSquare[1]].setVegetationDensity(0);
                            map[upperRightCentralSquare[0] + (n + 1)][upperRightCentralSquare[1]].setHumidityPercentage(100);
                            map[upperRightCentralSquare[0] + (n + 1)][upperRightCentralSquare[1]].setSoilType(MapCell.SoilType.ASPHALT);
                        }
                    }
                }
                else if(j == 2){//Downer Left
                    for(int n = 0; n < streetLength; n++){
                        if(streetOrientation == 0){//Vertical
                            map[downerLeftCentralSquare[0]][downerLeftCentralSquare[1] + (n + 1)].setVegetationDensity(0);
                            map[downerLeftCentralSquare[0]][downerLeftCentralSquare[1] + (n + 1)].setHumidityPercentage(100);
                            map[downerLeftCentralSquare[0]][downerLeftCentralSquare[1] + (n + 1)].setSoilType(MapCell.SoilType.ASPHALT);
                        }
                        else{//Horizontal
                            map[downerLeftCentralSquare[0] - (n + 1)][downerLeftCentralSquare[1]].setVegetationDensity(0);
                            map[downerLeftCentralSquare[0] - (n + 1)][downerLeftCentralSquare[1]].setHumidityPercentage(100);
                            map[downerLeftCentralSquare[0] - (n + 1)][downerLeftCentralSquare[1]].setSoilType(MapCell.SoilType.ASPHALT);
                        }
                    }
                }
                else{//Downer Right
                    for(int n = 0; n < streetLength; n++){
                        if(streetOrientation == 0){//Vertical
                            map[downerRightCentralSquare[0]][downerRightCentralSquare[1] + (n + 1)].setVegetationDensity(0);
                            map[downerRightCentralSquare[0]][downerRightCentralSquare[1] + (n + 1)].setHumidityPercentage(100);
                            map[downerRightCentralSquare[0]][downerRightCentralSquare[1] + (n + 1)].setSoilType(MapCell.SoilType.ASPHALT);
                        }
                        else{//Horizontal
                            map[downerRightCentralSquare[0] + (n + 1)][downerRightCentralSquare[1]].setVegetationDensity(0);
                            map[downerRightCentralSquare[0] + (n + 1)][downerRightCentralSquare[1]].setHumidityPercentage(100);
                            map[downerRightCentralSquare[0] + (n + 1)][downerRightCentralSquare[1]].setSoilType(MapCell.SoilType.ASPHALT);
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
                        if (housesPerVillage[i] >= numberOfHouses && map[n][m].getSoilType() == MapCell.SoilType.VEGETATION && nextToRoad(map, n, m) && rand.nextInt(10) == 0) {
                            map[n][m].setVegetationDensity(0);
                            map[n][m].setHumidityPercentage(100);
                            map[n][m].setSoilType(MapCell.SoilType.HOUSE);

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
                        if(map[n][m].getSoilType() == MapCell.SoilType.ASPHALT && ((n <= villagePositions[i][0] - mainRoadMinimumDistanceToCentralSquareX && m >= villagePositions[i][1] + mainRoadMinimumDistanceToCentralSquareY) || (n >= villagePositions[i][0] + mainRoadMinimumDistanceToCentralSquareX && m >= villagePositions[i][1] + mainRoadMinimumDistanceToCentralSquareY) || (m <= villagePositions[i][1] - mainRoadMinimumDistanceToCentralSquareY && n <= villagePositions[i][0] - mainRoadMinimumDistanceToCentralSquareX) || (m <= villagePositions[i][1] - mainRoadMinimumDistanceToCentralSquareY && n >= villagePositions[i][0] + mainRoadMinimumDistanceToCentralSquareX)) && rand.nextInt(housesPerVillage[i]) == 0){
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
                map[x][y].setSoilType(MapCell.SoilType.ASPHALT);

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
                map[x][y].setSoilType(MapCell.SoilType.ASPHALT);

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
                map[x][y].setSoilType(MapCell.SoilType.ASPHALT);

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
                map[x][y].setSoilType(MapCell.SoilType.ASPHALT);

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
                    if(map[houseX][y].getSoilType() == MapCell.SoilType.VEGETATION && (map[0].length - houseY) > houseY ){
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(MapCell.SoilType.DIRT);
                    }
                    y--;
                }
            }
            else if((map[0].length - houseY) < houseX){//down
                y++;
                while(y < map[0].length){
                    if(map[houseX][y].getSoilType() == MapCell.SoilType.VEGETATION) {
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(MapCell.SoilType.DIRT);
                    }
                    y++;
                }
            }
            else{//left
                x--;
                while(x >= 0){
                    if(map[x][houseY].getSoilType() == MapCell.SoilType.VEGETATION){
                        map[x][houseY].setVegetationDensity(0);
                        map[x][houseY].setHumidityPercentage(100);
                        map[x][houseY].setSoilType(MapCell.SoilType.DIRT);
                    }
                    x--;
                }
            }
        }
        else{//right side
            if(houseY < (map.length - houseX) && (map[0].length - houseY) > houseY ){//up
                y--;
                while(y >= 0){
                    if(map[houseX][y].getSoilType() == MapCell.SoilType.VEGETATION){
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(MapCell.SoilType.DIRT);
                    }
                    y--;
                }
            }
            else if((map[0].length - houseY) < (map.length - houseX)){//down
                y++;
                while(y < map[0].length){
                    if(map[houseX][y].getSoilType() == MapCell.SoilType.VEGETATION){
                        map[houseX][y].setVegetationDensity(0);
                        map[houseX][y].setHumidityPercentage(100);
                        map[houseX][y].setSoilType(MapCell.SoilType.DIRT);
                    }
                    y++;
                }
            }
            else{//right
                x++;
                while(x < map.length){
                    if(map[x][houseY].getSoilType() == MapCell.SoilType.VEGETATION){
                        map[x][houseY].setVegetationDensity(0);
                        map[x][houseY].setHumidityPercentage(100);
                        map[x][houseY].setSoilType(MapCell.SoilType.DIRT);
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
                if(map[i][j].getSoilType() == MapCell.SoilType.ASPHALT && rand.nextInt(map.length * map[0].length / 500) == 0){
                    roadConnection[0] = i;
                    roadConnection[1] = j;
                }
            }
        }

        return roadConnection;
    }

    public boolean nextToRoad(MapCell[][] map, int x, int y){
        if(map[x-1][y].getSoilType() == MapCell.SoilType.ASPHALT || map[x+1][y].getSoilType() == MapCell.SoilType.ASPHALT || map[x][y-1].getSoilType() == MapCell.SoilType.ASPHALT || map[x][y+1].getSoilType() == MapCell.SoilType.ASPHALT){
            return true;
        }

        return false;
    }

    public static int calculateDist(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static int calculateDist(MapCell c1, MapCell c2) { return Math.abs(c1.getX() - c2.getX()) + Math.abs(c1.getY() - c2.getY()); }

    public static int calculateDist(MapCell cell, Firefighter ff) {
        return Math.abs(cell.getX() - ff.getX()) + Math.abs(cell.getY() - ff.getY());
    }
}