public class PCB {
    private static int counter = 0;
    private int id;
    private int runtime;
    private int memory;
    private int priority;
    private int ioStart;
    private int ioStop;
    private int originalIoStart;
    private int originalIoStop;
    private String state;
    private int memoryAddress;

    public PCB(int runtime, int memory, int priority, int ioStart, int ioStop) {
        this.id = counter++;
        this.runtime = runtime;
        this.memory = memory;
        this.priority = priority;
        this.ioStart = ioStart;
        this.ioStop = ioStop;
        this.originalIoStart = ioStart;
        this.originalIoStop = ioStop;
        this.state = "READY";
    }

    public int getId() {
        return id;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getMemory() {
        return memory;
    }

    public int getPriority() {
        return priority;
    }

    public int getIoStart() {
        return ioStart;
    }

    public int getIoStop() {
        return ioStop;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getMemoryAddress() {
        return memoryAddress;
    }

    public void setMemoryAddress(int memoryAddress) {
        this.memoryAddress = memoryAddress;
    }

    public void setIoStart(int ioStart) {
        this.ioStart = ioStart;
    }

    public void setIoStop(int ioStop) {
        this.ioStop = ioStop;
    }

    public void run(int timeQuantum) {
        this.state = "RUNNING";
        runtime -= timeQuantum;
        if (runtime <= 0) {
            runtime = 0;
            state = "COMPLETED";
        } else {
            state = "READY";
        }
    }

    public void block() {
        this.state = "BLOCKED";
    }

    public void unblock() {
        this.state = "READY";
    }

    public void resetIoTimes() {
        this.ioStart = this.originalIoStart;
        this.ioStop = this.originalIoStop;
    }

    public static void resetCounter() {
        counter = 0;
    }
}
