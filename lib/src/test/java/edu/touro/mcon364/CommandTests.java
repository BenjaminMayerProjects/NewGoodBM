package edu.touro.mcon364;

import edu.touro.mcon152.bm.App;
import edu.touro.mcon152.bm.Commands.BenchmarkInvoker;
import edu.touro.mcon152.bm.Commands.ReadCommand;
import edu.touro.mcon152.bm.Commands.WriteCommand;
import edu.touro.mcon152.bm.ui.Gui;
import edu.touro.mcon152.bm.ui.MainFrame;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static edu.touro.mcon152.bm.persist.DiskRun.BlockSequence.SEQUENTIAL;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandTests {
    @Test
    void writeTest()
    {
        setupDefaultAsPerProperties();
        BenchmarkInvoker testInvoker = new BenchmarkInvoker();
        WriteCommand writeCommand = new WriteCommand(new MainTest(), 128, 2048, SEQUENTIAL, 25);
        testInvoker.setCommand(writeCommand);
        assertTrue(testInvoker.runCommand());

    }
    @Test
    void ReadTest()
    {
        setupDefaultAsPerProperties();
        BenchmarkInvoker testInvoker = new BenchmarkInvoker();
        ReadCommand readTest = new ReadCommand(new MainTest(), 128, 2048, SEQUENTIAL, 25);
        testInvoker.setCommand(readTest);
        assertTrue(testInvoker.runCommand());

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
        }
    }
}
