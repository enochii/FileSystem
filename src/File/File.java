package File;

//在这个系统中除了FileSystem外,理论上BlockCache应该是透明的

// 该类定义各种关于文件的操作
// 创建、删除、更新、换名
// 根据绝对路径和相对路径寻找文件

import FileSystem.FileSystem;
import FileSystem.Config;
import FileSystem.Block;

import java.util.List;

// 索引项
class Dirent{
    int iNum;
    String filename;
}

public class File {
    INode iNode;

    public File(INode inode){
        assert inode != null;
        iNode = inode;
    }

//    文件内容
    String content;
//    目录项
    List<Dirent> dirents;

//    获取文件内容
    String getContent(){
        assert iNode!=null && iNode.type == 2;

//        content = "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int bindex = 0;bindex < Config.NDirect - 1;bindex++){
            if(iNode.indexs[bindex] == 0){
                continue; //删除文件可能会使中间留空
            }
            Block block = FileSystem.getInstance().readBlockBybnum(iNode.indexs[bindex]);
//            todo: 我真的不会
            stringBuffer.append(block.getByteBuffer().toString());
        }

        return content = stringBuffer.toString();
    }

//    void setContent(String _content){
//
//    }

//    获取目录项
    List<Dirent> getDirents(int iNum){
        assert iNode!=null && iNode.type == 1;
        return null;
    }

//    创建新文件或者文件夹，返回i节点
    public static INode createFile(byte[] fileName, int type, int father){
        // 创建新文件
        INode child = INode.allocateINode(fileName, type, father);

        // 把新文件加入父目录
        FileSystem fs = FileSystem.getInstance();
        INode ifa = fs.readInode(father);
        int insertIndex;
        for(insertIndex = 0;insertIndex < Config.NDirect - 1;insertIndex++){
            if(ifa.indexs[insertIndex] == 0)break;
        }

        if(insertIndex == Config.NDirect - 1){
            System.err.println("No enough space for a new child in this dir!");
        }
        ifa.indexs[insertIndex] = child.iNum;

        fs.writeInode(ifa);

        return child;
    }

//    更新文件
    public void updateFile(String newContent){
        assert iNode.type == 2;
//        将内容写到对应的磁盘块上

        int totalBytes = newContent.getBytes().length;
        assert totalBytes < (Config.NDirect - 1)*Config.BlockSize;
        int blockNeedtoWrite = FileSystem.blockNum(totalBytes);

        content = newContent;

        FileSystem fs = FileSystem.getInstance();

        for(int bindex = 0; bindex < Config.NDirect - 1;bindex++){
            if(bindex >= blockNeedtoWrite){
                break;
            }
            String substr = newContent.substring(bindex*Config.BlockSize, (bindex+1)*Config.BlockSize);

            if(iNode.indexs[bindex] == 0){
                iNode.indexs[bindex] = fs.ballocate();
            }
            fs.writeBlock(new Block(substr.getBytes(), iNode.indexs[bindex]));
        }
    }

//    删除文件
    public void deleteFile(){
//        清空自己在父节点的i节点位图
//        清空自己占据的磁盘块位图
        FileSystem fs = FileSystem.getInstance();
        INode ifa = fs.readInode(iNode.indexs[Config.NDirect - 1]);

        int iindex;
        for(iindex  = 0;iindex < Config.NDirect - 1;iindex++){
            if(ifa.indexs[iindex]==iNode.iNum){
//                ifa.indexs[iindex] = 0;
                break;
            }
        }
        if(Config.NDirect - 1 == iindex){
            System.err.println("Current deleted file is not in its father's dirents?");
        }
        ifa.indexs[iindex] = 0;
//
        INode.destroyINode(iNode);
    }

//    重命名文件
    public void renameFile(){

    }
}
