import java.util.*;

public class OS {
    private List<PCB> processes;
    private Queue<PCB> readyQueue;
    private Queue<PCB> blockedQueue;
    private List<PCB> completedQueue;
    private PCB runningProcess;
    private MemoryManager memoryManager;
    private DiskManager diskManager;
    private FileSystem fileSystem;
    private int timeQuantum;
    private List<String> schedulingLog;
    private int currentTime;
    private boolean usePriorityScheduling;

    //初始化OS对象
    public OS(int memorySize, int diskSize) {
        processes = new ArrayList<>();
        readyQueue = new LinkedList<>();
        blockedQueue = new LinkedList<>();
        completedQueue = new ArrayList<>();
        memoryManager = new MemoryManager(memorySize);
        diskManager = new DiskManager(diskSize);
        fileSystem = new FileSystem();
        timeQuantum = 5;
        schedulingLog = new ArrayList<>();
        runningProcess = null;
        currentTime = 0;
        usePriorityScheduling = false; // 默认使用RR算法
    }

    //设置是否使用优先级调度算法
    public void setUsePriorityScheduling(boolean usePriorityScheduling) {
        this.usePriorityScheduling = usePriorityScheduling;
    }

    //获取就绪队列
    public Queue<PCB> getReadyQueue() {
        return readyQueue;
    }

    //获取当前正在运行的进程
    public PCB getRunningProcess() {
        return runningProcess;
    }

    //获取阻塞队列
    public Queue<PCB> getBlockedQueue() {
        return blockedQueue;
    }

    //获取完成的进程列表
    public List<PCB> getCompletedProcesses() {
        return completedQueue;
    }

    //获取文件列表
    public List<File> getFiles() {
        return fileSystem.getFiles();
    }

    //获取调度日志
    public List<String> getSchedulingLog() {
        return schedulingLog;
    }

    // 执行命令，根据命令类型调用相应的操作
    public String executeCommand(String command) {
        String[] parts = command.split(" ");
        int pid;
        switch (parts[0]) {
            case "creatproc":
                // 创建进程
                int runtime = Integer.parseInt(parts[1]);
                int memory = Integer.parseInt(parts[2]);
                int priority = Integer.parseInt(parts[3]);
                int ioStart = Integer.parseInt(parts[4]);
                int ioStop = Integer.parseInt(parts[5]);
                return createProcess(runtime, memory, priority, ioStart, ioStop);
            case "killproc":
                // 终止进程
                pid = Integer.parseInt(parts[1]);
                return killProcess(pid);
            case "psproc":
                // 显示进程状态
                return showProcesses();
            case "mem":
                // 显示内存使用
                return memoryManager.show();
            case "disk":
                // 显示磁盘使用
                return diskManager.show();
            case "creatfile":
                // 创建文件
                String filename = parts[1];
                int filesize = Integer.parseInt(parts[2]);
                int physicalLocation = diskManager.allocate(filesize);
                if (physicalLocation == -1) {
                    return "Not enough disk space.";
                }
                return fileSystem.createFile(filename, filesize, physicalLocation);
            case "deletefile":
                // 删除文件
                filename = parts[1];
                File file = fileSystem.getFile(filename);
                if (file != null) {
                    diskManager.free(file.getPhysicalLocation(), file.getSize());
                }
                return fileSystem.deleteFile(filename);
            case "lsfile":
                // 显示文件信息
                return fileSystem.listFiles();
            case "blockproc":
                // 阻塞进程
                pid = Integer.parseInt(parts[1]);
                return blockProcess(pid);
            case "unblockproc":
                // 唤醒进程
                pid = Integer.parseInt(parts[1]);
                return unblockProcess(pid);
            case "schedule":
                // 进程调度
                return usePriorityScheduling ? prioritySchedule() : rrSchedule();
            case "clear":
                // 清空所有队列和文件系统
                return clearAll();
            default:
                return "Invalid command.";
        }
    }

