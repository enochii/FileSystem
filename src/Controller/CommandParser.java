package Controller;

// commands: 1.cd    2.rm    3.touch     4.rmdir     5.mkdir
//          6.makefs    7.edit      8.rename
//

import File.File;
import FileSystem.FileSystem;
import FileSystem.MakeFS;

public class CommandParser {
    public static Command parse(String cmd, Controller controller){
        String[] words = cmd.split(" ",3);
        Command command = null;

        switch (words[0]){
            case "cd":
                command = new cd(words[1]);
                break;
            case "ls":
                command = new ls();
                break;
            case "touch":
                command = new touch(words[1]);
                break;
            case "mkdir":
                command = new mkdir(words[1]);
                break;
            case "mkfs":
                command = new mkfs();
                break;
            case "view":
                command = new view(words[1]);
                break;
            case "echo":
                command = new echo(words[1], words[2]);
                break;
            case "rmdir":
            case "rm":
                command = new rm(words[1]);
                break;
            case "exit":
//                FileSystem.getInstance().safeExit();
                command = new printInfo("Exit...\n", printInfo.EXIT);
                break;


            case "rename":
                command = new rename(words[1], words[2]);
                break;
            case "help":
                command = new printInfo(getHelp(), 0);
                break;
                default:
                    command = new printInfo("Invalid command! You can type \"help\" to get more information.\n", 0);
        }

        return command;
    }

    private static String getHelp(){
        return "---------------------------------------------------------------------\n"
                + "**Help Message**\n"
                + "ls: show all files in current directory.\n"
                + "touch [dir]: new a file\n"
                + "mkdir [dir]: new a dir\n" //
                + "rm [file/dir]: rm a dir/file\n"
                + "echo [file] [content]\n"
                + "mkfs: format and rebuild the file system. Please be careful...\n"
                + "view [file]: view the content of a file.\n"
                + "exit: exit the file system\n"
                + "cd [dir]: change the current dir\n"
                + "rename [file] [newname]: rename file with newname\n"
                + "help: print this message again...\n"
                + "---------------------------------------------------------------------\n"
                ;
    }
}

