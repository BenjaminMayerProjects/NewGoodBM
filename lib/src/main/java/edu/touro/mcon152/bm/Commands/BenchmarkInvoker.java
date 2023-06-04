package edu.touro.mcon152.bm.Commands;

public class BenchmarkInvoker {
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
