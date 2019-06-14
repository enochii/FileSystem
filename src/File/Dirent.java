package File;

// 索引项
public class Dirent{
    public int iNum;
    public String filename;

    public Dirent(int inum, byte[] filename){
        this(inum, new String(filename));
    }

    public Dirent(int inum, String filename){
        this.iNum = inum;
        this.filename = filename;
    }
}
