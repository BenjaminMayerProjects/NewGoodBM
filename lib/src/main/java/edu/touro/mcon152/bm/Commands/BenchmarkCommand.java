package edu.touro.mcon152.bm.Commands;

public interface BenchmarkCommand {
    /**
     * This is the interface that will allow us to use the command pattern. The interface
     * has only one method, that being the execute method. Using this interface, we can load different
     * sorts of BM commands (for the moment limited to read and write benchmarks) into
     * an invoker to be executed as requested by our DiskWorker
     */
    public boolean execute();
}
