package org.runaway.jobs;

import org.runaway.jobs.job.Fisherman;
import org.runaway.jobs.job.Mover;

public enum EJobs {
    MOVER(new Mover()),
    FISHERMAN(new Fisherman());

    private Job job;

    EJobs(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
