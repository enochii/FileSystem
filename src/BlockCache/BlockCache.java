package BlockCache;

// 暂未实现
// todo: 这是对磁盘块缓冲层的模拟，这部分的逻辑应该嵌在FileSystem的读写磁盘块中

//note: 读写磁盘块都会经过这一层,不过只要调用方改变了磁盘块内容,
// 都需要显示调用writeBlock或者writeInode
// 注意到writeInode调用了writeBlock

// 每次读磁盘都会先读到缓冲
//申请新的i节点需要写一次磁盘?(allocateInode)

// 1.读写命中则直接读写Cache的块
// 2.读写不命中
// 3.

public class BlockCache {

}
