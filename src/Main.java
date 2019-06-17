import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import Controller.Controller;
import FSView.EditView;
import FSView.MainView;
import File.INode;
import FileSystem.Config;
import FileSystem.FileSystem;

import javax.swing.*;

public class Main {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                FileSystem fs = FileSystem.getInstance();
                if(fs == null){
                    System.out.println("看起来不需要保存呢！");
                    return;
                }
                System.out.println("安全退出！");
                fs.safeExit();
            }
        }));
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
//        System.out.println("Hello World!");
//        File file = new File("test.txt");
////        if(!file.exists()){
//            file.delete();
//            file.createNewFile();
////        }
//        RandomAccessFile raf = new RandomAccessFile(file, "rw");
//        raf.seek(16);
////        byte[] buffer = new byte[5]
////        int i = 5;
//        raf.writeBytes("hello");
//        raf.seek(0);
//        raf.writeBytes("schsb");
//        System.out.println(raf.length());
//        byte[] buffer = new byte[64];
//        raf.seek(16);
//        raf.read(buffer);
//        System.out.println(buffer);
//        System.out.println(new String(buffer));
//
////
//        byte[] xi = new byte[20];
//        for (int i = 0;i<20;i++){
//            xi[i] = 'h';
//        }
//        String str = new String(xi);

        System.out.println("Please choose the mode you want(1. Cmd 2. UI):");
        Scanner in = new Scanner(System.in);
        int mode = in.nextInt();

        if(mode == 1){
            Controller controller = new Controller();
            controller.main();
            return;
        }

//        EventQueue.invokeLater(() -> {
//                JFrame.setDefaultLookAndFeelDecorated(true);

            MainView mainView = new MainView();
            mainView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//            System.out.println("再见！");

//        });
    }

};
