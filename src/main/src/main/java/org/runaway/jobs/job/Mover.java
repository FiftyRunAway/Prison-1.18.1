package org.runaway.jobs.job;

import org.bukkit.Material;
import org.runaway.jobs.Job;
import org.runaway.jobs.JobReq;
import org.runaway.jobs.JobRequriement;

import java.util.ArrayList;

public class Mover extends Job {

    @Override
    public int getLevel() {
        return 30;
    }

    @Override
    public String getName() {
        return "Грузчик";
    }

    @Override
    public String getDescrition() {
        return "Выгружайте ящики из грузовиков";
    }

    @Override
    public Material getMaterial() {
        return Material.CHEST_MINECART;
    }

    @Override
    public ArrayList<JobReq[]> getLevels() {
        ArrayList<JobReq[]> reqs = new ArrayList<>();
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 25) });
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 100),
                new JobReq(JobRequriement.MONEY, 50),
                new JobReq(JobRequriement.LEVEL, 15)});
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 250),
                new JobReq(JobRequriement.MONEY, 150),
                new JobReq(JobRequriement.LEVEL, 16)});
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 500),
                new JobReq(JobRequriement.MONEY, 200),
                new JobReq(JobRequriement.LEVEL, 17)});
        reqs.add(new JobReq[] { new JobReq(JobRequriement.BOXES, 750),
                new JobReq(JobRequriement.MONEY, 350),
                new JobReq(JobRequriement.LEVEL, 18)});

        return reqs;
    }

    @Override
    public String getConfigName() {
        return "mover";
    }
}
