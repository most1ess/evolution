import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import simulation.Screen;

public class Main {
    public static void main(String[] args) {
        Screen screen = new Screen("Simulation 2.0.3");
        try {
            AppGameContainer appGameContainer = new AppGameContainer(screen);
            appGameContainer.setTargetFrameRate(30);
            appGameContainer.setDisplayMode(1500, 1000, false);
            appGameContainer.start();
        } catch (SlickException e) {
            System.out.println("Watafak some error happened!!!");
        }
    }
}
