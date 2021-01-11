package org.runaway.tasks;

public interface Task extends Cancellable {
    void run(Runnable runnable);
}
