package FileSystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import File.INode;

public class MakeFS {
    public static void main() throws IOException {
        File image = new File("fs.iso");
        if(image.isFile()){
            //  删除文件
            image.delete();
            assert !image.isFile();
        }
        image.createNewFile();

        SuperBlock superBlock = new SuperBlock();
        boolean[] iNodeBitmap = new boolean[superBlock.totalINodeNum];
        boolean[] blockBitmap = new boolean[superBlock.totalBlockNum];
        Arrays.fill(iNodeBitmap, false);
        Arrays.fill(blockBitmap, false);

//        需要创建一个根目录
//        int[] indexs = new int[Config.NDirect];
//        byte[] rootName = new String("root").getBytes();
        INode root = new INode("root".getBytes(), 1, 0, -1);
        FileSystem fs = FileSystem.getInstance();
//        写入第一个i节点作为根目录
        fs.writeInode(root);

        InitFS(superBlock, iNodeBitmap, blockBitmap, null);

        //todo: 这部分还少了初始化位图的逻辑

//        留空首块，一般作为引导块（虽然在我们的系统中不需要）
//        第一块作为超级块存放
//        第二块开始作为Inode的位图，紧跟磁盘块的位图
    }

//    这部分逻辑在格式化和退出文件夹可以复用
    public static void InitFS(SuperBlock sb, boolean[] iNodeBitmap, boolean[] blockBitmap, INode root){
        FileSystem fs = FileSystem.getInstance();

        Block block = new Block(sb.toBytes(), 1);
        fs.writeBlock(block);
//        写入位图
        fs.writeInodeBitmap(iNodeBitmap);
        fs.writeBlockBitmap(blockBitmap);
    }
}
