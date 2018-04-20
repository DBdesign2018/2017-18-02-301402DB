package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;

import cn.edu.zucc.booklib.control.BookLendManager;
import cn.edu.zucc.booklib.control.BookManager;
import cn.edu.zucc.booklib.control.ReaderManager;
import cn.edu.zucc.booklib.model.BeanBook;
import cn.edu.zucc.booklib.model.BeanBookLendRecord;
import cn.edu.zucc.booklib.model.BeanReader;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.DbException;

public class FrmBookLendSearch extends JDialog  {
	private JPanel toolBar = new JPanel();
	private JLabel lableBarcode = new JLabel("图书：");
	private JTextField edtBookBarcode = new JTextField(10);
	private JLabel lableBookName = new JLabel("");
	private JLabel lableBookState = new JLabel("");
	
	private Object tblTitle[]={"读者ID","借阅时间","归还时间","罚金"};
	private Object tblData[][];
	DefaultTableModel tablmod=new DefaultTableModel();
	private JTable dataTable=new JTable(tablmod);
	private final java.text.SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private void reloadTable(){
		if("".equals(this.lableBookName.getText())){
			tblData =new Object[0][4];
			tablmod.setDataVector(tblData,tblTitle);
			this.dataTable.validate();
			this.dataTable.repaint();
			return;
		}
		try {
			List<BeanBookLendRecord> records=(new BookLendManager()).loadBookAllRecode(this.edtBookBarcode.getText());
			tblData =new Object[records.size()][4];
			for(int i=0;i<records.size();i++){
				tblData[i][0]=records.get(i).getReaderid();
				tblData[i][1]=sdf.format(records.get(i).getLendDate());
				tblData[i][2]=records.get(i).getReturnDate()==null?"":sdf.format(records.get(i).getReturnDate());
				tblData[i][3]=records.get(i).getPenalSum()+"";
			}
			tablmod.setDataVector(tblData,tblTitle);
			this.dataTable.validate();
			this.dataTable.repaint();
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void reloadBookInfo(){
		String s=this.edtBookBarcode.getText().trim();
		BeanBook book=null;
		if(!"".equals(s)){
			try {
				book=(new BookManager()).loadBook(s);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.lableBookName.setText("");
		this.lableBookState.setText("");
		if(book!=null){
			this.lableBookName.setText(book.getBookname());
			this.lableBookState.setText(book.getState());
		}
		this.reloadTable();
	}
	public FrmBookLendSearch(Frame f, String s, boolean b) {
		super(f, s, b);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.add(lableBarcode);
		toolBar.add(edtBookBarcode);
		toolBar.add(lableBookName);
		toolBar.add(lableBookState);
		this.lableBookState.setForeground(Color.red);
		
		
		this.getContentPane().add(toolBar, BorderLayout.NORTH);
		//提取现有数据
		this.reloadTable();
		this.getContentPane().add(new JScrollPane(this.dataTable), BorderLayout.CENTER);
		
		// 屏幕居中显示
		this.setSize(800, 600);
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int) (width - this.getWidth()) / 2,
				(int) (height - this.getHeight()) / 2);

		this.validate();

		this.edtBookBarcode.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
		    public void changedUpdate(DocumentEvent e) {//这是更改操作的处理
		    	FrmBookLendSearch.this.reloadBookInfo();
		  	}
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				FrmBookLendSearch.this.reloadBookInfo();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				FrmBookLendSearch.this.reloadBookInfo();
			}
		});
		
	}

	
}
