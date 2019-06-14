package Controller;

// commands: 1.cd    2.rm    3.touch     4.rmdir     5.mkdir
//          6.makefs    7.edit      8.rename
//

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
                MakeFS.main();
                break;
                default:
                    System.err.print("Invalid command!");
        }

        return command;
    }
}

