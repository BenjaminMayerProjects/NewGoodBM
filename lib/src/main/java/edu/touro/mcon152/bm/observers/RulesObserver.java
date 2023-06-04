package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;

public class RulesObserver  implements BenchmarkObserver{

    @Override
    public void update(DiskRun run) {
        if(run.getRunAvg() * 1.03 < run.getRunMax())
        {
            SlackManager performanceInspector = new SlackManager("GoodBenchmark");
            performanceInspector.postMsg2OurChannel("There has been a baaaad :sheep: benchmark," +
                    " one of " + run.getRunMax() + " which is more than three percent above the average" +
                    " of " + run.getRunAvg() + ".");
        }
    }
}
