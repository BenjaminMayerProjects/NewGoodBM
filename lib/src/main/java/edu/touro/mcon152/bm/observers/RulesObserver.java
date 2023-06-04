package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;
/**
 * Slack implementation of the observer method, with a sheep complaining about the exceptional
 * sluggishness of one of the benchmarks to Slack upon a deviation of 3 percent from the norm.
 */
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
