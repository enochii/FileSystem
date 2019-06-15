package File;

// 索引项
public class Dirent{
    public int iNum;
    public String filename;

    public Dirent(int inum, byte[] filename){
        String oriName = new String(filename);

        int emptyStart = 0;
        for(;emptyStart < filename.length;emptyStart++){
            if(filename[emptyStart] == 0){
                break;
            }
        }
        for(int i = emptyStart;i<filename.length;i++){
            if(filename[i]!=0){
                System.err.println("Invalid File Name!");
            }
        }
        oriName = oriName.substring(0, emptyStart);

        this.iNum = inum;
        this.filename = oriName;
    }

    public Dirent(int inum, String filename){
        this.iNum = inum;
        this.filename = filename;
    }
}
