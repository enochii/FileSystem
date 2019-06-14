package Controller;

// 文件系统的控制器

import File.File;
import FileSystem.FileSystem;

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

    public void main() throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            System.out.print("sch001-fs>");
            String cmd = console.readLine();

            CommandParser.parse(cmd, this).excute(this);
        }

    }

}
