# 操作系统第三次作业 文件系统

> 1651162 施程航

## 需求

## 设计

该文件系统借鉴了一点xv6的实现，大致有以下几层：

```shell
------------------------
File Lookup
------------------------
Inodes(Directory and File)
------------------------
Block and Allocator
------------------------
Underlying FileSystem
------------------------
```

以下是对涉及到的一些类的简述：

FileSystem: 底层文件系统，用`RandomAccessFile`进行模拟，在这一层包括了文件系统的布局信息。可以考虑在这里做Block Buffer。

Block: 一块磁盘块的缓冲。

Inode: i节点，存储关于文件的所有信息。与xv6不同的一点是，我们的inode有文件名信息。同样，目录被作为一种特殊的Inode。

Controller: 文件系统启动时的控制中心，指明当前存储命令，且对外提供各种Command。

MakeFS: 文件系统在第一次使用之前需要做一次初始化，也就是磁盘格式化。