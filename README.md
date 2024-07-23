# Time-sharing-system-OS-simulation-program
## 实验要求

设计一个OS模拟程序，要求：

（1）程序运行后提供一个交互界面或命令窗口，允许用户输入以下命令并可以对命令进行解释执行，

  creatproc命令:提交作业命令，要求用户提供作业估计运行时长、内存大小。如果作业执行期间有I/O操作，需要提供该作业I/O操作的起始停止时间。如果该作业执行期间没有I/O操作，则该项参数为-1，-1。该命令的解释执行过程为对该作业创建对应的进程，完成PCB建立、存储空间分配等工作。（即执行OS的创建进程原语）

  killproc  命令:终止进程命令。该命令的解释执行过程为执行进程撤销原语。killproc命令格式：killproc [进程号]

psproc命令:显示所有进程状态命令。该命令的解释执行过程为显示出所有进程的状态信息，主要包括进程id，进程状态，存储空间地址。

mem命令:显示内存空间使用情况信息。该命令的解释执行过程为显示内存空间的占用和空闲情况。

creatfile命令：创建文件命令。该命令的解释执行过程为执行创建文件原语。creatfile命令格式：creatfile  [文件名] 

deletefile命令：删除文件命令。该命令的解释执行过程为执行删除文件原语。creatfile命令格式：creatfile  [文件名]

lsfile命令：显示文件信息命令。该命令的解释执行过程为显示文件的FCB信息，主要显示文件id，文件名，文件物理位置（即盘块号）。

（2）设计思路提示：内存空间和磁盘外存空间可用数组模拟，进程管理需要设计相应的函数模拟实现进程管理原语，包括创建进程，终止进程，阻塞进程，唤醒进程，进程调度等原语。进程调度算法选择RR。内存分配可选择可变分区策略或页式内存分配方案中任意一种。外存分配可选择连续分配、链式分配或索引分配任一种。

（3）程序设计语言不限。

## 运行结果

**1.最终设计的更可视化的方式**

![image](https://github.com/user-attachments/assets/151db9b6-dcb4-440f-b019-b7817b854761)

**2.创建进程、显示进程状态、显示内存使用、创建文件等**

![image](https://github.com/user-attachments/assets/c2a29b62-fde7-494b-9066-e99b5699f7e1)

**3.终止进程、阻塞进程、唤醒进程**

<img width="434" alt="image" src="https://github.com/user-attachments/assets/ad1cd704-e441-433c-becd-1329922c2062">


