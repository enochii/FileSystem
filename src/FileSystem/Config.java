package FileSystem;

// 这里描述文件系统的基本配置
public class Config {
    //每个磁盘块的大小，字节为单位
    public final static int BlockSize = 1024;
    // i节点的数量，代表文件的最大数量
    public final static int TotalINodeNum = 1024;
    // 磁盘块的总数
    public final static int TotalBlockNum = 1024 * 16;
}
