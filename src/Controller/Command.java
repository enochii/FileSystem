package Controller;

import File.File;

import java.util.List;
import File.Dirent;

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
            for(int i = 0;i<dirents.size();i++){
                System.out.println(dirents.get(i).filename + " ");
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
        File.createFile(dirname.getBytes(),File.FILE,controller.curDir.getINode());
    }
}