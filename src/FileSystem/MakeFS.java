package FileSystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

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

//        留空首块，一般作为引导块（虽然在我们的系统中不需要）
//        第一块作为超级块存放
//        第二块开始作为Inode的位图，紧跟磁盘块的位图
    }
}