    //创建进程，并将其添加到就绪队列
    private String createProcess(int runtime, int memory, int priority, int ioStart, int ioStop) {
        if (runtime <= 0) {
            return "Runtime must be greater than 0.";
        }
        if (memory <= 0 || memory > memoryManager.getSize()) {
            return "Memory size must be between 1 and " + memoryManager.getSize() + ".";
        }
        if ((ioStart != -1 && ioStop != -1) && (ioStart >= ioStop || ioStart < 0 || ioStop > runtime)) {
            return "Invalid I/O start and stop times.";
        }
        if (memoryManager.getAvailableMemory() < memory) {
            return "Not enough available memory to create process.";
        }

        PCB process = new PCB(runtime, memory, priority, ioStart, ioStop);
        int address = memoryManager.allocate(memory);
        if (address != -1) {
            process.setMemoryAddress(address);
            processes.add(process);
            readyQueue.add(process);
            schedulingLog.add("Process " + process.getId() + " created and added to READY queue with priority " + priority + ".");
            return "Process " + process.getId() + " created.";
        } else {
            return "Not enough memory to create process.";
        }
    }

    //终止进程
    private String killProcess(int pid) {
        PCB process = null;
        for (PCB p : processes) {
            if (p.getId() == pid) {
                process = p;
                break;
            }
        }
        if (process != null) {
            memoryManager.free(process.getMemoryAddress(), process.getMemory());
            processes.remove(process);
            readyQueue.remove(process);
            blockedQueue.remove(process);
            completedQueue.remove(process);
            if (runningProcess == process) {
                runningProcess = null;
            }
            schedulingLog.add("Process " + pid + " killed.");
            return "Process " + pid + " killed.";
        } else {
            return "Process not found.";
        }
    }

    //将进程阻塞
    private String blockProcess(int pid) {
        PCB process = null;
        for (PCB p : processes) {
            if (p.getId() == pid) {
                process = p;
                break;
            }
        }
        if (process != null && process.getState().equals("READY")) {
            process.block();
            readyQueue.remove(process);
            blockedQueue.add(process);
            schedulingLog.add("Process " + pid + " moved from READY to BLOCKED queue.");
            return "Process " + pid + " blocked.";
        } else {
            return "Process not found or not in READY state.";
        }
    }

    //将进程从阻塞状态唤醒
    private String unblockProcess(int pid) {
        PCB process = null;
        for (PCB p : blockedQueue) {
            if (p.getId() == pid) {
                process = p;
                break;
            }
        }
        if (process != null) {
            process.unblock();
            blockedQueue.remove(process);
            readyQueue.add(process);
            schedulingLog.add("Process " + pid + " moved from BLOCKED to READY queue.");
            return "Process " + pid + " unblocked.";
        } else {
            return "Process not found or not in BLOCKED state.";
        }
    }

    //显示所有进程信息
    private String showProcesses() {
        StringBuilder sb = new StringBuilder();
        for (PCB process : processes) {
            sb.append("PID: ").append(process.getId()).append(", State: ").append(process.getState())
                    .append(", Memory Address: ").append(process.getMemoryAddress())
                    .append(", Priority: ").append(process.getPriority()).append("\n");
        }
        return sb.toString();
    }

    //RR调度算法
    private String rrSchedule() {
        schedulingLog.clear();
        StringBuilder sb = new StringBuilder();
        currentTime += timeQuantum;
        if (runningProcess == null && readyQueue.isEmpty() && blockedQueue.isEmpty()) {
            return "No processes to schedule.";
        }

        Queue<PCB> tempBlockedQueue = new LinkedList<>();
        while (!blockedQueue.isEmpty()) {
            PCB process = blockedQueue.poll();
            if (process.getIoStop() <= currentTime) {
                process.unblock();
                readyQueue.add(process);
                schedulingLog.add("Process " + process.getId() + " moved from BLOCKED to READY.");
                process.setIoStart(-1);
                process.setIoStop(-1);
            } else {
                tempBlockedQueue.add(process);
            }
        }
        blockedQueue = tempBlockedQueue;

        if (runningProcess == null && !readyQueue.isEmpty()) {
            runningProcess = readyQueue.poll();
            runningProcess.setState("RUNNING");
            schedulingLog.add("Process " + runningProcess.getId() + " moved from READY to RUNNING.");
        }

        if (runningProcess != null) {
            runningProcess.run(timeQuantum);

            if (runningProcess.getIoStart() != -1 && runningProcess.getRuntime() <= runningProcess.getIoStart()) {
                runningProcess.setState("BLOCKED");
                blockedQueue.add(runningProcess);
                schedulingLog.add("Process " + runningProcess.getId() + " moved from RUNNING to BLOCKED.");
                runningProcess = null;
            } else if (runningProcess.getRuntime() > 0) {
                runningProcess.setState("READY");
                readyQueue.add(runningProcess);
                schedulingLog.add("Process " + runningProcess.getId() + " moved from RUNNING to READY.");
                runningProcess = readyQueue.poll();
                if (runningProcess != null) {
                    runningProcess.setState("RUNNING");
                    schedulingLog.add("Process " + runningProcess.getId() + " moved from READY to RUNNING.");
                }
            } else {
                runningProcess.resetIoTimes();
                runningProcess.setState("COMPLETED");
                completedQueue.add(runningProcess);
                memoryManager.free(runningProcess.getMemoryAddress(), runningProcess.getMemory());
                processes.remove(runningProcess);
                schedulingLog.add("Process " + runningProcess.getId() + " moved from RUNNING to COMPLETED.");
                runningProcess = readyQueue.poll();
                if (runningProcess != null) {
                    runningProcess.setState("RUNNING");
                    schedulingLog.add("Process " + runningProcess.getId() + " moved from READY to RUNNING.");
                }
            }
        }

        return sb.toString();
    }

