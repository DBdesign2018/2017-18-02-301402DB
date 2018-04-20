package cn.edu.zucc.booklib.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

public class FrmReaderManager_ModifyReader extends JDialog implements ActionListener {
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
	public FrmReaderManager_ModifyReader(JDialog f, String s, boolean b,Map<String,BeanReaderType> rtMap,BeanReader r) {
		super(f, s, b);
		this.reader=r;
		this.readerTypeMap_name=rtMap;
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolBar.add(btnOk);
		toolBar.add(btnCancel);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		workPane.add(labelReaderid);
		this.edtId.setEnabled(false);
		this.edtId.setText(this.reader.getReaderid());
		workPane.add(edtId);
		workPane.add(labelReaderName);
		this.edtName.setText(r.getReaderName());
		workPane.add(edtName);
		workPane.add(labelReaderType);
		//提取读者类别信息
		String[] strTypes=new String[this.readerTypeMap_name.size()+1];
		strTypes[0]="";
		java.util.Iterator<BeanReaderType> itRt=this.readerTypeMap_name.values().iterator();
		int i=1;
		int oldIndex=0;
		while(itRt.hasNext()){
			BeanReaderType rt=itRt.next();
			strTypes[i]=rt.getReaderTypeName();
			if(this.reader.getReaderTypeId()==rt.getReaderTypeId()){
				oldIndex=i;
			}
			i++;
		}
		cmbReadertype=new JComboBox(strTypes);
		this.cmbReadertype.setSelectedIndex(oldIndex);
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
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FrmReaderManager_ModifyReader.this.reader=null;
			}
		});
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnCancel) {
			this.setVisible(false);
			this.reader=null;
			return;
		}
		else if(e.getSource()==this.btnOk){
			if(this.cmbReadertype.getSelectedIndex()<0){
				JOptionPane.showMessageDialog(null, "请选择读者类别","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			String name=this.edtName.getText();
			String rtName=this.cmbReadertype.getSelectedItem().toString();
			BeanReaderType rt=this.readerTypeMap_name.get(rtName);
			if(rt==null){
				JOptionPane.showMessageDialog(null, "请选择读者类别","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				if(this.reader.getReaderTypeId()!=rt.getReaderTypeId()){
					(new ReaderManager()).changeReaderType(this.reader.getReaderid(),rt.getReaderTypeId());
				}
				if(!this.getReader().getReaderName().equals(name)){
					(new ReaderManager()).renameReader(this.getReader().getReaderid(),name);
				}
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
