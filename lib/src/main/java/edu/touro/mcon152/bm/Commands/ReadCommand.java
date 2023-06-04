package edu.touro.mcon152.bm.Commands;

import edu.touro.mcon152.bm.*;
import edu.touro.mcon152.bm.persist.DiskRun;
import edu.touro.mcon152.bm.persist.EM;
import edu.touro.mcon152.bm.ui.Gui;
import jakarta.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import static edu.touro.mcon152.bm.App.*;
import static edu.touro.mcon152.bm.DiskMark.MarkType.READ;

public class ReadCommand implements BenchmarkCommand
        {
    private int wUnitsComplete = 0,
            rUnitsComplete = 0,
            unitsComplete;

    private int wUnitsTotal;
    private int rUnitsTotal;
    private int unitsTotal;

    private float percentComplete;

    private int blockSize = blockSizeKb * KILOBYTE;
    private byte[] blockArr = new byte[blockSize];

    private DiskMark rMark;
    private int startFileNum = App.nextMarkNumber;
    private BenchmarkUI userInterface;
    private int numOfBlocks;
    private int sizeOfBlocks;
    private DiskRun.BlockSequence sequence;
    private int numOfMarks;

    public ReadCommand(BenchmarkUI userInterface, int numOfBlocks, int sizeOfBlocks,
                       DiskRun.BlockSequence sequence, int numOfMarks) {
        this.userInterface = userInterface;
        this.numOfBlocks = numOfBlocks;
        this.sizeOfBlocks = sizeOfBlocks;
        this.sequence = sequence;
        this.numOfMarks = numOfMarks;
        wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        unitsTotal = wUnitsTotal + rUnitsTotal;
    }
    public boolean execute()
    {

        DiskRun run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
        run.setNumMarks(App.numOfMarks);
        run.setNumBlocks(App.numOfBlocks);
        run.setBlockSize(App.blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));


        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        for (int m = startFileNum; m < startFileNum + App.numOfMarks && !userInterface.isCancelledUI(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);  // starting to keep track of a new benchmark
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                for (int b = 0; b < numOfBlocks; b++) {
                    if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                        int rLoc = Util.randInt(0, numOfBlocks - 1);
                        rAccFile.seek((long) rLoc * blockSize);
                    } else {
                        rAccFile.seek((long) b * blockSize);
                    }
                    rAccFile.readFully(blockArr, 0, blockSize);
                    totalBytesReadInMark += blockSize;
                    rUnitsComplete++;
                    unitsComplete = rUnitsComplete + wUnitsComplete;
                    percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
                    App.worker.getUserInterface().setProgressUI((int) percentComplete);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            App.updateMetrics(rMark);
            userInterface.publishUI(rMark);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }

    /*
      Persist info about the Read BM Run (e.g. into Derby Database) and add it to a GUI panel
     */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();

        Gui.runPanel.addRun(run);
        return true;
    }
}
