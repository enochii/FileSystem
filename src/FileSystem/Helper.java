package FileSystem;

import java.io.IOException;

public class Helper {
    static public void handleIOE(IOException e, String msg){
        System.err.println(msg);
        e.printStackTrace();
    }
}
