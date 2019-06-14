package FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
//import java.lang.reflect.Array;
import java.nio.ByteBuffer;
//import java.util.Arrays;

import File.INode;

/*
    当文件系统工作时，超级块和位图常驻内存，i节点信息和数据块要通过读镜像文件的对应磁盘块
    来获取对应的信息。
    退出磁盘时需要调用saveExit()写回磁盘
    这里还没有实现磁盘块的cache，但保留了实现的接口，详情见writeBlock和readBlock
 */

// 文件系统布局如下
/*
    \ 超级块 | i节点位图 | 磁盘块位图 | i节点数组 | 数据块 |
 */
//这部分填写文件系统在磁盘镜像文件上的逻辑，提供访问数据块/i节点的接口等

// 说明：1.int在此为4个byte
//       2.i节点编号和磁盘块均从0开始，且i节点0被分配为根目录，磁盘块0，1分别为留空块和超级块

public class FileSystem {
//    镜像文件
    private static File image;
//    通过storage来访问镜像文件
    private static RandomAccessFile storage;
//    全局的单例对象
    private static FileSystem instance = new FileSystem();

//    final int inodeStart; // i节点的开始偏移
//    final int blockStart; // 磁盘块的开始偏移

//    对外开放的接口
    static public FileSystem getInstance(){
        assert instance != null;
        return instance;
    }

//    存储元信息
    private static SuperBlock superBlock;

    private boolean[] iNodeBitmap;
    private boolean[] blockBitmap;

