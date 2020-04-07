import java.util.LinkedList;
import java.util.List;

public class ThreadPool extends ThreadGroup {

    private static final IDAssigner poolID = new IDAssigner(1);

    private boolean alive;
    private List<Runnable> taskList;

    public ThreadPool(int numThreads) {
        super("ThreadPool-" + poolID.next());
        setDaemon(true);
        taskList = new LinkedList<>();
        alive = true;
        for (int i = 0; i < numThreads; i++) {
            new PooledThread(this).start();
        }
    }

    public synchronized void runTask(Runnable task) {
        if (!alive)
            throw new IllegalStateException("ThreadPool-" + poolID.getCurrent() + " is dead");
        if (task != null) {
            taskList.add(task);
            notify();
        }
    }

    // continue tutorial ???? https://www.youtube.com/watch?v=QyZZeGVKyAo


    protected synchronized Runnable getTask() throws InterruptedException {
        while (taskList.size() == 0) {
            if (!alive)
                return null;
            wait();
        }
        return taskList.remove(0);
    }

}