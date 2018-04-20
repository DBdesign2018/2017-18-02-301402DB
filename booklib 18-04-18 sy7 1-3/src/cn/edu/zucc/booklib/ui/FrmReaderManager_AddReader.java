package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.edu.zucc.booklib.control.ReaderManager;
import cn.edu.zucc.booklib.control.SystemUserManager;
import cn.edu.zucc.booklib.model.BeanReader;
import cn.edu.zucc.booklib.model.BeanReaderType;
import cn.edu.zucc.booklib.util.BaseException;

public class FrmReaderManager_AddReader extends JDialog implements ActionListener {
	private BeanReader reader=null;
	
	private JPanel toolBar = new JPanel();
	private JPanel workPane = new JPanel();
	private Button btnOk = new Button("确定");
	private Button btnCancel = new Button("取消");
	private JLabel labelReaderid = new JLabel("读者证号：");
	private JLabel labelReaderName = new JLabel("读者姓名：");
	private JLabel labelReaderType = new JLabel("读者类别：");
	
	private JTextField edtId = new JTextField(20);
	private JTextField edtName = new JTextField(20);
	private Map<String,BeanReaderType> readerTypeMap_name=null;
	private JComboBox cmbReadertype=null;
	public FrmReaderManager_AddReader(JDialog f, String s, boolean b,Map<String,BeanReaderType> rtMap) {
		super(f, s, b);
		this.readerTypeMap_name=rtMap;
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolBar.add(btnOk);
		toolBar.add(btnCancel);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		workPane.add(labelReaderid);
		workPane.add(edtId);
		workPane.add(labelReaderName);
		workPane.add(edtName);
		workPane.add(labelReaderType);
		//提取读者类别信息
		String[] strTypes=new String[this.readerTypeMap_name.size()+1];
		strTypes[0]="";
		java.util.Iterator<BeanReaderType> itRt=this.readerTypeMap_name.values().iterator();
		int i=1;
		while(itRt.hasNext()){
			strTypes[i]=itRt.next().getReaderTypeName();
			i++;
		}
		cmbReadertype=new JComboBox(strTypes);
		workPane.add(cmbReadertype);
		
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
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnCancel) {
			this.setVisible(false);
			return;
		}
		else if(e.getSource()==this.btnOk){
			if(this.cmbReadertype.getSelectedIndex()<0){
				JOptionPane.showMessageDialog(null, "请选择读者类别","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			String id=this.edtId.getText();
			String name=this.edtName.getText();
			BeanReader r=new BeanReader();
			r.setReaderid(id);
			r.setReaderName(name);
			String rtName=this.cmbReadertype.getSelectedItem().toString();
			BeanReaderType rt=this.readerTypeMap_name.get(rtName);
			if(rt==null){
				JOptionPane.showMessageDialog(null, "请选择读者类别","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			r.setReaderTypeId(rt.getReaderTypeId());
			
			
			try {
				(new ReaderManager()).createReader(r);
				this.reader=r;
				this.setVisible(false);
			} catch (BaseException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(),"错误",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	public BeanReader getReader() {
		return reader;
	}
	
}
