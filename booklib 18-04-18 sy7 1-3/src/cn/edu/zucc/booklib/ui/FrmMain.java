package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import cn.edu.zucc.booklib.control.SystemUserManager;

public class FrmMain extends JFrame implements ActionListener {
	private JMenuBar menubar=new JMenuBar(); ;
    private JMenu menu_Manager=new JMenu("ϵͳ����");
    private JMenu menu_lend=new JMenu("���Ĺ���");
    private JMenu menu_search=new JMenu("��ѯͳ��");
    private JMenuItem  menuItem_UserManager=new JMenuItem("�û�����");
    private JMenuItem  menuItem_ReaderTypeManager=new JMenuItem("����������");
    private JMenuItem  menuItem_ReaderManager=new JMenuItem("���߹���");
    private JMenuItem  menuItem_PublisherManager=new JMenuItem("���������");
    private JMenuItem  menuItem_BookManager=new JMenuItem("ͼ�����");
    
    private JMenuItem  menuItem_Lend=new JMenuItem("����");
    private JMenuItem  menuItem_Return=new JMenuItem("����");
    
    private JMenuItem  menuItem_BookLendSearch=new JMenuItem("ͼ����������ѯ");
    private JMenuItem  menuItem_ReaderLendSearch=new JMenuItem("���߽��������ѯ");
    private JMenuItem  menuItem_BookLendStatic=new JMenuItem("ͼ��������ͳ��");
    private JMenuItem  menuItem_ReaderLendStatic=new JMenuItem("���߽������ͳ��");
    
    
	private FrmLogin dlgLogin=null;
	private JPanel statusBar = new JPanel();
	public FrmMain(){
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setTitle("ͼ�����ϵͳ");
		dlgLogin=new FrmLogin(this,"��½",true);
		dlgLogin.setVisible(true);
	    //�˵�
	    if("����Ա".equals(SystemUserManager.currentUser.getUsertype())){
	    	menu_Manager.add(menuItem_UserManager);
	    	menuItem_UserManager.addActionListener(this);
	    	menu_Manager.add(menuItem_ReaderTypeManager);
	    	menuItem_ReaderTypeManager.addActionListener(this);
	    	menu_Manager.add(menuItem_ReaderManager);
	    	menuItem_ReaderManager.addActionListener(this);
	    	menu_Manager.add(menuItem_PublisherManager);
	    	menuItem_PublisherManager.addActionListener(this);
	    	menu_Manager.add(menuItem_BookManager);
	    	menuItem_BookManager.addActionListener(this);
	    	menubar.add(menu_Manager);
	    }
	    menu_lend.add(this.menuItem_Lend);
	    menuItem_Lend.addActionListener(this);
	    menu_lend.add(this.menuItem_Return);
	    menuItem_Return.addActionListener(this);
	    menubar.add(menu_lend);
	    menu_search.add(this.menuItem_BookLendSearch);
	    menuItem_BookLendSearch.addActionListener(this);
	    menu_search.add(this.menuItem_ReaderLendSearch);
	    menuItem_ReaderLendSearch.addActionListener(this);
	    menu_search.add(this.menuItem_BookLendStatic);
	    menuItem_BookLendStatic.addActionListener(this);
	    menu_search.add(this.menuItem_ReaderLendStatic);
	    menuItem_ReaderLendStatic.addActionListener(this);
	    menubar.add(this.menu_search);
	    this.setJMenuBar(menubar);
	    //״̬��
	    statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
	    JLabel label=new JLabel("����!"+SystemUserManager.currentUser.getUsername());
	    statusBar.add(label);
	    this.getContentPane().add(statusBar,BorderLayout.SOUTH);
	    this.addWindowListener(new WindowAdapter(){   
	    	public void windowClosing(WindowEvent e){ 
	    		System.exit(0);
             }
        });
	    this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==this.menuItem_UserManager){
			FrmUserManager dlg=new FrmUserManager(this,"�û�����",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_ReaderTypeManager){
			FrmReaderTypeManager dlg=new FrmReaderTypeManager(this,"����������",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_ReaderManager){
			FrmReaderManager dlg=new FrmReaderManager(this,"���߹���",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_PublisherManager){
			FrmPublisherManager dlg=new FrmPublisherManager(this,"���������",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_BookManager){
			FrmBookManager dlg=new FrmBookManager(this,"ͼ�����",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_Lend){
			FrmLend dlg=new FrmLend(this,"����",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_Return){
			FrmReturn dlg=new FrmReturn(this,"�黹",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_BookLendSearch){
			FrmBookLendSearch dlg=new FrmBookLendSearch(this,"ͼ����������ѯ",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_ReaderLendSearch){
			FrmReaderLendSearch dlg=new FrmReaderLendSearch(this,"���߽��������ѯ",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_BookLendStatic){
			FrmBookLendStatic dlg=new FrmBookLendStatic(this,"ͼ�����ͳ��",true);
			dlg.setVisible(true);
		}
		else if(e.getSource()==this.menuItem_ReaderLendStatic){
			FrmReaderLendStatic dlg=new FrmReaderLendStatic(this,"���߽���ͳ��",true);
			dlg.setVisible(true);
		}
	}
}
