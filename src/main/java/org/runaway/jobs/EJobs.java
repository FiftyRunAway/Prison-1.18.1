package org.runaway.jobs;

import org.runaway.jobs.job.Fisherman;

public enum EJobs {
    FISHERMAN(new Fisherman());

    private Job job;

    EJobs(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
