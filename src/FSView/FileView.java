package FSView;

import Controller.Controller;
import File.File;
import File.INode;
import File.Dirent;
import FileSystem.FileSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// 一个Dirent的视图
public class FileView extends JPanel {
//    底层人民
//    private File file;
    public Dirent dirent;
    private MainView mainView;

    private SPopupMenu sPopupMenu = null;//延时创建

    private JLabel mainLabel;

    public FileView(Dirent _dirent, MainView mainView){
        super();
        this.mainView = mainView;
//        TEST
        if(_dirent == null){
            _dirent = new Dirent(0, "wdnmd", File.DIR);
        }

        this.dirent = _dirent;
        this.setSize(new Dimension(ViewConfig.FILE_ICON_PANEL_SIZE,
                ViewConfig.FILE_ICON_PANEL_SIZE));
        this.setPreferredSize(new Dimension(ViewConfig.FILE_ICON_PANEL_SIZE,
                ViewConfig.FILE_ICON_PANEL_SIZE));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);

        initIcon();

//        文件拥有鼠标监听器
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {

                // 双击
                if(me.getClickCount() == 2){
                    System.out.println("一记二连击！");
                }

                // 右键
                if(me.getButton() == MouseEvent.BUTTON3){
                    System.out.println("一记右勾拳！");

                    // 第一次需要用到菜单再加载就Ok了
                    if(sPopupMenu == null){
                        sPopupMenu = new SPopupMenu();
                        // 加菜单项
                        sPopupMenu.addMenuItem("打开(Open)", new CommandActionListener(dirent.iNum, mainView.getController()){
                            @Override
                            public void actionPerformed(ActionEvent event){
                                File openFile = new File(FileSystem.getInstance().readInode(inum));

                                int fileType = openFile.getINode().type;
                                if(fileType == File.DIR){
//                                    java.util.List<Dirent> direntList = openFile.getDirents();
                                    controller.curDir = openFile;
                                    mainView.updateDirents();
                                }else if(fileType == File.FILE){
                                    String content = openFile.getContent();
                                    // todo: 弹出一个EditView????
                                    System.out.println(content);
                                }
                            }
                        });
                        sPopupMenu.addMenuItem("删除(Delete)", new CommandActionListener(dirent.iNum, mainView.getController()){
                            @Override
                            public void actionPerformed(ActionEvent event){
                                File deleteFile = new File(FileSystem.getInstance().readInode(inum));

                                deleteFile.deleteFile();
                                mainView.updateDirents();
                            }
                        });
                        sPopupMenu.addMenuItem("重命名(Rename)", new CommandActionListener(dirent.iNum, mainView.getController()){
                            @Override
                            public void actionPerformed(ActionEvent event){
                                File file = new File(FileSystem.getInstance().readInode(inum));

                                //todo: 弹出EditView...
                                file.renameFile("..");
                            }
                        });
                    }

                    sPopupMenu.menu.show(me.getComponent(),me.getX(),me.getY());
                }
            }
        });
    }

    void test(){
        System.out.println(dirent.filename+"........");
    }

    void initIcon(){
        String img;
//        INode inode = file.getINode();
        switch (dirent.type){
            case File.FILE:
                img = "resource/file.jpg";
                break;
            case File.DIR:
                img ="resource/dir.jpg";
                break;
                default:
                    img = "resource/word.jpg";
                    System.out.println("Invalid File type");
        }

//        System.out.println(new java.io.File("resource/file.jpg").exists());

        ImageIcon icon = new ImageIcon(img);
        icon.setImage(icon.getImage().getScaledInstance(ViewConfig.FILE_ICON_SIZE,
                ViewConfig.FILE_ICON_SIZE, Image.SCALE_DEFAULT));

        mainLabel = new JLabel(new String(dirent.filename), icon, JLabel.CENTER);
        mainLabel.setBorder(BorderFactory.createEmptyBorder(0,
                (ViewConfig.FILE_ICON_PANEL_SIZE - ViewConfig.FILE_ICON_SIZE) / 2, 0,
                (ViewConfig.FILE_ICON_PANEL_SIZE - ViewConfig.FILE_ICON_SIZE) / 2));
        mainLabel.setVerticalTextPosition(JLabel.BOTTOM);
        mainLabel.setHorizontalTextPosition(JLabel.CENTER);

        // 添加到文件栏
        this.add(mainLabel);

    }

    void setSelected(boolean selected){
        if(selected){
            this.setBackground(Color.LIGHT_GRAY);
        }else{
            this.setBackground(Color.WHITE);
        }
    }

    public void onMouseClicked(MouseEvent mouseEvent){

    }
}
