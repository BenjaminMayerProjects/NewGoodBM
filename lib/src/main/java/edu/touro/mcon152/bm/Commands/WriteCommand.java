package edu.touro.mcon152.bm.Commands;

import edu.touro.mcon152.bm.*;
import edu.touro.mcon152.bm.observers.BenchmarkObserver;
import edu.touro.mcon152.bm.observers.BenchmarkSubject;
import edu.touro.mcon152.bm.persist.DiskRun;
import edu.touro.mcon152.bm.persist.EM;
import edu.touro.mcon152.bm.ui.Gui;
import jakarta.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mcon152.bm.App.*;
import static edu.touro.mcon152.bm.DiskMark.MarkType.WRITE;

/**
        * This is the write implementation of our Benchmark Command Interface. It serves to make benchmark writes, mostly independent
        * of the DiskWorker class, as the various data needed is provided directly via the constructor.
**/
public class WriteCommand implements BenchmarkCommand, BenchmarkSubject {
    private int wUnitsComplete = 0,
            rUnitsComplete = 0,
            unitsComplete;

    private int wUnitsTotal;
    private int rUnitsTotal;
    private int unitsTotal;

    private float percentComplete;

    private int blockSize = blockSizeKb * KILOBYTE;
    private byte[] blockArr = new byte[blockSize];

    private DiskMark wMark;
    private int startFileNum = App.nextMarkNumber;
    private BenchmarkUI userInterface;
    private int numOfBlocks;
    private int sizeOfBlocks;
    private ArrayList<BenchmarkObserver> observers;
    private DiskRun.BlockSequence sequence;
    private int numOfMarks;

    public WriteCommand(BenchmarkUI userInterface, int numOfBlocks, int sizeOfBlocks,
                        DiskRun.BlockSequence sequence, int numOfMarks) {
        this.userInterface = userInterface;
        this.numOfBlocks = numOfBlocks;
        this.sizeOfBlocks = sizeOfBlocks;
        this.sequence = sequence;
        this.numOfMarks = numOfMarks;
        wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        unitsTotal = wUnitsTotal + rUnitsTotal;
        observers = new ArrayList<>();
    }



    public boolean execute() {
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }
        // declare local vars formerly in DiskWorker




        DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, App.blockSequence);
        run.setNumMarks(App.numOfMarks);
        run.setNumBlocks(App.numOfBlocks);
        run.setBlockSize(App.blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        // Tell logger and GUI to display what we know so far about the Run
        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        // Create a test data file using the default file system and config-specified location
        if (!App.multiFile) {
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }

            /*
              Begin an outer loop for specified duration (number of 'marks') of benchmark,
              that keeps writing data (in its own loop - for specified # of blocks). Each 'Mark' is timed
              and is reported to the GUI for display as each Mark completes.
             */
        for (int m = startFileNum; m < startFileNum + App.numOfMarks && !userInterface.isCancelledUI(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            wMark = new DiskMark(WRITE);    // starting to keep track of a new benchmark
            wMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (App.writeSyncEnable) {
                mode = "rwd";
            }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;

                            /*
                              Report to GUI what percentage level of Entire BM (#Marks * #Blocks) is done.
                             */
                        worker.userInterface.setProgressUI((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }

                /*
                  Compute duration, throughput of this Mark's step of BM
                 */
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            msg("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");
            App.updateMetrics(wMark);

                /*
                  Let the GUI know the interim result described by the current Mark
                 */
            userInterface.publishUI(wMark);

            // Keep track of statistics to be displayed and persisted after all Marks are done.
            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
            run.setEndTime(new Date());
        }

        /**
         Inform all observers of the conclusion of a benchmark, so they can
         update a GUI, utilize Persistance, or send Slack messages.
         **/


        notifyObservers(run);




        return true;
    }

    @Override
    public void notifyObservers(DiskRun run)
    {
        for (BenchmarkObserver observer: observers) {
            observer.update(run);

        }
    }

    @Override
    public void addObserver(BenchmarkObserver observer) {
        observers.add(observer);

    }

    @Override
    public void removeObserver(BenchmarkObserver observer)
    {
        observers.remove(observer);
    }
}