    //优先级调度算法
    private String prioritySchedule() {
        schedulingLog.clear();
        StringBuilder sb = new StringBuilder();
        currentTime += timeQuantum;
        if (runningProcess == null && readyQueue.isEmpty() && blockedQueue.isEmpty()) {
            return "No processes to schedule.";
        }

        Queue<PCB> tempBlockedQueue = new LinkedList<>();
        while (!blockedQueue.isEmpty()) {
            PCB process = blockedQueue.poll();
            if (process.getIoStop() <= currentTime) {
                process.unblock();
                readyQueue.add(process);
                schedulingLog.add("Process " + process.getId() + " moved from BLOCKED to READY.");
                process.setIoStart(-1);
                process.setIoStop(-1);
            } else {
                tempBlockedQueue.add(process);
            }
        }
        blockedQueue = tempBlockedQueue;

        if (runningProcess == null && !readyQueue.isEmpty()) {
            runningProcess = readyQueue.stream().max(Comparator.comparingInt(PCB::getPriority)).orElse(null);
            readyQueue.remove(runningProcess);
            runningProcess.setState("RUNNING");
            schedulingLog.add("Process " + runningProcess.getId() + " moved from READY to RUNNING.");
        }

        if (runningProcess != null) {
            runningProcess.run(timeQuantum);

            if (runningProcess.getIoStart() != -1 && runningProcess.getRuntime() <= runningProcess.getIoStart()) {
                runningProcess.setState("BLOCKED");
                blockedQueue.add(runningProcess);
                schedulingLog.add("Process " + runningProcess.getId() + " moved from RUNNING to BLOCKED.");
                runningProcess = null;
            } else if (runningProcess.getRuntime() > 0) {
                runningProcess.setState("READY");
                readyQueue.add(runningProcess);
                schedulingLog.add("Process " + runningProcess.getId() + " moved from RUNNING to READY.");
                runningProcess = readyQueue.stream().max(Comparator.comparingInt(PCB::getPriority)).orElse(null);
                readyQueue.remove(runningProcess);
                if (runningProcess != null) {
                    runningProcess.setState("RUNNING");
                    schedulingLog.add("Process " + runningProcess.getId() + " moved from READY to RUNNING.");
                }
            } else {
                runningProcess.resetIoTimes();
                runningProcess.setState("COMPLETED");
                completedQueue.add(runningProcess);
                memoryManager.free(runningProcess.getMemoryAddress(), runningProcess.getMemory());
                processes.remove(runningProcess);
                schedulingLog.add("Process " + runningProcess.getId() + " moved from RUNNING to COMPLETED.");
                runningProcess = readyQueue.stream().max(Comparator.comparingInt(PCB::getPriority)).orElse(null);
                readyQueue.remove(runningProcess);
                if (runningProcess != null) {
                    runningProcess.setState("RUNNING");
                    schedulingLog.add("Process " + runningProcess.getId() + " moved from READY to RUNNING.");
                }
            }
        }

        return sb.toString();
    }

    //清空所有队列和文件系统
    private String clearAll() {
        readyQueue.clear();
        blockedQueue.clear();
        completedQueue.clear();
        runningProcess = null;
        processes.clear();
        memoryManager = new MemoryManager(memoryManager.getSize());
        diskManager = new DiskManager(diskManager.getSize());
        fileSystem = new FileSystem();
        PCB.resetCounter();
        File.resetCounter();
        return "All queues and file system have been cleared.";
    }
}
