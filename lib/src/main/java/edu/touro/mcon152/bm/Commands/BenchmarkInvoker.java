package edu.touro.mcon152.bm.Commands;

public class BenchmarkInvoker {
    /**
     * This is our invoker. It serves to load up commands in order so that when the client makes a reques to the invoker,
     * it is ready to carry out any command.
     */
    private BenchmarkCommand command;
    public BenchmarkInvoker(BenchmarkCommand command)
    {
        this.command = command;
    }
    public BenchmarkInvoker()
    {

    }
    public boolean runCommand()
    {
        command.execute();
        return true;
    }

    public void setCommand(BenchmarkCommand command) {
        this.command = command;
    }
}
