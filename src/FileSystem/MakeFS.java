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
    }
}
