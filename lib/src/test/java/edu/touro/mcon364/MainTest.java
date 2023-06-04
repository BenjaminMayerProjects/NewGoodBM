package edu.touro.mcon364;


import edu.touro.mcon152.bm.App;
import edu.touro.mcon152.bm.BenchmarkUI;
import edu.touro.mcon152.bm.DiskMark;
import edu.touro.mcon152.bm.DiskWorker;
import edu.touro.mcon152.bm.ui.Gui;
import edu.touro.mcon152.bm.ui.MainFrame;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests that show the benchmark can be run independent of
 * the SwingUI. Proven using a very limited version of
 * the benchmark being run using a stripped down UIInterface
 * that has just enough to pass the tests
 *
 * @implement UIInterface
 */
public class MainTest implements BenchmarkUI{
    private int currentPercentComplete;

    public MainTest() {
        setupDefaultAsPerProperties();

    }


    public void setupDefaultAsPerProperties()
    {
        /**
         * Bruteforce setup of static classes/fields to allow DiskWorker to run.
         *
         * @author lcmcohen
         */


        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();
        System.out.println(App.getConfigString());
        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr


        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference
        App.dataDir = new File(App.locationDir.getAbsolutePath() + File.separator + App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        } else {
            App.dataDir.mkdirs(); // create data dir if not already present
            App.worker = new DiskWorker();
            BenchmarkUI test = new MainTest();
            test.setCallable(App.worker);
            App.worker.setUI(test);
        }
    }



    @Override
    public void setCallable(Callable userCallable) {

    }




    @Override
    public boolean isCancelledUI() {
        return false;
    }

    @Override
    public void process(List<DiskMark> markList) {

    }

    @Override
    public void done() {

    }


    @Override
    public void cancelUI(boolean b) {
    }

    @Override
    public void addPropertyChangeListenerUI(PropertyChangeListener pcl) {

    }

    @Test
    @Override
    public void executeUI() {
        try {

            setupDefaultAsPerProperties();
            App.worker = new DiskWorker();
            BenchmarkUI test = new MainTest();
            test.setCallable(App.worker);
            App.worker.setUI(test);
            App.worker.call();
            assertEquals(100, currentPercentComplete);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void setProgressUI(int percentComplete) {
        assertTrue(percentComplete >= 0 && percentComplete <= 100);
        currentPercentComplete = percentComplete;
    }
    @Override
    public void publishUI (DiskMark wMark) {
        assertNotNull(wMark);
    }
}
