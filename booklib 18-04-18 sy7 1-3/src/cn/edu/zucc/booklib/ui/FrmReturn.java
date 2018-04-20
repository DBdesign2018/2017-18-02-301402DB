package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
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

public class FrmReturn extends JDialog implements ActionListener {
	private JPanel toolBar = new JPanel();
	private JLabel lableBarcode = new JLabel("图书：");
	private JTextField edtBookBarcode = new JTextField(10);
	private JLabel lableBookName = new JLabel("");
	private JLabel lableBookState = new JLabel("");
	private JLabel lablePenalSum=new JLabel("");
	
	private Button btnReturn = new Button("归还");
	private Object tblTitle[]={"条码","书名","出版社","价格"};
	private Object tblData[][];
	List<BeanBook> lentBooks=null;
	DefaultTableModel tablmod=new DefaultTableModel();
	private JTable dataTable=new JTable(tablmod);
	private void reloadTable(String readerid){
		if(readerid==null || "".equals(readerid)){
			tblData =new Object[0][4];
			tablmod.setDataVector(tblData,tblTitle);
			this.dataTable.validate();
			this.dataTable.repaint();
			return ;
		}
			
		try {
			lentBooks=(new BookLendManager()).loadReaderLentBooks(readerid);
			
			tblData =new Object[lentBooks.size()][4];
			for(int i=0;i<lentBooks.size();i++){
				tblData[i][0]=lentBooks.get(i).getBarcode();
				tblData[i][1]=lentBooks.get(i).getBookname();
				tblData[i][2]=lentBooks.get(i).getPubName();
				tblData[i][3]=lentBooks.get(i).getPrice()+"";
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
		this.lablePenalSum.setText("");
		if(book!=null){
			this.lableBookName.setText(book.getBookname());
			this.lableBookState.setText(book.getState());
			try {
				BeanBookLendRecord r=(new BookLendManager()).loadUnReturnRecord(book.getBarcode());
				if(r!=null){
					Date lendDate=r.getLendDate();
					long x=(System.currentTimeMillis()-lendDate.getTime())/(1000*60*60*24);
					double penalSum=0;
					if(x>60){
						penalSum=(x-60)*0.1;
						this.lablePenalSum.setText("罚金："+penalSum);
					}
					this.reloadTable(r.getReaderid());
					return;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.reloadTable(null);
	}
	public FrmReturn(Frame f, String s, boolean b) {
		super(f, s, b);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.lablePenalSum.setForeground(Color.red);
		toolBar.add(lableBarcode);
		toolBar.add(edtBookBarcode);
		toolBar.add(lableBookName);
		toolBar.add(lableBookState);
		this.lableBookState.setForeground(Color.red);
		toolBar.add(btnReturn);
		
		
		this.getContentPane().add(toolBar, BorderLayout.NORTH);
		//提取现有数据
		this.getContentPane().add(new JScrollPane(this.dataTable), BorderLayout.CENTER);
		
		// 屏幕居中显示
		this.setSize(800, 600);
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int) (width - this.getWidth()) / 2,
				(int) (height - this.getHeight()) / 2);

		this.validate();

		this.btnReturn.addActionListener(this);
				this.edtBookBarcode.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
		    public void changedUpdate(DocumentEvent e) {//这是更改操作的处理
		    	FrmReturn.this.reloadBookInfo();
		  	}
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				FrmReturn.this.reloadBookInfo();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				FrmReturn.this.reloadBookInfo();
			}
		});
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnReturn){
			if("".equals(this.lableBookName.getText())){
				JOptionPane.showMessageDialog(null,"图书不存在","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(!"已借出".equals(this.lableBookState.getText())){
				JOptionPane.showMessageDialog(null,"图书没有借出","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				(new BookLendManager()).returnBook(this.edtBookBarcode.getText());
				this.edtBookBarcode.setText("");
				this.reloadBookInfo();
			} catch (BaseException e1) {
				JOptionPane.showMessageDialog(null,e1.getMessage(),"错误",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
}
