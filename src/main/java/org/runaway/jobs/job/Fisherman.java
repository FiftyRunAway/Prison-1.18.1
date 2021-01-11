package org.runaway.jobs.job;

import org.bukkit.Material;
import org.runaway.jobs.Job;
import org.runaway.jobs.JobReq;
import org.runaway.jobs.JobRequriement;

import java.util.ArrayList;

public class Fisherman extends Job {

    @Override
    public int getLevel() {
        return 12;
    }

    @Override
    public String getName() {
        return "Рыболовство";
    }

    @Override
    public String getDescrition() {
        return "Ловите рыбу на пруду своей удочкой";
    }

    @Override
    public Material getMaterial() {
        return Material.FISHING_ROD;
    }

    @Override
    public ArrayList<JobReq[]> getLevels() {
        ArrayList<JobReq[]> reqs = new ArrayList<>();
        reqs.add(new JobReq[] { new JobReq(JobRequriement.LEGENDARY_FISH, 10) });
        reqs.add(new JobReq[] { new JobReq(JobRequriement.LEGENDARY_FISH, 50) });
        reqs.add(new JobReq[] { new JobReq(JobRequriement.LEGENDARY_FISH, 150) });
        reqs.add(new JobReq[] { new JobReq(JobRequriement.LEGENDARY_FISH, 300) });
        reqs.add(new JobReq[] { new JobReq(JobRequriement.LEGENDARY_FISH, 500) });
        return reqs;
    }

    @Override
    public String getConfigName() {
        return "fisherman";
    }
}
