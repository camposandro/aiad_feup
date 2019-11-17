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

    // World update fixed rate
    public static int WORLD_UPDATE_RATE = 2000;
    public static int FF_UPDATE_RATE = 200;
    public static int MAX_NUM_FIRES = 5;
    public static int NUM_FIREFIGHTERS = 5;
    public static int NUM_ROAMING_TURNS = 3;

    // Minimum size             = 150x75
    // Recommended size         = 200x100
    // Recommended maximum size = 800X400
    // Absolute Repast Maximum  = 1200x600
    private static int ENV_WIDTH = 200;
    private static int ENV_HEIGHT = 100;

    private Random rand;

    private ContainerController mainContainer;

    private DisplaySurface displaySurface;
    private Object2DGrid environment;

    private MapState mapState;

    private FireStation fireStation;
    private List<Firefighter> firefighters;

    public Map<AID, MyAgent> agents = new HashMap();

    public SimulationLauncher() {
        setRand(new Random());
    }

    public static void main(String[] args) throws IOException {
        boolean BATCH_MODE = false;
        SimInit init = new SimInit();
        init.setNumRuns(1); // works only in batch mode
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

    private void buildModel() {
        setEnvironment(new Object2DGrid(ENV_WIDTH, ENV_HEIGHT));
        setMapState(new MapState(this,ENV_WIDTH, ENV_HEIGHT));
    }

    private void buildSchedule() {
        getSchedule().scheduleActionAtInterval(100, this, "simulationStep");
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
        for (int i = 0; i < NUM_FIREFIGHTERS; i++) {
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
        for (int i = 0; i < ENV_WIDTH; i++) {
            for (int j = 0; j < ENV_HEIGHT; j++) {
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
        updateEnvironment();
        updateAgents();
        displaySurface.updateDisplay();
    }

    @Override
    public String[] getInitParam() {
        return new String[] {
                "numFirefighters, numFires"
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
}
