package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.edu.zucc.booklib.control.BookManager;
import cn.edu.zucc.booklib.control.PublisherManager;
import cn.edu.zucc.booklib.control.ReaderManager;
import cn.edu.zucc.booklib.control.SystemUserManager;
import cn.edu.zucc.booklib.model.BeanBook;
import cn.edu.zucc.booklib.model.BeanPublisher;
import cn.edu.zucc.booklib.model.BeanReader;
import cn.edu.zucc.booklib.model.BeanReaderType;
import cn.edu.zucc.booklib.util.BaseException;

public class FrmBookManager_ModifyBook extends JDialog implements ActionListener {
	private BeanBook book=null;
	
	private JPanel toolBar = new JPanel();
	private JPanel workPane = new JPanel();
	private Button btnOk = new Button("确定");
	private Button btnCancel = new Button("取消");
	private JLabel labelId = new JLabel("图书条码：");
	private JLabel labelName = new JLabel("图书名称：");
	private JLabel labelPrice = new JLabel("图书单价：");
	private JLabel labelPub = new JLabel("出版社：");
	
	private JTextField edtId = new JTextField(20);
	private JTextField edtName = new JTextField(20);
	private JTextField edtPrice = new JTextField(20);
	private Map<String,BeanPublisher> pubMap_name=new HashMap<String,BeanPublisher>();
	private Map<String,BeanPublisher> pubMap_id=new HashMap<String,BeanPublisher>();
	
	
	private JComboBox cmbPub=null;
	public FrmBookManager_ModifyBook(JDialog f, String s, boolean b,BeanBook book) {
		super(f, s, b);
		this.book=book;
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolBar.add(btnOk);
		toolBar.add(btnCancel);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		workPane.add(labelId);
		this.edtId.setText(book.getBarcode());
		this.edtId.setEnabled(false);
		workPane.add(edtId);
		workPane.add(labelName);
		this.edtName.setText(book.getBookname());
		workPane.add(edtName);
		workPane.add(labelPrice);
		this.edtPrice.setText(book.getPrice()+"");
		workPane.add(edtPrice);
		workPane.add(labelPub);
		//提取读出版社信息
		try {
			List<BeanPublisher> pubs=(new PublisherManager()).loadAllPublisher();
			String[] strpubs=new String[pubs.size()+1];
			strpubs[0]="";
			int oldIndex=0;
			for(int i=0;i<pubs.size();i++){
				strpubs[i+1]=pubs.get(i).getPublisherName();
				if(book.getPubid()!=null && book.getPubid().equals(pubs.get(i).getPubid())) oldIndex=i+1;
				this.pubMap_id.put(pubs.get(i).getPubid(),pubs.get(i));
				this.pubMap_name.put(pubs.get(i).getPublisherName(), pubs.get(i));
			}
			this.cmbPub=new JComboBox(strpubs);
			this.cmbPub.setSelectedIndex(oldIndex);
			workPane.add(this.cmbPub);
			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.getContentPane().add(workPane, BorderLayout.CENTER);
		this.setSize(360, 180);
		// 屏幕居中显示
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int) (width - this.getWidth()) / 2,
				(int) (height - this.getHeight()) / 2);

		this.validate();
		this.btnOk.addActionListener(this);
		this.btnCancel.addActionListener(this);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FrmBookManager_ModifyBook.this.book=null;
			}
		});
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnCancel) {
			this.setVisible(false);
			this.book=null;
			return;
		}
		else if(e.getSource()==this.btnOk){
			String name=this.edtName.getText();
			double price=0;
			try{
				price=Double.parseDouble(this.edtPrice.getText());
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "单价输入不正确","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			book.setBookname(name);
			book.setPrice(price);
			if(this.cmbPub.getSelectedIndex()>0){
				BeanPublisher p=this.pubMap_name.get(this.cmbPub.getSelectedItem().toString());
				if(p!=null) book.setPubid(p.getPubid());
			}
			else book.setPubid(null);
			
			
			try {
				(new BookManager()).modifyBook(book);
				this.setVisible(false);
			} catch (BaseException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(),"错误",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	public BeanBook getBook() {
		return book;
	}
	
}
