import java.util.ArrayList;
import java.util.Iterator;

/**
 * A class that implements this interface has a method `public boolean
 * mayBeDisposed()` to check whether or not the item is ready to be disposed.
 * 
 * For convenience the `Disposable` interface contains a method that will
 * iterate through an `ArrayList` of `Disposables`, checks whether any item is
 * ready to be disposed and then removes that item from the list.
 */
public interface Disposable {

    public boolean mayBeDisposed();

    // remove reference if no longer needed so object can be garbage collected
    public static void handleDisposing(ArrayList<? extends Disposable> group) {
        Iterator<? extends Disposable> critterIterator = group.iterator();
        while (critterIterator.hasNext()) {
            Disposable critter = critterIterator.next();
            if (critter.mayBeDisposed()) {
                critterIterator.remove();
            }
        }
    }

}