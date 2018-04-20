package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.edu.zucc.booklib.control.ReaderManager;
import cn.edu.zucc.booklib.model.BeanReaderType;
import cn.edu.zucc.booklib.util.BaseException;

public class FrmReaderTypeManager_AddReaderType extends JDialog implements ActionListener {
	private BeanReaderType readertype=null;
	
	private JPanel toolBar = new JPanel();
	private JPanel workPane = new JPanel();
	private Button btnOk = new Button("ȷ��");
	private Button btnCancel = new Button("ȡ��");
	private JLabel labelName = new JLabel("������ƣ�");
	private JLabel labelLimitted = new JLabel("�������ƣ�");
	
	private JTextField edtName = new JTextField(20);
	private JTextField edtLimited = new JTextField(20);
	public FrmReaderTypeManager_AddReaderType(JDialog f, String s, boolean b) {
		super(f, s, b);
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolBar.add(btnOk);
		toolBar.add(btnCancel);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		workPane.add(labelName);
		workPane.add(edtName);
		workPane.add(labelLimitted);
		workPane.add(edtLimited);
		this.getContentPane().add(workPane, BorderLayout.CENTER);
		this.setSize(360, 140);
		// ��Ļ������ʾ
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int) (width - this.getWidth()) / 2,
				(int) (height - this.getHeight()) / 2);

		this.validate();
		this.btnOk.addActionListener(this);
		this.btnCancel.addActionListener(this);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnCancel) {
			this.setVisible(false);
			return;
		}
		else if(e.getSource()==this.btnOk){
			
			String name=this.edtName.getText();
			int n=0;
			try{
				n=Integer.parseInt(this.edtLimited.getText());
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, this.edtLimited.getText()+"����һ���Ϸ�������","����",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(n<0 || n>100){
				JOptionPane.showMessageDialog(null, "�������Ʊ�����0-100֮��","����",JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.readertype=new BeanReaderType();
			this.readertype.setLendBookLimitted(n);
			this.readertype.setReaderTypeName(name);
			try {
				(new ReaderManager()).createReaderType(this.readertype);
				this.setVisible(false);
			} catch (BaseException e1) {
				this.readertype=null;
				JOptionPane.showMessageDialog(null, e1.getMessage(),"����",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	public BeanReaderType getReadertype() {
		return readertype;
	}
	
}
