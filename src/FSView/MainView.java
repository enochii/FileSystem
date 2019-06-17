package FSView;

import Controller.Controller;
import File.Dirent;
import Controller.PathLookup;
import File.File;
import FileSystem.FileSystem;
import FileSystem.MakeFS;
import Controller.Command;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import Controller.Command.*;

// 主界面
public class MainView extends JFrame{

    private SPopupMenu sPopupMenu = null;
    private JButton goUp; // 上一级目录
    private JTextField addressField;
    private JButton goTo; //进入该目录

    private Controller controller;

    private JPanel contentPanel;

    public Controller getController(){
        return controller;
    }
    public MainView(){
        goUp = new JButton("👆");
        addressField = new JTextField("root");
        goTo = new JButton("👉");
        controller = new Controller();

        goUp.addActionListener(actionEvent -> {
            goUp();
        });

        goTo.addActionListener(actionEvent -> {
            goTo();
        });

//        contentPanel = new JPanel();

        initToolPanel();
        initContentPanel();
        initScroll();

        initMainView();
        addRightClickListener();
        updateDirents();
    }

//    返回父目录
    private void goUp(){
        controller.curDir = new File(PathLookup.getFather(controller.curDir));
        updateDirents();
    }

//    去到地址栏指定的目录
    private void goTo(){
        String path = addressField.getText();
        System.out.println(path);
        controller.curDir = new File(PathLookup.pathLookup(controller.curDir, path));
        updateDirents();
    }

//    初始化主界面的基本设置
    private void initMainView(){
        this.setTitle("Sch001's File System!");
        this.setSize(ViewConfig.WINDOW_WIDTH, ViewConfig.WINDOW_HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.WHITE);


        this.setVisible(true); // sb...

    }

    private void initContentPanel(){
        // 左侧对齐的流式布局
        contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentPanel.setBounds(0,20, 750 + 0, 1000 + 20);
        contentPanel.setBackground(Color.WHITE);
//        contentPanel.setLayout(new GridLayout());
        this.add(contentPanel);
    }

    private void initScroll(){
        // 移除所有components
        this.contentPanel.removeAll();

        // 设置背景色
        this.contentPanel.setBackground(Color.WHITE);

        // 为工具栏添加监听事件
        this.contentPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                System.out.println("resize");

                Dimension d = MainView.this.contentPanel.getPreferredSize();
                int totalNum = MainView.this.contentPanel.getComponents().length;
                int col = ViewConfig.WINDOW_WIDTH
                        / (ViewConfig.FILE_ICON_PANEL_SIZE + 5);
                int row = totalNum / col + 1;
                // 根据显示文件的总数量重置panel的高度
                int newHeight = row * (ViewConfig.FILE_ICON_PANEL_SIZE + 5) + 5;
                d.height = newHeight;
                d.width = ViewConfig.WINDOW_WIDTH;
                MainView.this.contentPanel.setPreferredSize(d);
            }
        });

        //        TEST
//        for(int i=0;i<15;i++){
//            contentPanel.add(new FileView(null, null));
//        }
//
//
//        contentPanel.revalidate();
//        contentPanel.repaint();

//        pack();
//        contentPanel.setMinimumSize(new Dimension(ViewConfig.WINDOW_WIDTH, 100));
//        contentPanel.setMaximumSize(new Dimension(ViewConfig.WINDOW_WIDTH, 100));
//        contentPanel.setPreferredSize(new Dimension(ViewConfig.WINDOW_WIDTH, 100));


//        加个滚动条
        JScrollPane contentScrollPane = new JScrollPane(this.contentPanel);
        contentScrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // 添加到主界面
        this.add(contentScrollPane, BorderLayout.CENTER);
    }

//    工具栏，包括上级目录按钮和地址栏
    void initToolPanel(){
        JPanel _toolPanel = new JPanel();

        _toolPanel.setBackground(Color.decode("#B2EBF2"));
        // 设置布局
        _toolPanel.setLayout(new BoxLayout(_toolPanel, BoxLayout.X_AXIS));
        _toolPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 添加按鈕、地址栏到工具栏
        _toolPanel.add(goUp);
        _toolPanel.add(addressField);
        _toolPanel.add(goTo);

        this.add(_toolPanel, BorderLayout.PAGE_START);
    }

//    命名空间...
//    更新目录项
    public void updateDirents(){
        java.util.List <Dirent> dirents = controller.curDir.getDirents();

        contentPanel.removeAll();
        for (int i=0;i<dirents.size();i++){
            contentPanel.add(new FileView(dirents.get(i), this));
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    void addRightClickListener(){

        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                // 右键
                if(me.getButton() == MouseEvent.BUTTON3){
                    System.out.println("一记右勾拳！");
                    sPopupMenu = new SPopupMenu();
                    // 格式化/上一级目录/新建文件/新建目录
                    sPopupMenu.addMenuItem("上一级目录(Go Up)", new CommandActionListener(controller){
                        @Override
                        public void actionPerformed(ActionEvent event){
                            goUp();
                        }
                    });
                    sPopupMenu.addMenuItem("格式化(Format!)",new CommandActionListener(controller){
                        @Override
                        public void actionPerformed(ActionEvent event){
                            MakeFS.main();
                            controller.curDir = new File(FileSystem.getInstance().getRoot());
                            updateDirents();
                        }
                    });

                    // 新建文件
                    sPopupMenu.addMenuItem("新建文件(New File)", new CommandActionListener(controller){
                        @Override
                        public void actionPerformed(ActionEvent event){
                            newFile(File.FILE);
                        }
                    });

                    sPopupMenu.addMenuItem("新建目录(New Directory)", new CommandActionListener(controller){
                        @Override
                        public void actionPerformed(ActionEvent event){
                            newFile(File.DIR);
                        }
                    });

                    sPopupMenu.menu.show(me.getComponent(),me.getX(),me.getY());
                }

            }
        });
    }
//    新建文件/目录
    private void newFile(int type){
        if(type != File.DIR && type != File.FILE){
            System.err.println("Invalid File Type!");
        }
        String fileType = type == File.FILE? "file":"directory";

        String filename = (String)JOptionPane.showInputDialog(this,
                "Enter your "+ fileType + " name", "New "+ fileType,
                        JOptionPane.WARNING_MESSAGE);
        if(filename == null){
            return;
        }

        File.createFile(filename.getBytes(), type, controller.curDir.getINode());

        controller.curDir.updateInode();
        updateDirents();
    }
}
