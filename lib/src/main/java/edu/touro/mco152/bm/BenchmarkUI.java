package edu.touro.mco152.bm;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.Callable;

public interface BenchmarkUI {
        /**
         * What has been done here: We have created an interface which, by reverse engineering everything that Swing
         * requires to function, created an interface which can be theoretically be implemented
         * by any UI that has the capability to similarly implement these methods. We also created
         * a Callable field disk, which is separate from DiskWorker and upon being plugged into
         * in the main class, allows the UI to utilize whatever logic is necessary, should the UI need to
         * implement the logic like Swing.
         */


        Callable disk = null;
        void setCallable(Callable userCallable);
        void publishUI(DiskMark diskmark);
        boolean isCancelledUI();
        void process(List<DiskMark> markList);

        void done();


        void setProgressUI(int percentComplete);

        void cancelUI(boolean b);


        void addPropertyChangeListenerUI(PropertyChangeListener pcl);

        void executeUI();
}
