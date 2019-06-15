package FileSystem;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

// Block是对一个磁盘块的缓冲，事实上block是对ByteBuffer的包装
public class Block {
//    private int blocksize;
    //磁盘块大小
    byte[] buffer;
//    ByteBuffer byteBuffer; //缓冲区
    int bnum = -1; // 磁盘块号
    //---------------------------------------//
    public Block(byte[] buffer, int bnum){
//        assert buffer.length == Config.BlockSize;

        byte[] blockBuffer = buffer;
        if(buffer.length < Config.BlockSize){
            blockBuffer = new byte[Config.BlockSize];
            System.arraycopy(buffer, 0, blockBuffer, 0, buffer.length);
        }
        this.buffer = blockBuffer;
        this.bnum = bnum;
    }

//    获取一个新的包装字节数组的缓冲区
    public ByteBuffer getByteBuffer(){
        assert buffer != null;
        return ByteBuffer.wrap(buffer);
    }

    public void setBuffer(byte[] bytes){
        buffer = bytes;
    }

    public int getBnum(){
        return bnum;
    }
}
