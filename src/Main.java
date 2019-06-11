import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import File.INode;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        File file = new File("test.txt");
//        if(!file.exists()){
            file.delete();
            file.createNewFile();
//        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(16);
//        byte[] buffer = new byte[5]
//        int i = 5;
        raf.writeBytes("hello");
        raf.seek(0);
        raf.writeBytes("schsb");
        System.out.println(raf.length());
        byte[] buffer = new byte[64];
        raf.seek(16);
        raf.read(buffer);
        System.out.println(buffer);
        System.out.println(new String(buffer));
//
//        INode iNode = new INode();
//        byte[] buf = iNode.toBytes();
//        System.out.println(buf.length);
    }
}
