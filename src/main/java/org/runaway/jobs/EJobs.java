package org.runaway.jobs;

import org.runaway.jobs.job.Fisherman;
import org.runaway.jobs.job.Mover;

public enum EJobs {
    FISHERMAN(new Fisherman()),
    MOVER(new Mover());

    private Job job;

    EJobs(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
