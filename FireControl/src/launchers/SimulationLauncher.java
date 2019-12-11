package launchers;

import agents.FireStation;
import agents.Firefighter;
import agents.MyAgent;
import jade.core.AID;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import utils.MapState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationLauncher extends Repast3Launcher {
    //-------------------------------------------WORLD SELECTION--------------------------------------------//
    public static int WORLD_WIDTH = 200;        // Recommended size = 200
    public static int WORLD_HEIGHT = 100;       // Recommended size = 100

    public static boolean RANDOMWORLD = false;

    public static int NUM_FIRES = 2;
    public static int NUM_FIREFIGHTERS = 4;

    public static double NumRivers = 1;
    public static double RiverWidth = 5;
    public static double NumLakes = 1;
    public static double LakeRadius = 12;

    public static double NumVillages = 1;
    public static double TotalNumHouses = 32;

    public static int NUM_REMOTE_HOUSES = 2;

    public static int WORLD_UPDATE_RATE = 1000; // World update fixed rate
    public static int FF_UPDATE_RATE = 10;

    public static double ViewingDist = 6;
    public static double ExtinguishingDist = 2;
    public static int NUM_ROAMING_TURNS = 6;
    public static double MaxWaterCapacity = 100;
    public static int EXTINGUISH_PUMPING_VELOCITY = 1;
    public static int REFILL_PUMPING_VELOCITY = EXTINGUISH_PUMPING_VELOCITY * 3;
    //-----------------------------------------------------------------------------------------------------//
    private Random rand;

    private static int numFFEnded = 0;
    private static int totalBurnedArea = 0;
    public static boolean endSim = false;

    private ContainerController mainContainer;

    //private DisplaySurface displaySurface;
    private Object2DGrid environment;
    private int envWidth = WORLD_WIDTH;
    private int envHeight = WORLD_HEIGHT;

    private MapState mapState;

    private double NumFireFighters  = NUM_FIREFIGHTERS;
    private double NumFires = NUM_FIRES;

    private FireStation fireStation;
    private List<Firefighter> firefighters;


    public Map<AID, MyAgent> agents = new HashMap();

    public SimulationLauncher() {
        setRand(new Random());
    }

    public static void main(String[] args) throws IOException {
        boolean BATCH_MODE = true;
        SimInit init = new SimInit();
        init.setNumRuns(100); // works only in batch mode
        init.loadModel(new SimulationLauncher(), "Parameters.txt", BATCH_MODE);


    }

    public void setup() {
        super.setup();
/*
        if (displaySurface != null)
            displaySurface.dispose();
*/
        String displaySurfaceName = "Fire Control Environment";

        //setDisplaySurface(new DisplaySurface(this, displaySurfaceName));
        //registerDisplaySurface(displaySurfaceName, displaySurface);
    }

    public void begin() {
        buildModel();
        //buildDisplay();
        buildSchedule();
        super.begin();
    }
    public void setNumFireFighters(double numFireFighters) {
        System.out.println(numFireFighters);
        this.NumFireFighters = numFireFighters;
    }

    public double getNumFireFighters() {
        return NumFireFighters;
    }
    public void setNumFires(double numFires) {

        setNumRivers(ThreadLocalRandom.current().nextInt(0, 6));
        setRiverWidth(ThreadLocalRandom.current().nextInt(1, 6));
        setNumLakes(ThreadLocalRandom.current().nextInt(0, 6));
        setLakeRadius(ThreadLocalRandom.current().nextInt(1, 6));
        setNumVillages(ThreadLocalRandom.current().nextInt(0, 2));
        setTotalNumHouses(ThreadLocalRandom.current().nextInt(8, 33));

        System.out.println(NumRivers);
        System.out.println(RiverWidth);
        System.out.println(NumLakes);
        System.out.println(LakeRadius);
        System.out.println(NumVillages);
        System.out.println(TotalNumHouses);
        System.out.println("SET NUMBER OF FIRES");
        this.NumFires = numFires;
    }

    public static double getMaxWaterCapacity() {
        return MaxWaterCapacity;
    }

    public static void setMaxWaterCapacity(double maxWaterCapacity) {
        MaxWaterCapacity = maxWaterCapacity;
    }

    public double getNumFires() {
        return NumFires;
    }

    public static double getLakeRadius() {
        return LakeRadius;
    }

    public static void setLakeRadius(double lakeRadius) {
        LakeRadius = lakeRadius;
    }

    public static double getNumLakes() {
        return NumLakes;
    }

    public static void setNumLakes(double numLakes) {
        NumLakes = numLakes;
    }

    public static double getNumRivers() {
        return NumRivers;
    }

    public static void setNumRivers(double numRivers) {
        NumRivers = numRivers;
    }

    public static double getExtinguishingDist() {
        return ExtinguishingDist;
    }

    public static void setExtinguishingDist(double extinguishingDist) {
        ExtinguishingDist = extinguishingDist;
    }

    public static double getNumVillages() {
        return NumVillages;
    }

    public static void setNumVillages(double numVillages) {
        NumVillages = numVillages;
    }

    public static double getRiverWidth() {
        return RiverWidth;
    }

    public static void setRiverWidth(double riverWidth) {
        RiverWidth = riverWidth;
    }

    public static double getTotalNumHouses() {
        return TotalNumHouses;
    }

    public static void setTotalNumHouses(double totalNumHouses) {
        TotalNumHouses = totalNumHouses;
    }

    public static double getViewingDist() {
        return ViewingDist;
    }

    public static void setViewingDist(double viewingDist) {
        ViewingDist = viewingDist;
    }

    private void buildModel() {

        this.totalBurnedArea = 0;
        setEnvironment(new Object2DGrid(WORLD_WIDTH, WORLD_HEIGHT));
        setMapState(new MapState(this, WORLD_WIDTH, WORLD_HEIGHT));
    }

    private void buildSchedule() {
        getSchedule().scheduleActionAtInterval(1, this, "simulationStep");
    }

    private void buildDisplay() {
        Object2DDisplay firefightersDisplay = new Object2DDisplay(environment);
        firefightersDisplay.setObjectList(firefighters);
        //displaySurface.addDisplayable(firefightersDisplay, "Show Firefighters");
        //displaySurface.setBackground(Color.WHITE);
        //displaySurface.display();
    }

    @Override
    protected void launchJADE() {
        setMainContainer(Runtime.instance().createMainContainer(new ProfileImpl()));
        try {
            updateEnvironment();
            launchFirefighters();
            launchFireStation();
        } catch(StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private void launchFireStation() throws StaleProxyException {
        setFireStation(new FireStation(this));
        environment.putObjectAt(fireStation.getX(), fireStation.getY(), fireStation);
        mainContainer.acceptNewAgent("FireStation", fireStation).start();
        agents.put(fireStation.getAID(), fireStation);
    }

    private void launchFirefighters() throws StaleProxyException {
        List<Firefighter> firefighters = new ArrayList<>();
        for (int i = 0; i < NumFireFighters; i++) {
            Firefighter ff = new Firefighter(this, 0, i);
            environment.putObjectAt(ff.getX(), ff.getY(), ff);
            mainContainer.acceptNewAgent("firefighter-" + i, ff).start();
            agents.put(ff.getAID(), ff);
            firefighters.add(ff);
        }
        setFirefighters(firefighters);
    }

    public AID getFirestationAID() {
        return fireStation.getAID();
    }

    private void updateEnvironment() {
        for (int i = 0; i < WORLD_WIDTH; i++) {
            for (int j = 0; j < WORLD_HEIGHT; j++) {
                environment.putObjectAt(i,j,mapState.getGrid()[i][j]);
            }
        }
    }

    private void updateAgents() {
        List<Firefighter> nextFirefighters = new ArrayList<>();
        for (int i = 0; i < firefighters.size(); i++) {
            Firefighter ff = firefighters.get(i);
            if (ff.getState().getHealth() <= 0) {
                int nff = SimulationLauncher.getNumFFEnded();
                nff++;
                SimulationLauncher.setNumFFEnded(nff);
                System.out.println("rip in peace");
                mainContainer.removeLocalAgent(ff);
                agents.remove(ff.getAID(), ff);
            } else {
                nextFirefighters.add(ff);
            }
        }
        setFirefighters(nextFirefighters);
        for (Firefighter f: firefighters) {
            environment.putObjectAt(f.getX(), f.getY(), f);
        }

    }

    public void simulationStep() throws IOException {
        if(numFFEnded >= getNumFireFighters()){
            getSchedule().executeEndActions();
            writeDataToOutFile("results.csv");
            numFFEnded = 0;
            stop();
        }
        updateEnvironment();
        updateAgents();
        //displaySurface.updateDisplay();
    }

    public void writeDataToOutFile(String fileName) throws IOException {
        int fireExt = 0;
        double totalNumHouses = TotalNumHouses * NumVillages + NUM_REMOTE_HOUSES;
        if(isFireExtinguished()){
            fireExt = 1;
        }
        String str = totalBurnedArea + "," + fireExt + "," + NumFireFighters + "," + NumFires + "," +
                EXTINGUISH_PUMPING_VELOCITY + "," + REFILL_PUMPING_VELOCITY + "," + MaxWaterCapacity + "," + NUM_ROAMING_TURNS +
                "," + ViewingDist + "," + ExtinguishingDist + "," + NumRivers + "," + RiverWidth + ","
                + NumLakes + "," + LakeRadius + "," + NumVillages + "," + totalNumHouses;
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append('\n');
        writer.append(str);

        writer.close();
    }

    public Boolean isFireExtinguished(){
        for(int i = 0; i < this.mapState.getGrid().length; i++){
            for(int j = 0; j < this.mapState.getGrid()[0].length; j++){
                if(this.mapState.getGrid()[i][j].isOnFire()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String[] getInitParam() {
        System.out.println("GETTING PARAMS");
        return new String[] {
                "NumFireFighters", "NumFires","MaxWaterCapacity", "ViewingDist", "ExtinguishingDist"
        };
    }

    @Override
    public String getName() {
        return "AIAD - Fire Control";
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public ContainerController getMainContainer() {
        return mainContainer;
    }

    public void setMainContainer(ContainerController mainContainer) {
        this.mainContainer = mainContainer;
    }
/*
    public DisplaySurface getDisplaySurface() {
        return displaySurface;
    }

    public void setDisplaySurface(DisplaySurface displaySurface) {
        this.displaySurface = displaySurface;
    }
*/
    public Object2DGrid getEnvironment() {
        return environment;
    }

    public void setEnvironment(Object2DGrid environment) {
        this.environment = environment;
    }

    public FireStation getFireStation() {
        return fireStation;
    }

    public void setFireStation(FireStation firestation) {
        this.fireStation = firestation;
    }

    public List<Firefighter> getFirefighters() {
        return firefighters;
    }

    public void setFirefighters(List<Firefighter> firefighters) {
        this.firefighters = firefighters;
    }

    public MapState getMapState() {
        return mapState;
    }

    public void setMapState(MapState state) {
        this.mapState = state;
    }

    public static int getNumFFEnded(){
        return numFFEnded;
    }

    public static void setNumFFEnded(int ff){
        numFFEnded = ff;
    }

    public static int getTotalBurnedArea(){
        return totalBurnedArea;
    }

    public static void setTotalBurnedArea(int tba){
        totalBurnedArea = tba;
    }
}
