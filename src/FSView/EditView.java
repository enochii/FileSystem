package FSView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import File.File;

public class EditView extends JFrame implements DocumentListener {
    private JTextPane textPane;//文本内容

    private JMenu exitMenu;// 退出菜单
    private File file;
    private boolean edited  = false;//是否已经修改
    private boolean save = false;//是否保存

    private String filename;

    public EditView(File file){
        super();
        filename = new String(file.getINode().filename);
        setTitle(filename);
        this.setSize(ViewConfig.WINDOW_WIDTH, ViewConfig.WINDOW_HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.WHITE);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.file = file;
        textPane = new JTextPane();
        textPane.setText(file.getContent());
        textPane.getDocument().addDocumentListener(this);
        this.add(textPane, BorderLayout.CENTER);

        addMenu();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                onClosing();
            }
            @Override
            public void windowClosed(WindowEvent windowEvent){
                onClosed();
            }
        });
    }

    void addMenu(){
        JMenuBar menuBar = new JMenuBar();
        exitMenu = new JMenu("Exit");
        exitMenu.setMnemonic(KeyEvent.VK_K);// --


        exitMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent menuEvent) {
                onClosing();
                onClosed();
            }

            @Override
            public void menuDeselected(MenuEvent menuEvent) {

            }

            @Override
            public void menuCanceled(MenuEvent menuEvent) {

            }
        });

        menuBar.add(exitMenu);
        this.setJMenuBar(menuBar);
    }

//    关闭窗口两步走
//    代码复用
   private void onClosing(){
        // 无修改时可以直接退出
        if(!edited){
            this.dispose();
            return;
        }
        // 保存修改/不保存/取消退出
        int result = JOptionPane.showConfirmDialog(this,
                "Do you want save the change?", "Exit",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == 0 || result == 1) {
            // 退出 0：保存 | 1：不保存
            save = result==0;
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dispose();
        } else {
            // 取消
            System.out.println("取消");
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }

    }

    private void onClosed(){
        if (edited && save) {
            System.out.println("保存");
            file.updateFile(textPane.getText());
            file.updateInode();
        }
    }

    //DocumentListener的接口
    // 监听文件
    public void insertUpdate(DocumentEvent e) {
        edited = true;
        System.out.println("插入");
        setTitle(filename + " ⚫Edited");
    }

    public void removeUpdate(DocumentEvent e) {
        edited = true;
        System.out.println("删除");
        setTitle(filename + " ⚫Edited");
    }

    public void changedUpdate(DocumentEvent e) {
        System.out.println("修改");
    }
}