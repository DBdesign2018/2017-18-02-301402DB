package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.edu.zucc.booklib.control.ReaderManager;
import cn.edu.zucc.booklib.model.BeanReaderType;
import cn.edu.zucc.booklib.util.BaseException;

public class FrmReaderTypeManager_ModifyReaderType extends JDialog implements ActionListener {
	private BeanReaderType readertype=null;
	
	private JPanel toolBar = new JPanel();
	private JPanel workPane = new JPanel();
	private Button btnOk = new Button("确定");
	private Button btnCancel = new Button("取消");
	private JLabel labelName = new JLabel("类别名称：");
	private JLabel labelLimitted = new JLabel("借阅限制：");
	
	private JTextField edtName = new JTextField(20);
	private JTextField edtLimited = new JTextField(20);
	public FrmReaderTypeManager_ModifyReaderType(JDialog f, String s, boolean b,BeanReaderType rt) {
		super(f, s, b);
		this.readertype=rt;
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolBar.add(btnOk);
		toolBar.add(btnCancel);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		workPane.add(labelName);
		this.edtName.setText(rt.getReaderTypeName());
		workPane.add(edtName);
		workPane.add(labelLimitted);
		this.edtLimited.setText(rt.getLendBookLimitted()+"");
		workPane.add(edtLimited);
		this.getContentPane().add(workPane, BorderLayout.CENTER);
		this.setSize(360, 140);
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
				FrmReaderTypeManager_ModifyReaderType.this.readertype=null;
			}
		});
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnCancel) {
			this.setVisible(false);
			this.readertype=null;
			return;
		}
		else if(e.getSource()==this.btnOk){
			String name=this.edtName.getText();
			int n=0;
			try{
				n=Integer.parseInt(this.edtLimited.getText());
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, this.edtLimited.getText()+"不是一个合法的整数","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(n<0 || n>100){
				JOptionPane.showMessageDialog(null, "借阅限制必须在0-100之间","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.readertype.setLendBookLimitted(n);
			this.readertype.setReaderTypeName(name);
			try {
				(new ReaderManager()).modifyReaderType(this.readertype);
				this.setVisible(false);
			} catch (BaseException e1) {
				this.readertype=null;
				JOptionPane.showMessageDialog(null, e1.getMessage(),"错误",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	public BeanReaderType getReadertype() {
		return readertype;
	}
	
}
