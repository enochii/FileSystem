package File;

import FileSystem.Block;
import FileSystem.FileSystem;

// 磁盘块的分配器
public class BlockAllocator {
    public static Block allocateBlock(){
        int bnum = FileSystem.getInstance().ballocate();

        return new Block(null, bnum);
    }

    public static void destroyBlock(Block block){
        destroyBlock(block.getBnum());
    }

    public static void destroyBlock(int bnum){
        FileSystem.getInstance().brelease(bnum);
    }
}
