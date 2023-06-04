package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;

public interface BenchmarkObserver {
    public void update(DiskRun run);
}
