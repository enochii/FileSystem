package Controller;

// 提供路径到i节点/File的转换,包括相对路径和绝对路径

import File.File;
import File.INode;
import FileSystem.FileSystem;
import FileSystem.Config;
import File.Dirent;

import java.util.List;


public class PathLookup {
    public static String getSeparator(){
        return "/";
    }

    public static String[] splitPathname(String pathname){
        return pathname.split(getSeparator());
    }

//    在dir目录下找出文件名为filename的文件
    public static Dirent lookupInDir(String filename, File dir){
        assert dir.getINode().type == 1;

        List<Dirent> dirents = dir.getDirents();
        for(int i = 0;i<dirents.size();i++){
            Dirent dirent = dirents.get(i);
//            System.out.println(dirent.filename);
            if(filename.equals(dirent.filename)){
                return dirent;
            }
        }

        System.err.println("Can not find " + filename);
        return null;
    }

//    todo: 这里还少了".."和比较直观的相对路径
//    './'表示相对路径,'root/xxx/'表示绝对路径.在我们的系统中根目录为root
    public static INode pathLookup(File curDir, String _path){
        String[] path = splitPathname(_path);
//        File startDir = null;
//        assert path[0].equals(".") || path[0].equals("root");

        if(path[0].equals("..")){
            if(path.length > 1){
                System.out.println("Ignore the part after \"..\"");
            }
            return getFather(curDir);
        }

        if(path[0].equals("root")){
            curDir = new File(FileSystem.getInstance().getRoot());
        }

        int startIndex = 1;
        if(!path[0].equals(".")&&!path[0].equals("root")){
            startIndex = 0;
        }

        assert curDir != null;
        for(int i = startIndex;i<path.length;i++){
            Dirent dirent = lookupInDir(path[i], curDir);
            INode inode = FileSystem.getInstance().readInode(dirent.iNum);
//            文件需作为路径的最后一个分割符出现
            if(inode.type == File.FILE && i != path.length - 1){
                System.err.println("Invalid Path, "+inode.filename + "is not a dir");
            }
            curDir = new File(inode);
        }

        return curDir.getINode();
    }

//    获取父目录
//    这里一个比较hack的想法是把["..", faInum]存入dirents数组，但是有点...
    public static INode getFather(File curDir){
        int ifa = curDir.getINode().indexs[Config.NDirect - 1];
        return FileSystem.getInstance().readInode(ifa);
    }
}
