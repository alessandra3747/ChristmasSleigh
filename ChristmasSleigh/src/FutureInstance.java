import java.util.concurrent.Future;

public class FutureInstance {

    private String name;
    private Future<?> future;

    public FutureInstance(String name, Future<?> future) {
        this.name = name;
        this.future = future;
    }

    public String getName() {
        return name;
    }

    public Future<?> getFuture() {
        return future;
    }

    public boolean isInitialized() {
        return future != null;
    }

    public String getState() {
        if (future == null)
            return "Uninitialized";
        if (future.isDone())
            return "Done";
        if (future.isCancelled())
            return "Cancelled";
        return "Running";
    }

    @Override
    public String toString() {
        return name +
                "   |  Initialized: " + (isInitialized()? "YES":"NO") +
                "   |  State: " + getState() +
                "   |  is Done: " + (future!=null && future.isDone()? "YES":"NO");
    }
}
