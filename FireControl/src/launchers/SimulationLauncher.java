package launchers;

import agents.Fire;
import agents.FireStation;
import agents.Firefighter;
import agents.MyAgent;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.AID;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import utils.MapCell;
import utils.MapState;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SimulationLauncher extends Repast3Launcher {

    private Random rand;

    private ContainerController mainContainer;

    private DisplaySurface displaySurface;
    private Object2DGrid environment;
    private int envWidth = 2;
    private int envHeight = 2;

    private int numFirefighters = 1;
    private int numFires = 1;

    private FireStation fireStation;
    private List<Firefighter> firefighters;
    private List<Fire> fires;

    private Map<AID, MyAgent> myAgents;

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
        setEnvironment(new Object2DGrid(envWidth, envHeight));
    }

    private void buildSchedule() {
        getSchedule().scheduleActionAtInterval(1000, this, "simulationStep");
    }

    private void buildDisplay() {
        Object2DDisplay firefightersDisplay = new Object2DDisplay(environment);
        firefightersDisplay.setObjectList(firefighters);
        displaySurface.addDisplayable(firefightersDisplay, "Show Firefighters");

        Object2DDisplay firesDisplay = new Object2DDisplay(environment);
        firesDisplay.setObjectList(fires);
        displaySurface.addDisplayable(firesDisplay, "Show Fires");

        displaySurface.setBackground(Color.WHITE);
        displaySurface.setSize(400,400);
        displaySurface.display();
    }

    @Override
    protected void launchJADE() {
        setMainContainer(Runtime.instance().createMainContainer(new ProfileImpl()));
        try {
            initializeMapCells();
            launchFirefighters();
            launchFires();
            //launchFireStation();
        } catch(StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private void initializeMapCells() {
        MapCell[][] state = MapState.initialState;
        for (int i = 0; i < envWidth; i++) {
            for (int j = 0; j < envHeight; j++) {
                environment.putObjectAt(i,j,state[i][j]);
            }
        };
    }

    private void launchFireStation() throws StaleProxyException {
        setFireStation(new FireStation(this));
        environment.putObjectAt(fireStation.getX(), fireStation.getY(), fireStation);
        mainContainer.acceptNewAgent("FireStation", fireStation).start();
    }

    private void launchFirefighters() throws StaleProxyException {
        List<Firefighter> firefighters = new ArrayList<>();
        for (int i = 0; i < numFirefighters; i++) {
            Firefighter ff = new Firefighter(this);
            System.out.println("Firefighter: (" + ff.getX() + "," + ff.getY() + ")");
            environment.putObjectAt(ff.getX(), ff.getY(), ff);
            mainContainer.acceptNewAgent("firefighter-" + i, ff).start();
            firefighters.add(ff);
        };
        setFirefighters(firefighters);
    }

    private void launchFires() throws StaleProxyException {
        List<Fire> fires = new ArrayList<>();
        for (int i = 0; i < numFires; i++) {
            Fire f = new Fire(this, rand.nextInt(envWidth), rand.nextInt(envHeight));
            System.out.println("Fire: (" + f.getX() + "," + f.getY() + ")");
            environment.putObjectAt(f.getX(), f.getY(), f);
            mainContainer.acceptNewAgent("fire-" + i, f).start();
            fires.add(f);
        };
        setFires(fires);
    }

    public void simulationStep() {
        System.out.println("UPDATED");
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

    public int getEnvWidth() {
        return envWidth;
    }

    public void setEnvWidth(int envWidth) {
        this.envWidth = envWidth;
    }

    public int getEnvHeight() {
        return envHeight;
    }

    public void setEnvHeight(int envHeight) {
        this.envHeight = envHeight;
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

    public List<Fire> getFires() {
        return fires;
    }

    public void setFires(List<Fire> fires) {
        this.fires = fires;
    }
}
