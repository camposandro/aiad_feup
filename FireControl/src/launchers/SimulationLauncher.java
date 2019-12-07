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
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import utils.MapState;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SimulationLauncher extends Repast3Launcher {
    //-------------------------------------------WORLD SELECTION--------------------------------------------//
    public static int WORLD_WIDTH = 200;        // Recommended size = 200
    public static int WORLD_HEIGHT = 100;       // Recommended size = 100

    public static boolean RANDOMWORLD = false;

    public static int MAX_NUM_FIRES = 2;
    public static int NUM_FIREFIGHTERS = 2;

    public static int NUM_RIVERS = 1;
    public static int RIVER_MAX_WIDTH = 5;
    public static int NUM_LAKES = 1;
    public static int LAKE_MAX_RADIUS = 12;

    public static int NUM_VILLAGES = 1;
    public static int VILLAGE_MAX_HOUSES = 64;

    public static int NUM_REMOTE_HOUSES = 2;

    public static int WORLD_UPDATE_RATE = 1000; // World update fixed rate
    public static int FF_UPDATE_RATE = 10;
    
    public static int NUM_ROAMING_TURNS = 5;
    //-----------------------------------------------------------------------------------------------------//
    private Random rand;

    private static int numFFEnded = 0;
    private static int totalBurnedArea = 0;
    public static boolean endSim = false;

    private ContainerController mainContainer;

    private DisplaySurface displaySurface;
    private Object2DGrid environment;
    private int envWidth = WORLD_WIDTH;
    private int envHeight = WORLD_HEIGHT;

    private MapState mapState;

    private double NumFirefighters = NUM_FIREFIGHTERS;
    private double NumFires = MAX_NUM_FIRES;

    private FireStation fireStation;
    private List<Firefighter> firefighters;

    public Map<AID, MyAgent> agents = new HashMap();

    public SimulationLauncher() {
        setRand(new Random());
    }

    public static void main(String[] args) throws IOException {
        boolean BATCH_MODE = false;
        SimInit init = new SimInit();
        init.setNumRuns(100); // works only in batch mode
        init.loadModel(new SimulationLauncher(), null, BATCH_MODE);
    }

    public void setup() {
        super.setup();

        if (displaySurface != null)
            displaySurface.dispose();

        String displaySurfaceName = "Fire Control Environment";
        setDisplaySurface(new DisplaySurface(this, displaySurfaceName));
        registerDisplaySurface(displaySurfaceName, displaySurface);
    }

    public void begin() {
        buildModel();
        buildDisplay();
        buildSchedule();
        super.begin();
    }
    public void setNumFirefighters(double numFirefighters) {
        this.NumFirefighters = numFirefighters;
    }

    public double getNumFirefighters() {
        return NumFirefighters;
    }
    public void setNumFires(double numFires) {
        System.out.println("SET NUMBER OF FIRES");
        this.NumFires = numFires;
    }

    public double getNumFires() {
        return NumFires;
    }
    private void buildModel() {
        setEnvironment(new Object2DGrid(WORLD_WIDTH, WORLD_HEIGHT));
        setMapState(new MapState(this, WORLD_WIDTH, WORLD_HEIGHT));
    }

    private void buildSchedule() {
        getSchedule().scheduleActionAtInterval(1, this, "simulationStep");
    }

    private void buildDisplay() {
        Object2DDisplay firefightersDisplay = new Object2DDisplay(environment);
        firefightersDisplay.setObjectList(firefighters);
        displaySurface.addDisplayable(firefightersDisplay, "Show Firefighters");
        displaySurface.setBackground(Color.WHITE);
        displaySurface.display();
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
        for (int i = 0; i < NumFirefighters; i++) {
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

    public void simulationStep() {
        if(numFFEnded >= NUM_FIREFIGHTERS){
            getSchedule().executeEndActions();
            stop();
        }
        updateEnvironment();
        updateAgents();
        displaySurface.updateDisplay();
    }

    @Override
    public String[] getInitParam() {
        System.out.println("GETTING PARAMS");
        return new String[] {
                "NumFirefighters", "NumFires"
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

    public DisplaySurface getDisplaySurface() {
        return displaySurface;
    }

    public void setDisplaySurface(DisplaySurface displaySurface) {
        this.displaySurface = displaySurface;
    }

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
