package FileSystem;

// Block是对一个磁盘块的缓冲
public class Block {
//    private int blocksize;
    //默认认为一个磁盘块为512 KB
    private byte[] buffer;

    //---------------------------------------//
    public Block(byte[] buffer){
        assert buffer.length == Config.BlockSize;
        this.buffer = buffer;
    }
}