    static void initFS(){
        image  = new File("fs.iso");
        try {
            storage = new RandomAccessFile(image, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    private FileSystem(boolean mkFS){
//        initFS();
//        superBlock = new SuperBlock();
//    }

    private FileSystem() {
        initFS();

//        位图，0表示空闲，1表示已经被占据
//        TODO: 设置allocatedBlockNum（好像不用）
//              这里的逻辑感觉
        iNodeBitmap = readInodeBitmap();
        blockBitmap = readBlockBitmap();
        superBlock = readSuperBlock();
//        计算偏移
//        inodeStart = Config.BlockSize * 2 + uptoBlockSize(superBlock.totalINodeNum) + uptoBlockSize(superBlock.totalBlockNum);
//        blockStart = inodeStart + uptoBlockSize(superBlock.totalINodeNum * Config.InodeSize);
    }

    public INode getRoot(){
        return readInode(0);
    }

//    遍历i节点位图，返回第一个空闲i节点编号
    public int iallocate(){
        for(int inum=0;inum<Config.TotalINodeNum;inum++){
            if(!iNodeBitmap[inum]){
                iNodeBitmap[inum] = true;
                return inum;
            }
        }
        System.err.println("No Inode left!");
        return -1;
    }

//    修改i节点的位，懒删除
    public void irelease(int inum){
        assert inum >= 0 && inum < iNodeBitmap.length && iNodeBitmap[inum];
        iNodeBitmap[inum] = false;
    }

//    磁盘块的分配和释放
    public int ballocate(){
        for(int bnum=0;bnum<Config.TotalBlockNum;bnum++){
            if(!blockBitmap[bnum]){
                blockBitmap[bnum] = true;
                if(superBlock.allocatedBlockNum <= bnum){
//                TODO: 在这里要申请物理磁盘块

                    superBlock.allocatedBlockNum ++;
                    System.out.println("Please implement me!");
                }
                return bnum;
            }
        }
        System.err.println("No Block left!");
        return -1;
    }

    public void brelease(int bnum){
        assert bnum >= 0 && bnum < Config.TotalBlockNum && blockBitmap[bnum] && bnum < superBlock.allocatedBlockNum;
        blockBitmap[bnum] = false;
    }

    private SuperBlock readSuperBlock(){
        SuperBlock sb = new SuperBlock();
//        跳过留空的引导块
        Block block = readOneBlock(Config.BlockSize);
        ByteBuffer byteBuffer = block.getByteBuffer();

        sb.totalINodeNum = byteBuffer.getInt();
        sb.totalBlockNum = byteBuffer.getInt();
        sb.allocatedBlockNum = byteBuffer.getInt();

        sb.iBitmapStart = byteBuffer.getInt();
        sb.bBitmapStart = byteBuffer.getInt();
        sb.iNodeStart = byteBuffer.getInt();
        sb.blockStart = byteBuffer.getInt();


        return sb;
    }

//    dbq，这里读取位图时都没有照着前面说的“要先读出一个磁盘块”，而是直接读RandomAccessFile
//    原因是byteBuffer不支持直接读boolean，后面再看看

//    这里我们把一个boolean当成一个byte读入...
//    当然也可以为了节省空间做点位运算，这就是时空权衡了..
    boolean[] readInodeBitmap(){
        boolean[] inodeBitmap = new boolean[superBlock.totalINodeNum];
//        跳过两个块就开始读
        try {
            storage.seek(Config.BlockSize * superBlock.iNodeStart );
        } catch (IOException e) {
            Helper.handleIOE(e, "Can not seek the address of Inode Bitmap!");
        }

        int inum = -1;
        try{
            for(inum = 0;inum < superBlock.totalINodeNum; inum++) {
                blockBitmap[inum] = storage.readBoolean();
            }
        }catch (IOException e){
            Helper.handleIOE(e, "Can not read "+ inum +"th Inode bit.");
        }

        return inodeBitmap;
    }

//    写位图的逻辑，要写的东西也太多了...
    private static void writeInodeBitmap(boolean[] iBitmap){
        try {
            storage.seek(Config.BlockSize * superBlock.iNodeStart );
        } catch (IOException e) {
            Helper.handleIOE(e, "Can not seek the address of Inode Bitmap!");
        }

        assert superBlock.totalINodeNum == iBitmap.length;
        int inum = -1;
        try{
            for(inum = 0;inum < superBlock.totalINodeNum; inum++) {
                storage.writeBoolean(iBitmap[inum]);
            }
        }catch (IOException e){
            Helper.handleIOE(e, "Can not write "+ inum +"th Inode bit.");
        }
    }

    private static void writeBlockBitmap(boolean[] bBitmap){
        try {
            storage.seek(Config.BlockSize * superBlock.blockStart );
        } catch (IOException e) {
            Helper.handleIOE(e, "Can not seek the address of Block Bitmap!");
        }

        assert superBlock.totalBlockNum == bBitmap.length;
        int bnum = -1;
        try{
            for(bnum = 0;bnum < superBlock.totalBlockNum; bnum++) {
                storage.writeBoolean(bBitmap[bnum]);
            }
        }catch (IOException e){
            Helper.handleIOE(e, "Can not write "+ bnum +"th Block bit.");
        }
    }

//
    boolean[] readBlockBitmap(){
        boolean[] blockBitmap = new boolean[superBlock.totalINodeNum];
//        跳过两个块和i节点位图
        try {
            storage.seek(superBlock.bBitmapStart);
        } catch (IOException e) {
            Helper.handleIOE(e, "Can not read Block Bitmap!");
        }

        int bnum = -1;
        try{
            for(bnum = 0;bnum < superBlock.totalBlockNum;bnum++) {
                blockBitmap[bnum] = storage.readBoolean();
            }
        }catch (IOException e){
            Helper.handleIOE(e, "Can not read "+ bnum +"th Block bit.");
        }

        return blockBitmap;
    }

//    先从Cache尝试读取，不命中再从镜像（磁盘）文件读入
    public Block readOneBlock(int pos){
//    TODO: 这里还可以再做一手缓冲磁盘块的逻辑
        return _readOneBlock(pos);
    }

    public Block readBlockBybnum(int bnum){
        return readOneBlock((bnum + superBlock.blockStart) * Config.BlockSize);
    }

//    读出一个磁盘块
//    在每次读出文件上的信息时，我们一次最少读出一个磁盘块
    private static final  Block _readOneBlock(int pos){
//        TODO: 考虑地址对齐的逻辑
        assert pos % Config.BlockSize == 0;
        pos -= pos % Config.BlockSize;
        try {
            storage.seek(pos);
        } catch (IOException e) {
            Helper.handleIOE(e, "Can not seek the address" + pos);
        }

        byte[] buf = new byte[Config.BlockSize];
        try{
            storage.read(buf);
        }catch (IOException e){
            Helper.handleIOE(e, "Can not read the Block at the address" + pos);
        }

        return new Block(buf, pos / Config.BlockSize);
    }

//    计算存下sz个字节需要多少个磁盘块
    public static int blockNum(int sz){
        return (sz - 1 + Config.BlockSize) / Config.BlockSize;
    }

    public static int uptoBlockSize(int sz){
        return blockNum(sz) * Config.BlockSize;
    }

//    写一个磁盘块
    public static void writeBlock(Block block){
        _writeBlock(block);
    }

    private static void _writeBlock(Block block){
        int pos = superBlock.blockStart * Config.BlockSize + block.bnum * Config.BlockSize;
        assert pos % Config.BlockSize == 0;
        try {
            storage.seek(pos);
        } catch (IOException e) {
            Helper.handleIOE(e, "Can not seek to write back block");
        }
        try {
            storage.write(block.buffer);
        } catch (IOException e) {
            Helper.handleIOE(e, "Can not write back Block" + block.bnum);
        }
    }

/*    TODO: 退出时把内存中文件系统的信息写进磁盘
            记得在用户强制关闭窗口也要调用safeExit
  */
    public void safeExit(){
        FileSystem.ReWriteFS(superBlock, iNodeBitmap, blockBitmap, null);
    }

//    读取特定编号的Inode
    public INode readInode(int inum){
        assert inum < superBlock.totalINodeNum;

//        该i节点在磁盘上第一个i节点地址的偏移量
        int offset = inum * Config.InodeSize;
        int pos = superBlock.iNodeStart * Config.BlockSize + offset;
        Block block = readOneBlock(pos);

        ByteBuffer byteBuffer = block.getByteBuffer();

//        done :这里有问题，没有定位i节点 -- ok了
        // file name(16), iNum(4), indexs(4*11)
        offset = offset % Config.BlockSize; //修改偏移量为当前单个磁盘块的位移

        int type = byteBuffer.getInt(offset + Config.FileNameLen);
        int iNum = byteBuffer.getInt(offset + Config.FileNameLen + Config.IntSize);

        int[] indexs = new int[Config.NDirect + 1];
        for(int i = 0;i < Config.NDirect + 1;i++){
            indexs[i] = byteBuffer.getInt(offset + Config.FileNameLen + (i+2)*Config.IntSize);
        }
        byte[] filename = new byte[Config.FileNameLen];
        for(int i=0;i<byteBuffer.array().length;i++){
            filename[i] = byteBuffer.array()[i + offset];
        }

        return new INode(filename, type, iNum, indexs);
    }

//    todo: 写入i节点，这里就算写入一个i节点也要写入一个磁盘块
//          大概这就是为啥需要加个BlockCache层的原因吧...
    public void writeInode(INode inode){
        System.err.println("Please do me!");

        int offset = inode.iNum * Config.InodeSize;
        int pos = superBlock.iNodeStart * Config.BlockSize + offset;
        Block block = readOneBlock(pos);

        pos = pos % Config.BlockSize;
//        修改该block的内容
        byte[] bytes = block.getByteBuffer().array();
        byte[] inodeBytes = inode.toBytes();
        for(int i = 0;i < Config.InodeSize;i++){
            bytes[i + pos] = inodeBytes[i];
        }
        block.setBuffer(bytes);
//        写回磁盘
        FileSystem.writeBlock(block);
    }


//    定位
    private void saveSeek(int pos, String errMsg){
        try {
            storage.seek(pos);
        } catch (IOException e) {
            Helper.handleIOE(e, errMsg);
        }
    }

    //    这部分逻辑在格式化和退出文件夹可以复用
    static void ReWriteFS(SuperBlock sb, boolean[] iNodeBitmap, boolean[] blockBitmap, INode root){

        Block block = new Block(sb.toBytes(), 1);
        writeBlock(block);
//        写入位图
        writeInodeBitmap(iNodeBitmap);
        writeBlockBitmap(blockBitmap);
    }
}

class SuperBlock{
    int totalINodeNum ;
    int totalBlockNum ;
    //todo: 加入以下元信息
//    int dataBlockNum; // 数据块的块数

//    以下均为磁盘块号
    int iBitmapStart;
    int bBitmapStart;
    int iNodeStart;
    int blockStart;

//    文件系统中已经分配的物理磁盘块，这些物理磁盘块可能已经被某些文件占据或者占据后释放
//    这里我们规定一个磁盘块被文件释放后，只是把对应的位图置0，物理上并不做真实释放
    int allocatedBlockNum;

    SuperBlock(){
        totalBlockNum = Config.TotalBlockNum;
        totalINodeNum = Config.TotalINodeNum;
        allocatedBlockNum = 0; // 总感觉这个字段是不需要的- -
        iBitmapStart = 2;
        bBitmapStart = FileSystem.blockNum(Config.TotalINodeNum) + iBitmapStart;
        iNodeStart = bBitmapStart + FileSystem.blockNum(Config.TotalBlockNum);
        blockStart = iNodeStart + FileSystem.blockNum(Config.InodeSize * Config.TotalINodeNum);
    }
//    int isFormatted;

//    转化为字节数组
    byte[] toBytes(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(Config.BlockSize);
        byteBuffer.putInt(totalINodeNum);
        byteBuffer.putInt(totalBlockNum);
        byteBuffer.putInt(allocatedBlockNum);

        byteBuffer.putInt(iBitmapStart);
        byteBuffer.putInt(bBitmapStart);
        byteBuffer.putInt(iNodeStart);
        byteBuffer.putInt(blockStart);

        return byteBuffer.array();
    }

//
}