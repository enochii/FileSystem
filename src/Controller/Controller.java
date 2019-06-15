package Controller;

// 文件系统的控制器

import File.File;
import FileSystem.FileSystem;
import FileSystem.MakeFS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {

    public final static boolean CMD = true;
    public final static boolean UI = false;

    // todo: 搞一个UI的member
    FileSystem fs;
    File curDir; // 用户当前所处的目录
    boolean mode = CMD;

    public Controller(){
//        MakeFS.main();

        curDir = new File(FileSystem.getInstance().getRoot());
//        System.out.println("Root: "+ new String(curDir.getINode().filename));
    }

    public void main() throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            System.out.print("schell> ");
            String cmd = console.readLine();

            Command command = CommandParser.parse(cmd, this);
            command.excute(this);

            curDir.updateInode();

            if(command.getClass() == printInfo.class){
                printInfo pcmd = (printInfo)command;
                if(pcmd.type == printInfo.EXIT){
                    break;
                }
            }

        }

//        保存文件系统元信息
        FileSystem.getInstance().safeExit();

    }

}
