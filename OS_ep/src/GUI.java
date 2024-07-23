import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    private JTextField runtimeInput;
    private JTextField memoryInput;
    private JTextField ioStartInput;
    private JTextField ioStopInput;
    private JTextField priorityInput;
    private JTextField fileNameInput;
    private JTextField fileSizeInput;
    private JTextArea outputArea;
    private JTable readyQueueTable;
    private JTable runningQueueTable;
    private JTable blockedQueueTable;
    private JTable completedQueueTable;
    private JTable fileTable;
    private DefaultTableModel readyQueueTableModel;
    private DefaultTableModel runningQueueTableModel;
    private DefaultTableModel blockedQueueTableModel;
    private DefaultTableModel completedQueueTableModel;
    private DefaultTableModel fileTableModel;
    private OS os;
    private JRadioButton rrButton;
    private JRadioButton priorityButton;

    public GUI() {
        os = new OS(1000, 200);

        setTitle("OS 模拟器");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // 左侧面板用于命令输入和按钮
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(new JLabel("运行时间:"), gbc);
        runtimeInput = new JTextField(5);
        gbc.gridx = 1;
        leftPanel.add(runtimeInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        leftPanel.add(new JLabel("内存大小:"), gbc);
        memoryInput = new JTextField(5);
        gbc.gridx = 1;
        leftPanel.add(memoryInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        leftPanel.add(new JLabel("优先级:"), gbc);
        priorityInput = new JTextField(5);
        gbc.gridx = 1;
        leftPanel.add(priorityInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        leftPanel.add(new JLabel("I/O 开始:"), gbc);
        ioStartInput = new JTextField(5);
        gbc.gridx = 1;
        leftPanel.add(ioStartInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        leftPanel.add(new JLabel("I/O 结束:"), gbc);
        ioStopInput = new JTextField(5);
        gbc.gridx = 1;
        leftPanel.add(ioStopInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        leftPanel.add(new JLabel("文件名:"), gbc);
        fileNameInput = new JTextField(10);
        gbc.gridx = 1;
        leftPanel.add(fileNameInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        leftPanel.add(new JLabel("文件大小:"), gbc);
        fileSizeInput = new JTextField(10);
        gbc.gridx = 1;
        leftPanel.add(fileSizeInput, gbc);

        JButton createProcessButton = new JButton("创建进程");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        createProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String runtimeText = runtimeInput.getText();
                String memoryText = memoryInput.getText();
                String priorityText = priorityInput.getText();
                String ioStartText = ioStartInput.getText();
                String ioStopText = ioStopInput.getText();

                int runtime = Integer.parseInt(runtimeText);
                int memory = Integer.parseInt(memoryText);
                int priority = Integer.parseInt(priorityText); // 即使是RR算法，也传递优先级
                int ioStart = ioStartText.isEmpty() ? -1 : Integer.parseInt(ioStartText);
                int ioStop = ioStopText.isEmpty() ? -1 : Integer.parseInt(ioStopText);

                String command = String.format("creatproc %d %d %d %d %d", runtime, memory, priority, ioStart, ioStop);
                String output = os.executeCommand(command);
                outputArea.append(output + "\n");
                clearInputs();
                updateTables();
            }
        });
        leftPanel.add(createProcessButton, gbc);

        JButton killProcessButton = new JButton("终止进程");
        gbc.gridy = 8;
        killProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pidText = JOptionPane.showInputDialog("请输入进程ID:");
                if (pidText != null && !pidText.isEmpty()) {
                    int pid = Integer.parseInt(pidText);
                    String command = String.format("killproc %d", pid);
                    String output = os.executeCommand(command);
                    outputArea.append(output + "\n");
                    updateTables();
                }
            }
        });
        leftPanel.add(killProcessButton, gbc);

        JButton blockProcessButton = new JButton("阻塞进程");
        gbc.gridy = 9;
        blockProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pidText = JOptionPane.showInputDialog("请输入进程ID:");
                if (pidText != null && !pidText.isEmpty()) {
                    int pid = Integer.parseInt(pidText);
                    String command = String.format("blockproc %d", pid);
                    String output = os.executeCommand(command);
                    outputArea.append(output + "\n");
                    updateTables();
                }
            }
        });
        leftPanel.add(blockProcessButton, gbc);

        JButton unblockProcessButton = new JButton("唤醒进程");
        gbc.gridy = 10;
        unblockProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pidText = JOptionPane.showInputDialog("请输入进程ID:");
                if (pidText != null && !pidText.isEmpty()) {
                    int pid = Integer.parseInt(pidText);
                    String command = String.format("unblockproc %d", pid);
                    String output = os.executeCommand(command);
                    outputArea.append(output + "\n");
                    updateTables();
                }
            }
        });
        leftPanel.add(unblockProcessButton, gbc);

        JButton showProcessesButton = new JButton("显示进程状态");
        gbc.gridy = 11;
        showProcessesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String output = os.executeCommand("psproc");
                outputArea.append(output + "\n");
            }
        });
        leftPanel.add(showProcessesButton, gbc);

        JButton showMemoryButton = new JButton("显示内存使用");
        gbc.gridy = 12;
        showMemoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String output = os.executeCommand("mem");
                outputArea.append(output + "\n");
            }
        });
        leftPanel.add(showMemoryButton, gbc);

        JButton createFileButton = new JButton("创建文件");
        gbc.gridy = 13;
        createFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileNameInput.getText();
                int fileSize = Integer.parseInt(fileSizeInput.getText());
                if (!fileName.isEmpty()) {
                    String command = String.format("creatfile %s %d", fileName, fileSize);
                    String output = os.executeCommand(command);
                    outputArea.append(output + "\n");
                    fileNameInput.setText("");
                    fileSizeInput.setText("");
                    updateTables();
                }
            }
        });
        leftPanel.add(createFileButton, gbc);

        JButton deleteFileButton = new JButton("删除文件");
        gbc.gridy = 14;
        deleteFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileNameInput.getText();
                if (!fileName.isEmpty()) {
                    String command = String.format("deletefile %s", fileName);
                    String output = os.executeCommand(command);
                    outputArea.append(output + "\n");
                    fileNameInput.setText("");
                    updateTables();
                } else {
                    outputArea.append("File name cannot be empty.\n");
                }
            }
        });
        leftPanel.add(deleteFileButton, gbc);

        JButton listFilesButton = new JButton("显示文件信息");
        gbc.gridy = 15;
        listFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String output = os.executeCommand("lsfile");
                outputArea.append(output + "\n");
            }
        });
        leftPanel.add(listFilesButton, gbc);

        // 选择调度算法
        rrButton = new JRadioButton("RR调度算法", true);
        priorityButton = new JRadioButton("优先级轮转调度算法");
        ButtonGroup algorithmGroup = new ButtonGroup();
        algorithmGroup.add(rrButton);
        algorithmGroup.add(priorityButton);

        gbc.gridy = 16;
        leftPanel.add(new JLabel("选择调度算法:"), gbc);
        gbc.gridy = 17;
        leftPanel.add(rrButton, gbc);
        gbc.gridy = 18;
        leftPanel.add(priorityButton, gbc);

        JButton scheduleButton = new JButton("进程调度");
        gbc.gridy = 19;
        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                os.setUsePriorityScheduling(priorityButton.isSelected());
                new Thread(() -> {
                    while (!os.getReadyQueue().isEmpty() || os.getRunningProcess() != null || !os.getBlockedQueue().isEmpty()) {
                        String output = os.executeCommand("schedule");
                        SwingUtilities.invokeLater(() -> {
                            outputArea.append(output + "\n");
                            outputArea.append(os.getSchedulingLog().toString() + "\n");  // 添加详细的调度日志信息
                            updateTables();
                        });
                        try {
                            Thread.sleep(2000); // 每2秒调度一次
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        leftPanel.add(scheduleButton, gbc);

        // 添加清空按钮
        JButton clearButton = new JButton("清空");
        gbc.gridy = 20;
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String output = os.executeCommand("clear");
                outputArea.append(output + "\n");
                updateTables();
            }
        });
        leftPanel.add(clearButton, gbc);

        // 输出区域
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("输出信息"));

        // 进程表格
        readyQueueTableModel = new DefaultTableModel(new Object[]{"PID", "运行时长", "内存大小", "优先级", "I/O 开始", "I/O 结束", "状态"}, 0);
        readyQueueTable = new JTable(readyQueueTableModel);
        JScrollPane readyQueueScrollPane = new JScrollPane(readyQueueTable);
        readyQueueScrollPane.setBorder(BorderFactory.createTitledBorder("就绪队列"));

        runningQueueTableModel = new DefaultTableModel(new Object[]{"PID", "运行时长", "内存大小", "优先级", "I/O 开始", "I/O 结束", "状态"}, 0);
        runningQueueTable = new JTable(runningQueueTableModel);
        JScrollPane runningQueueScrollPane = new JScrollPane(runningQueueTable);
        runningQueueScrollPane.setBorder(BorderFactory.createTitledBorder("运行队列"));

        blockedQueueTableModel = new DefaultTableModel(new Object[]{"PID", "运行时长", "内存大小", "优先级", "I/O 开始", "I/O 结束", "状态"}, 0);
        blockedQueueTable = new JTable(blockedQueueTableModel);
        JScrollPane blockedQueueScrollPane = new JScrollPane(blockedQueueTable);
        blockedQueueScrollPane.setBorder(BorderFactory.createTitledBorder("阻塞队列"));

        completedQueueTableModel = new DefaultTableModel(new Object[]{"PID", "运行时长", "内存大小", "优先级", "I/O 开始", "I/O 结束", "状态"}, 0);
        completedQueueTable = new JTable(completedQueueTableModel);
        JScrollPane completedQueueScrollPane = new JScrollPane(completedQueueTable);
        completedQueueScrollPane.setBorder(BorderFactory.createTitledBorder("完成队列"));

        // 文件表格
        fileTableModel = new DefaultTableModel(new Object[]{"ID", "文件名", "物理位置"}, 0);
        fileTable = new JTable(fileTableModel);
        JScrollPane fileScrollPane = new JScrollPane(fileTable);
        fileScrollPane.setBorder(BorderFactory.createTitledBorder("文件系统"));

        // 表格布局
        JPanel tablePanel = new JPanel(new GridLayout(2, 3, 10, 10));
        tablePanel.add(readyQueueScrollPane);
        tablePanel.add(runningQueueScrollPane);
        tablePanel.add(blockedQueueScrollPane);
        tablePanel.add(completedQueueScrollPane);
        tablePanel.add(fileScrollPane);
        tablePanel.add(outputScrollPane);

        // 添加面板到主框架
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tablePanel);
        splitPane.setResizeWeight(0.3);
        add(splitPane, BorderLayout.CENTER);

        pack();
    }

    private void updateTables() {
        // 更新进程表格
        readyQueueTableModel.setRowCount(0);
        runningQueueTableModel.setRowCount(0);
        blockedQueueTableModel.setRowCount(0);
        completedQueueTableModel.setRowCount(0);

        for (PCB process : os.getReadyQueue()) {
            Object[] rowData = {process.getId(), process.getRuntime(), process.getMemory(), process.getPriority(), process.getIoStart(), process.getIoStop(), process.getState()};
            readyQueueTableModel.addRow(rowData);
        }

        PCB runningProcess = os.getRunningProcess();
        if (runningProcess != null) {
            Object[] rowData = {runningProcess.getId(), runningProcess.getRuntime(), runningProcess.getMemory(), runningProcess.getPriority(), runningProcess.getIoStart(), runningProcess.getIoStop(), runningProcess.getState()};
            runningQueueTableModel.addRow(rowData);
        }

        for (PCB process : os.getBlockedQueue()) {
            Object[] rowData = {process.getId(), process.getRuntime(), process.getMemory(), process.getPriority(), process.getIoStart(), process.getIoStop(), process.getState()};
            blockedQueueTableModel.addRow(rowData);
        }

        for (PCB process : os.getCompletedProcesses()) {
            Object[] rowData = {process.getId(), process.getRuntime(), process.getMemory(), process.getPriority(), process.getIoStart(), process.getIoStop(), process.getState()};
            completedQueueTableModel.addRow(rowData);
        }

        // 更新文件表格
        fileTableModel.setRowCount(0);
        for (File file : os.getFiles()) {
            Object[] rowData = {file.getId(), file.getName(), file.getPhysicalLocation()};
            fileTableModel.addRow(rowData);
        }
    }

    private void clearInputs() {
        runtimeInput.setText("");
        memoryInput.setText("");
        priorityInput.setText("");
        ioStartInput.setText("");
        ioStopInput.setText("");
        fileNameInput.setText("");
        fileSizeInput.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
}
