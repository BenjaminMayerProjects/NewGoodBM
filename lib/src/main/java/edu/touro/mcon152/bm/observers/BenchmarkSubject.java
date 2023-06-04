package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;

/**
 * This is the interface of our Subject side of the Observer Pattern. This is a classic implementation of
 * a subject, with the caveat that when the informObservers() method is called the diskRun is passed to the
 * observers, so that they have the info that they need in order to act.
 */

public interface BenchmarkSubject {
    void notifyObservers(DiskRun run);
    void addObserver(BenchmarkObserver observer);
    void removeObserver(BenchmarkObserver observer);
}
