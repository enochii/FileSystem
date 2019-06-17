package File;

import FileSystem.Config;
import FileSystem.FileSystem;

import java.nio.ByteBuffer;

/*
    todo: 是否有必要再做一个dInode层
          父节点可以放在indexs数组的最后一个位置
*/

//关于一个Inode在磁盘上的存储：一个int一字节，这里的具体实现是文件名+iNum+indexs = 64 bytes

//一个iNode会记录一个文件的信息，这里的一个文件可以是文件夹或者真实的文件
//这里还要做一手inode分配的逻辑
public class INode {
//     再看看这里的public合不合适...
//    INode的类型，1表示目录，2表示文件
    public int type;
    public byte[] filename = new byte[Config.FileNameLen];
//    i节点编号
    public int iNum;
//    如果是目录则代表i节点编号，文件则代表文件块
//    在这里我们保留了非直接索引的接口，暂未做出实现
    public int[] indexs = new int[Config.NDirect +1];

    public byte[] toBytes(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(Config.InodeSize);
        byteBuffer.put(filename);
        if(Config.FileNameLen > filename.length){
            byteBuffer.put(new byte[Config.FileNameLen - filename.length]);
        }
        byteBuffer.putInt(type);
        byteBuffer.putInt(iNum);
        for(int i=0;i<Config.NDirect+1;i++){
            byteBuffer.putInt(indexs[i]);
        }

        return byteBuffer.array();
    }

//    需要调用i节点的构造函数：
//    1. 从磁盘上读取一个i节点信息
//    2. 新申请一个i节点

//    一般用于读取磁盘的i节点信息后生成一个i节点
    public INode(byte[] filename, int type, int inum, int[] indexs){
        this(filename, type, inum);

        assert indexs != null;
        this.indexs = indexs;
//        this.indexs[Config.NDirect - 1] = father;
    }

    private INode(byte[] filename, int type, int inum){
//        System.out.println("Filename Length: "+ filename.length);
        for(int i = 0;i<Config.FileNameLen;i++){
            if(i<filename.length){
                this.filename[i] = filename[i];
            }else{
                this.filename[i] = 0;
            }
        }
//        this.filename = filename;
        this.type = type;
        this.iNum = inum;
    }

//    根据文件名创建一个i节点
//    note: 当前目录的文件名查重不在这里做
    public INode(byte[] filename, int type, int inum, int father){
        this(filename, type, inum);
        this.indexs = new int[Config.NDirect + 1];
//        最后一个节点用于存储父节点
        this.indexs[Config.NDirect -1] = father;
    }

//    这里关于i节点的逻辑都没有处理父节点的indexs，我们留到高层做
//    也就是create/delete File
    public static INode allocateINode(byte[] filename, int type,  int father){
        int inum = FileSystem.getInstance().iallocate();

//        todo: 这里估计需要把新申请的inode写回磁盘
        INode nInode =  new INode(filename, type,inum, father);

        FileSystem.getInstance().writeInode(nInode);
        return nInode;
    }

//    重载版本
    static void destroyINode(int inum){
        destroyINode(FileSystem.getInstance().readInode(inum));
    }
//    根据type域分发
    static void destroyINode(INode iNode){
//        目录
        if(iNode.type == File.DIR){
            destroyDirINode(iNode);
        }else{
            assert iNode.type == File.FILE;
            destroyFileINode(iNode);
        }
        FileSystem.getInstance().irelease(iNode.iNum);
    }

//    删除i节点分两种：文件和目录
//    目录删除基于文件删除
    private static void destroyDirINode(INode iNode){
//        目录删除要递归删除该目录下的文件和目录
        FileSystem.getInstance().irelease(iNode.iNum);
        for(int iindex = 0;iindex<Config.NDirect - 1;iindex++){
            if(iNode.indexs[iindex] == 0){
                continue;
            }

            destroyINode(iNode.indexs[iindex]);
            //这里好像也可以用懒删除，只需要消除位图就可以了...
            iNode.indexs[iindex] = 0;
        }
        iNode.indexs[Config.NDirect - 1] = 0;
    }

    private static void destroyFileINode(INode iNode){
        FileSystem.getInstance().irelease(iNode.iNum);
        for(int bindex = 0; bindex < Config.NDirect;bindex++){
//            释放磁盘块
            if(iNode.indexs[bindex] == 0){
                continue;
            }
            BlockAllocator.destroyBlock(iNode.indexs[bindex]);
        }
//        这里没实现间接索引，暂时留空释放间接索引块的逻辑
    }
}
