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
//    TODO: 修改type域了...
//          再看看这里的public合不合适...
//    INode的类型，1表示目录，2表示文件
    public int type;
    public byte[] fileName = new byte[Config.FileNameLen];
//    i节点编号
    public int iNum;
//    如果是目录则代表i节点编号，文件则代表文件块
//    在这里我们保留了非直接索引的接口，暂未做出实现
    public int[] indexs = new int[Config.NDirect +1];

    public byte[] toBytes(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        byteBuffer.put(fileName);
        byteBuffer.putInt(type);
        byteBuffer.putInt(iNum);
        for(int i=0;i<Config.NDirect+1;i++){
            byteBuffer.putInt(indexs[i]);
        }

        return byteBuffer.array();
    }

//    一般用于读取磁盘的i节点信息后生成一个i节点
    public INode(byte[] fileName, int type, int iNum, int[] indexs){
        this.fileName = fileName;
        this.type = type;
        this.iNum = iNum;
        this.indexs = indexs==null? new int[Config.NDirect+1]:indexs;
    }

//    根据文件名创建一个i节点
//    note: 当前目录的文件名查重不在这里做
    public INode(byte[] fileName){
        this.fileName = fileName;
    }

    static INode allocateINode(byte[] fileName, int type){
        int inum = FileSystem.getInstance().iallocate();

//        todo: 这里估计需要把新申请的inode写回磁盘
        INode nInode =  new INode(fileName,type, inum, null);

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
        if(iNode.type == 1){
            destroyDirINode(iNode);
        }else{
            assert iNode.type == 2;
            destroyFileINode(iNode);
        }
    }

//    删除i节点分两种：文件和目录
//    目录删除基于文件删除
    static void destroyDirINode(INode iNode){
//        目录删除要递归删除该目录下的文件和目录
        FileSystem.getInstance().irelease(iNode.iNum);
        for(int inum = 0;inum<Config.NDirect;inum++){
            if(iNode.indexs[inum] == 0){
                break;
            }
            destroyINode(inum);
        }
    }

    static void destroyFileINode(INode iNode){
        FileSystem.getInstance().irelease(iNode.iNum);
        for(int bnum = 0; bnum < Config.NDirect;bnum++){
//            释放磁盘块
            if(iNode.indexs[bnum] == 0){
                break;
            }
            BlockAllocator.destroyBlock(bnum);
        }
//        这里没实现间接索引，暂时留空释放简介索引块的逻辑
    }
}
