// NOTE: This interface is experimental and might not be the best way to implement the functionality.
// The reason for choosing a functional interface is purely educational.

/**
 * Shakeable is a functional interface that can be used together with lambda
 * expressions to 'pass a method' to another class via its constructor. The idea
 * was to pass a method that 'shakes' the screen from the InvaderGameState class
 * to the Shooter class such that when a missile hits the shooter, we can call
 * the shake method of the InvadersGameState class from within the Shooter
 * class.
 * 
 * https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
 * 
 */
public interface Shakeable {

    public void shake();

}