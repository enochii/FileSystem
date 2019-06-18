package FSView;

// 点击文件/目录弹出菜单，用户可以选择
//     打开/关闭/修改内容(目录没有)/重命名/删除

// 点击空白处弹出（格式化/新建文件（夹）一类的）菜单？

import File.File;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SPopupMenu {
    JPopupMenu menu;
//    File selectedFile; // 选中的文件或目录

    SPopupMenu(){
//        selectedFile = file;
        menu = new JPopupMenu();
//        addMenuItem("Test", new CommandActionListener(null){
//            public void actionPerformed(ActionEvent e){
//                System.out.println("我错了");
//            }
//        });
    }

//    加入一个菜单项
    public void addMenuItem(String string, ActionListener listener){
        JMenuItem element = new JMenuItem(string);
        element.addActionListener(listener);
        menu.add(element);
    }
}
