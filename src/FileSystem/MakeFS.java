package FileSystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import File.INode;
import File.File.*;

public class MakeFS {
    public static void main(){
//        File image = new File("fs.iso");
//        if(image.isFile()){
//            //  删除文件
//            image.delete();
//            assert !image.isFile();
//        }
//        image.createNewFile();

        SuperBlock superBlock = new SuperBlock();
        boolean[] iNodeBitmap = new boolean[superBlock.totalINodeNum];
        boolean[] blockBitmap = new boolean[superBlock.totalBlockNum];
        Arrays.fill(iNodeBitmap, false);
        Arrays.fill(blockBitmap, false);

//        需要创建一个根目录
//        int[] indexs = new int[Config.NDirect];
//        byte[] rootName = new String("root").getBytes();

//        初始化静态成员
        FileSystem.initFS();
        FileSystem.superBlock = superBlock;

        INode root = new INode("root".getBytes(), 1, 0, -1000000);
//        FileSystem fs = FileSystem.getInstance();
//        写入第一个i节点作为根目录
        FileSystem.writeInode(root);

        // 填i节点位图
        iNodeBitmap[0] = true;

        // 填磁盘块位图
//        留空首块，一般作为引导块（虽然在我们的系统中不需要）
//        第一块作为超级块存放
//        第二块开始作为Inode的位图，紧跟磁盘块的位图
        int blockUsed = 1+1+FileSystem.blockNum(Config.TotalINodeNum )+FileSystem.blockNum(Config.TotalBlockNum )
                + FileSystem.blockNum(Config.InodeSize*Config.TotalINodeNum);
        for(int bnum = 0;bnum < blockUsed; bnum++){
            blockBitmap[bnum] = true;
        }


        FileSystem.ReWriteFS(superBlock, iNodeBitmap, blockBitmap, null);

//        System.out.println("root".getBytes());
//        System.out.println("Root: " + FileSystem.getInstance().getRoot().filename);
    }


}
