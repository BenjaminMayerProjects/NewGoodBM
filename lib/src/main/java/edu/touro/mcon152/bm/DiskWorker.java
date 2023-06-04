package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.Commands.BenchmarkCommand;
import edu.touro.mcon152.bm.Commands.BenchmarkInvoker;
import edu.touro.mcon152.bm.Commands.ReadCommand;
import edu.touro.mcon152.bm.Commands.WriteCommand;
import edu.touro.mcon152.bm.ui.Gui;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mcon152.bm.App.*;

/**
 * Run the disk benchmarking exclusively as a Swing-compliant thread (only one of these threads can run at
 * once.) Must cooperate with Swing to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks, all of which is done in doInBackground(). It is instantiated by the
 * startBenchmark() method.
 * <p>
 * To be Swing compliant this class extends SwingWorker and is dependant on it. It declares that its final return (when
 * doInBackground() is finished) is of type Boolean, and declares that intermediate results are communicated to
 * Swing using an instance of the DiskMark class.
 */

/**
 * Run the disk benchmarking as a Swing-compliant thread (only one of these threads can run at
 * once.) Can cooperate with Swing and other forms of user interface to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks, all of which is done in doInBackground(). It is instantiated by the
 * startBenchmark() method.
 * <p>
 * To be compliant with many types of user interfaces, including Swing, our class implements the Callable interface
 * (As suggested on our course's Slack) This means that our DoInBackground method, which prior required
 * direct dependency on Swing, can be replaced by passing the DW class to whichever interface we please
 * and having said interface call our Call method to execute DiskWorker's logic.
 */

public class DiskWorker implements Callable {
    public BenchmarkUI userInterface;

    public DiskWorker()
    {
        this.userInterface = userInterface;
    }

    // Record any success or failure status returned from SwingWorker (might be us or super)
    Boolean lastStatus = null;  // so far unknown
    //callable copy/paste
    //
    @Override
    public Object call() throws Exception {

        /*
          We 'got here' because: 1: End-user clicked 'Start' on the benchmark UI,
          which triggered the start-benchmark event associated with the App::startBenchmark()
          method.  2: startBenchmark() then instantiated a DiskWorker, and called
          its (super class's) execute() method, causing our user interface to eventually
          call this doInBackground() method.
         */
        Logger.getLogger(App.class.getName()).log(Level.INFO, "*** New worker thread started ***");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);
        /**
         * To utilize our Command Pattern, we shall initialize our Invoker, and our commands. This initialization
         * will only be done once per running of DiskWorker.
         */

        BenchmarkInvoker invoker = new BenchmarkInvoker();
        ReadCommand readCommand = new ReadCommand(userInterface, numOfBlocks, blockSizeKb, blockSequence, numOfMarks);
        WriteCommand writeCommand = new WriteCommand(userInterface, numOfBlocks, blockSizeKb, blockSequence, numOfMarks);
        /*
        /*
          init local vars that keep track of benchmarks, and a large read/write buffer
         */
        Gui.updateLegend();  // init chart legend info

        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }
        /*
          The GUI allows a Write, Read, or both types of BMs to be started. They are done serially.
         */
        if (App.writeTest) {
            invoker.setCommand(writeCommand);
            invoker.runCommand();
        }
        if (App.readTest && App.writeTest && !userInterface.isCancelledUI()) {
            JOptionPane.showMessageDialog(Gui.mainFrame,
                    """
                            For valid READ measurements please clear the disk cache by
                            using the included RAMMap.exe or flushmem.exe utilities.
                            Removable drives can be disconnected and reconnected.
                            For system drives use the WRITE and READ operations\s
                            independantly by doing a cold reboot after the WRITE""",
                    "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
        }

        // Same as above, just for Read operations instead of Writes.
        if (App.readTest) {
            invoker.setCommand(readCommand);
            invoker.runCommand();

        }
        App.nextMarkNumber += App.numOfMarks;
        return true;
    }

    /**
     * Process a list of 'chunks' that have been processed, ie that our thread has previously
     * published to Swing. For my info, watch Professor Cohen's video -
     * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
     * @param markList a list of DiskMark objects reflecting some completed benchmarks
     **/



    /**
     * Called when doInBackGround method of SwingWorker successfully or unsuccessfully finishes or is aborted.
     * This method is called by Swing and has access to the get method within it's scope, which returns the computed
     * result of the doInBackground method.
     */
    public Boolean getLastStatus() {
        return lastStatus;
    }
    public void setUserInterface(BenchmarkUI newInterface)
    {
        userInterface = newInterface;
    }

    public BenchmarkUI getUserInterface() {
        return userInterface;
    }
}
