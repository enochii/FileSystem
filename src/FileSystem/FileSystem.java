package FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
//import java.lang.reflect.Array;
import java.util.Arrays;

// 文件系统布局如下
/*
    \ 超级块 | i节点位图 | 磁盘块位图 | i节点数组 | 数据块 |
 */
//这部分填写文件系统在磁盘镜像文件上的逻辑，提供访问数据块/i节点的接口等
public class FileSystem {
//    镜像文件
    private static File image;
//    通过storage来访问镜像文件
    private static RandomAccessFile storage;
//    全局的单例对象
    private static FileSystem instance = new FileSystem();

//    对外开放的接口
    static public FileSystem getInstance(){
        return instance;
    }

//    存储元信息
    private SuperBlock superBlock;

    private boolean[] iNodeBitmap;
    private boolean[] blockBitmap;

    private FileSystem() {
        image  = new File("fs.iso");
        try {
            storage = new RandomAccessFile(image, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        位图，0表示空闲，1表示已经被占据
//        TODO: 读取磁盘superBlock信息，并且设置allocatedBlockNum
        iNodeBitmap = new boolean[Config.TotalINodeNum];
        blockBitmap = new boolean[Config.TotalBlockNum];
        Arrays.fill(iNodeBitmap, false);
        Arrays.fill(blockBitmap,false);

    }

//    返回空闲i节点编号
    int iget(){
        for(int inum=0;inum<Config.TotalINodeNum;inum++){
            if(!iNodeBitmap[inum]){
                iNodeBitmap[inum] = true;
                return inum;
            }
        }
        System.err.println("No Inode left!");
        return -1;
    }

//    修改位图
    void irelease(int inum){
        assert inum >= 0 && inum < iNodeBitmap.length && iNodeBitmap[inum];
        iNodeBitmap[inum] = false;
    }

//    磁盘块的分配和释放
    int ballocate(){
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

    void brelease(int bnum){
        assert bnum >= 0 && bnum < Config.TotalBlockNum && blockBitmap[bnum] && bnum < superBlock.allocatedBlockNum;
        blockBitmap[bnum] = false;
    }
}

class SuperBlock{
    int totalINodeNum ;
    int totalBlockNum ;
//    文件系统中已经分配的物理磁盘块，这些物理磁盘块可能已经被某些文件占据或者占据后释放
//    这里我们规定一个磁盘块被文件释放后，只是把对应的位图置0，物理上并不做真实释放
    int allocatedBlockNum;

    SuperBlock(){
        totalBlockNum = Config.TotalBlockNum;
        totalINodeNum = Config.TotalINodeNum;
        allocatedBlockNum = 0;
    }
//    int isFormatted;
}