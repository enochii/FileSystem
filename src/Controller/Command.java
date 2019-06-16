package Controller;

import File.File;

import java.util.List;
import File.Dirent;
import FileSystem.FileSystem;
import FileSystem.MakeFS;
import File.INode;

public interface Command {
    public void excute(Controller controller);
}

class ls implements Command{

    @Override
    public void excute(Controller controller){
        File dir = controller.curDir;
        assert dir != null;

        List<Dirent> dirents = dir.getDirents();

        if(controller.mode == Controller.CMD){
//            System.out.println(dirents.size());
            for(int i = 0;i<dirents.size();i++){
                Dirent dirent = dirents.get(i);
//                在目录后面打印(dir)--
                System.out.print(dirent.filename + (dirent.type == File.DIR? "(dir) ":" "));
            }
            if(dirents.size()>0){
                System.out.println();
            }
        }
    }

}

class touch implements Command{
    String filename;
    public touch(String filename){
        this.filename = filename;
    }

    @Override
    public void excute(Controller controller){
        File.createFile(filename.getBytes(),File.FILE,controller.curDir.getINode());
    }
}

class mkdir implements Command{
    String dirname;

    public mkdir(String dirname){
        this.dirname = dirname;
    }
    @Override
    public void excute(Controller controller){
        assert dirname != null;
        File.createFile(dirname.getBytes(),File.DIR,controller.curDir.getINode());
    }
}

class mkfs implements Command{
    @Override
    public void excute(Controller controller){
        MakeFS.main();
//      重置根目录
        controller.curDir = new File(FileSystem.getInstance().getRoot());
    }
}

// 该命令会覆盖文件所有的内容
class echo implements Command{
    String file;
    String cont;
    public echo(String filename, String content){
        file = filename;
        cont = content;
    }

    @Override
    public void excute(Controller controller){
        File echoFile = FileHelper.findFile(controller, file);
        echoFile.updateFile(cont);
    }
}

class view implements Command{
    String file;
    public view(String filename){
        file = filename;
    }

    @Override
    public void excute(Controller controller){

        File viewFile = FileHelper.findFile(controller, file);
//        TEST
//        for(int i=0;i<inode.indexs.length;i++){
//            System.out.print(inode.indexs[i] + " ");
//        }

        System.out.println(file+": "+ viewFile.getContent());
    }
}

class rm implements Command{
    String filename;
    public rm(String filename){
        this.filename = filename;
    }
    @Override
    public void excute(Controller controller){
        File rmFile = FileHelper.findFile(controller, filename);

        rmFile.deleteFile();
    }

}

// 打印帮助/出错信息
class printInfo implements Command{
//    可能需要进一步做处理的命令
    final static int FILE_NOT_FOUND = 1;
    final static int FILE_ALREADY_EXIST = 2;
    final static int EXIT = 3;

    String msg;
    int type;

    printInfo(String info, int cmdType){
        type = cmdType;
        msg = info;
    }

    @Override
    public void excute(Controller controller){
        System.out.print(msg);
    }
}

class cd implements Command{
    String path;
    cd(String path){
        this.path = path;
    }

    @Override
    public void excute(Controller controller){
        INode newDir = PathLookup.pathLookup(controller.curDir, path);
        controller.curDir = new File(newDir);
    }
}

class FileHelper{
    // helper
    static File findFile(Controller controller, String filename){
        File curDir = controller.curDir;
        Dirent dirent = PathLookup.lookupInDir(filename, curDir);

        INode inode = FileSystem.getInstance().readInode(dirent.iNum);
        File file = new File(inode);

        return file;
    }
}