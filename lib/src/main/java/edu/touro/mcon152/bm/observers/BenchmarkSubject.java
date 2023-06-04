package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;

public interface BenchmarkSubject {
    void notifyObservers(DiskRun run);
    void addObserver(BenchmarkObserver observer);
    void removeObserver(BenchmarkObserver observer);
}
