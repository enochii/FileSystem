package FSView;

import Controller.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// 命令行和界面的区别大概是：用户提供的标识文件的字段是文件名还是FileView包含的Dirent

public class CommandActionListener implements ActionListener{
    public int inum  = -1;
    public Controller controller = null;

    public CommandActionListener(int inum, Controller controller){
        this.inum = inum;
        this.controller = controller;
    }

    public CommandActionListener(Controller controller){
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent event){

    }
}
