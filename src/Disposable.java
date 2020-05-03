import java.util.ArrayList;
import java.util.Iterator;

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