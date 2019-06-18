package File;

//在这个系统中除了FileSystem外,理论上BlockCache应该是透明的

// 该类定义各种关于文件的操作
// 创建、删除、更新、换名
// 根据绝对路径和相对路径寻找文件

import FileSystem.FileSystem;
import FileSystem.Config;
import FileSystem.Block;
import FileSystem.Helper;

import java.util.ArrayList;
import java.util.List;


public class File {

    public final static int DIR = 1;
    public final static int FILE = 2;


    INode iNode;

    public File(INode inode){
        iNode = inode;
    }

    public INode getINode(){
        return iNode;
    }
    private void setiNode(INode inode){
        this.iNode = inode;
    }

    public void updateInode(){
        setiNode(FileSystem.getInstance().readInode(iNode.iNum));
    }

//    由于刷新问题暂时没做缓存文件内容和目录的缓存
//    文件内容
//    String content;
//    目录项
//    List<Dirent> dirents;

//    获取文件内容
    public String getContent(){
        Helper.assertion(iNode!=null && iNode.type == FILE, iNode.filename + "is not a File!");

//        content = "";
        StringBuilder stringBuffer = new StringBuilder();
        for (int bindex = 0;bindex < Config.NDirect - 1;bindex++){
            if(iNode.indexs[bindex] < FileSystem.getDataBlockStart()){
                break; // 有磁盘块为空后面没内容了应该
            }
            Block block = FileSystem.getInstance().readBlockBybnum(iNode.indexs[bindex]);
//            System.out.println("Block Index " + iNode.indexs[bindex] + new String(block.getByteBuffer().array()));
//
            stringBuffer.append(new String(block.getByteBuffer().array()));
        }

        return stringBuffer.toString();
    }

//    void setContent(String _content){
//
//    }

//    获取目录项
    public List<Dirent> getDirents(){
        Helper.assertion(iNode!=null && iNode.type == DIR, new String(iNode.filename) + " is not a Directory!");
//        System.out.println(iNode.iNum+" "+new String(iNode.filename));

        List<Dirent> dirents = new ArrayList<Dirent>();
        for(int i=0;i<Config.NDirect - 1;i++){
            if(iNode.indexs[i] == 0)continue;
//            TEST
//            System.out.println(i+" "+iNode.indexs[i]);
            INode inode = FileSystem.getInstance().readInode(iNode.indexs[i]);
            if(inode==null)continue;//懒删除的文件？
            dirents.add(new Dirent(iNode.indexs[i], inode.filename, inode.type));
        }

        return dirents;
    }

    public static INode createFile(byte[] filename, int type, INode ifa){
        return createFile(filename, type, ifa.iNum);
    }

//    创建新文件或者文件夹，返回i节点
    public static INode createFile(byte[] filename, int type, int father){

        // 把新文件加入父目录
        FileSystem fs = FileSystem.getInstance();
        INode ifa = fs.readInode(father);

        List<Dirent> dirents = new File(ifa).getDirents();
        for(Dirent dirent: dirents){
            if(dirent.filename.equals(new String(filename))){
//                文件重名
                return null;
            }
        }

        // 创建新文件
        INode child = INode.allocateINode(filename, type, father);

        int insertIndex;
        for(insertIndex = 0;insertIndex < Config.NDirect - 1;insertIndex++){
            if(ifa.indexs[insertIndex] == 0)break;
        }

        if(insertIndex == Config.NDirect - 1){
            System.err.println("No enough space for a new child in this dir!");
        }
        ifa.indexs[insertIndex] = child.iNum;

//        TEST
//        System.out.println("Child " + child.iNum + "Inserted in " + insertIndex + "of "+father);

        fs.writeInode(ifa);
//        System.out.println("Create file in " + ifa.iNum);

        return child;
    }

//    更新文件
    public void updateFile(String newContent){
        assert iNode.type == 2;
//        将内容写到对应的磁盘块上

        int totalBytes = newContent.getBytes().length;
        assert totalBytes < (Config.NDirect - 1)*Config.BlockSize;
        int blockNeedtoWrite = FileSystem.blockNum(totalBytes);

//        content = newContent;

        FileSystem fs = FileSystem.getInstance();

        for(int bindex = 0; bindex < Config.NDirect - 1;bindex++){
            if(bindex >= blockNeedtoWrite){
                break;
            }
            int end = Math.min(totalBytes, (bindex+1)*Config.BlockSize);
            String substr = newContent.substring(bindex*Config.BlockSize, end);


            if(iNode.indexs[bindex] == 0){
                iNode.indexs[bindex] = fs.ballocate();
//                System.out.println(iNode.indexs[bindex]);
            }
            fs.writeBlock(new Block(substr.getBytes(), iNode.indexs[bindex]));
//            System.out.println(substr + iNode.indexs[bindex]);
            fs.writeInode(iNode);
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

//    todo: 重命名文件
    public void renameFile(String string){
        iNode.filename = string.getBytes();
        FileSystem.getInstance().writeInode(iNode);
    }
}
