package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;

/**
 * This is the interface of our Observer. Simple, but upon being called by the Subject's informObservers
 * method, our observers will stand ready to execute a wide variety of updating observations, from Graphical
 * User Interface changes to telling the rest of the team on Slack of any problems.
 */
public interface BenchmarkObserver {
    public void update(DiskRun run);
}
