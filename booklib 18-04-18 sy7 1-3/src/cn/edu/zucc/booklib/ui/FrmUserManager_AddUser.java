package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.edu.zucc.booklib.control.SystemUserManager;
import cn.edu.zucc.booklib.model.BeanSystemUser;
import cn.edu.zucc.booklib.util.BaseException;

public class FrmUserManager_AddUser extends JDialog implements ActionListener {
	private BeanSystemUser user=null;
	
	private JPanel toolBar = new JPanel();
	private JPanel workPane = new JPanel();
	private Button btnOk = new Button("ȷ��");
	private Button btnCancel = new Button("ȡ��");
	private JLabel labelUserid = new JLabel("�˺ţ�");
	private JLabel labelUsername = new JLabel("������");
	private JLabel labelUsertype = new JLabel("���");
	
	private JTextField edtUserid = new JTextField(20);
	private JTextField edtUsername = new JTextField(20);
	private JComboBox cmbUsertype= new JComboBox(new String[] { "����Ա", "����Ա"});
	public FrmUserManager_AddUser(JDialog f, String s, boolean b) {
		super(f, s, b);
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolBar.add(btnOk);
		toolBar.add(btnCancel);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		workPane.add(labelUserid);
		workPane.add(edtUserid);
		workPane.add(labelUsername);
		workPane.add(edtUsername);
		workPane.add(labelUsertype);
		workPane.add(cmbUsertype);
		this.getContentPane().add(workPane, BorderLayout.CENTER);
		this.setSize(300, 180);
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
			if(this.cmbUsertype.getSelectedIndex()<0){
				JOptionPane.showMessageDialog(null,  "��ѡ���˺����","��ʾ",JOptionPane.ERROR_MESSAGE);
				return;
			}
			String userid=this.edtUserid.getText();
			String username=this.edtUsername.getText();
			String usertype=this.cmbUsertype.getSelectedItem().toString();
			user=new BeanSystemUser();
			user.setUserid(userid);
			user.setUsername(username);
			user.setUsertype(usertype);
			try {
				(new SystemUserManager()).createUser(user);
				this.setVisible(false);
			} catch (BaseException e1) {
				this.user=null;
				JOptionPane.showMessageDialog(null, e1.getMessage(),"����",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	public BeanSystemUser getUser() {
		return user;
	}
	
}
