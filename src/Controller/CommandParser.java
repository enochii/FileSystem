package Controller;

// commands: 1.cd    2.rm    3.touch     4.rmdir     5.mkdir
//          6.makefs    7.edit      8.rename
//

import File.File;
import FileSystem.FileSystem;
import FileSystem.MakeFS;

public class CommandParser {
    public static Command parse(String cmd, Controller controller){
        String[] words = cmd.split(" ");
        Command command = null;

        switch (words[0]){
            case "cd":
                break;
            case "ls":
                command = new ls();
                break;
            case "touch":
                command = new touch(words[1]);
                break;
            case "mkdir":
                command = new mkdir(words[1]);
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
                FileSystem.getInstance().safeExit();
                command = new printInfo("Exit...\n");
                break;

            case "append":
            case "help":
                command = new printInfo(getHelp());
                break;
                default:
                    command = new printInfo("Invalid command! You can type \"help\" to get more information.\n");
        }

        return command;
    }

    private static String getHelp(){
        return "help message:\n" +
                "";
    }
}

