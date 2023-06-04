package edu.touro.mcon152.bm.persist;

import edu.touro.mcon152.bm.observers.BenchmarkObserver;
import edu.touro.mcon152.bm.observers.BenchmarkSubject;
import edu.touro.mcon152.bm.ui.Gui;

public class PersistenceObserver implements BenchmarkObserver {
    @Override
    public void update(DiskRun run)
    {
        Gui.runPanel.addRun(run);

    }
}
