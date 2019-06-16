package FileSystem;

import java.io.IOException;

public class Helper {
    static public void handleIOE(IOException e, String msg){
        System.err.println(msg);
        e.printStackTrace();
    }
//    todo: 地址转换接口 inum/bnum ->  disk address


    static public void assertion(boolean condition, String msg){
        if(!condition){
            System.err.println(msg);
        }
    }
}
