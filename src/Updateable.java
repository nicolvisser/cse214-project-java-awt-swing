
/**
 * An updateable class 'wants' its update method to be called in whenever the
 * game updates it state in the game loop. The update method has a single
 * argument dt which is currently interpreted as the targeted amount of time in
 * milliseconds between frames
 */
public interface Updateable {

    public void update(int dt);

}