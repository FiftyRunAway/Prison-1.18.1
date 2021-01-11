package org.runaway.jobs;

public class JobReq {

    private JobRequriement requriement;
    private int value;

    public JobReq(JobRequriement requriement, int value) {
        this.requriement = requriement;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public JobRequriement getRequriement() {
        return requriement;
    }
}
