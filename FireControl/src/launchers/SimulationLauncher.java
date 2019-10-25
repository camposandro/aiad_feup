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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulationLauncher extends Repast3Launcher {

    private static boolean BATCH_MODE = false;

    private int NUM_FIREFIGHTERS = 10;

    private ContainerController mainContainer;

    private DisplaySurface displaySurface;
    private Object2DGrid environment;
    private int envWidth = 10;
    private int envHeight = 10;

    private FireStation fireStation;
    private List<Firefighter> firefighters;
    private List<Fire> fires;

    private Map<AID, MyAgent> myAgents;

    public static void main(String[] args) throws IOException {
        SimInit init = new SimInit();
        init.loadModel(new SimulationLauncher(), null, BATCH_MODE);
    }

    public void setup() {
        super.setup();
        String displaySurfaceName = "Fire Control Environment";
        setDisplaySurface(new DisplaySurface(this, displaySurfaceName));
        registerDisplaySurface(displaySurfaceName, displaySurface);
    }

    public void begin() {
        super.begin();
        buildModel();
        buildSchedule();
        buildDisplay();
    }

    private void buildModel() {
        setEnvironment(new Object2DGrid(envWidth, envHeight));
    }

    private void buildSchedule() {
        getSchedule().scheduleActionAtInterval(1, this, "simulationStep");
    }

    private void buildDisplay() {
        Object2DDisplay firefightersDisplay = new Object2DDisplay(environment);
        firefightersDisplay.setObjectList(firefighters);
        displaySurface.addDisplayable(firefightersDisplay, "Firefighters");
        //addSimEventListener(displaySurface);
        displaySurface.display();
    }

    @Override
    protected void launchJADE() {
        setMainContainer(Runtime.instance().createMainContainer(new ProfileImpl()));
        try {
            launchFireStation();
            launchFirefighters();
            launchFires();
        } catch(StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private void launchFireStation() throws StaleProxyException {
        setFireStation(new FireStation(this));
        environment = new Object2DGrid(envWidth, envHeight);
        environment.putObjectAt(fireStation.getX(), fireStation.getY(), fireStation);
        mainContainer.acceptNewAgent("FireStation", fireStation).start();
    }

    private void launchFirefighters() {
        setFirefighters(new ArrayList<>());
    }

    private void launchFires() {
        setFires(new ArrayList<>());
    }

    public void simulationStep() {

    }

    @Override
    public String[] getInitParam() {
        return new String[] {
                "NUM_FIREFIGHTERS"
        };
    }

    @Override
    public String getName() {
        return "AIAD - Fire Control";
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
