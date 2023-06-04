package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.dataDir;


public class SwingWorkerUI extends  SwingWorker<Boolean, DiskMark> implements BenchmarkUI {

    Callable disk = null;
    private Boolean lastStatus = null;

    @Override
    public void setCallable(Callable userCallable) {
        disk = userCallable;
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        return (Boolean) disk.call();
    }



    @Override
    public void publishUI(DiskMark diskmark) {
        publish(diskmark);

    }

    @Override
    public boolean isCancelledUI() {
        return isCancelled();
    }

    @Override
    public void process(List<DiskMark> markList) {
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }
    @Override
    public void done() {
        // Obtain final status, might from doInBackground ret value, or SwingWorker error
        try {
            lastStatus = super.get();   // record for future access
        } catch (Exception e) {
            Logger.getLogger(App.class.getName()).warning("Problem obtaining final status: " + e.getMessage());
        }

        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }
    @Override
    public void setProgressUI(int percentComplete)
    {
        setProgress(percentComplete);
    }
    @Override
    public void cancelUI(boolean b)
    {
        cancel(b);
    }
    public Boolean getLastStatusUI()
    {
        return lastStatus;
    }


    @Override
    public void addPropertyChangeListenerUI(PropertyChangeListener pcl) {
        addPropertyChangeListener(pcl);
    }

    @Override
    public void executeUI() {
        execute();
    }


}
