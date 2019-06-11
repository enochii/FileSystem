package FileSystem;

// 这里描述文件系统的基本配置
public class Config {
    //每个磁盘块的大小，字节为单位
    public final static int BlockSize = 1024;
    // i节点的数量，代表文件的最大数量
    public final static int TotalINodeNum = 1024;
    // 磁盘块的总数
    public final static int TotalBlockNum = 1024 * 16;

//    文件名的最大长度
    public final static int FileNameLen = 16;
//    文件直接索引数
    public final static int NDirect = 10;
//    Inode的大小
    public final static int InodeSize  = FileNameLen + (1+NDirect+1)* 4;

    public final static int IntSize = 4;
}
