package cn.edu.zucc.booklib.model;

public class BeanBook {
	private String barcode;
	private String bookname;
	private String pubid;
	private double price;
	private String state;//״̬���ѽ��,�ڿ�,��ɾ��
	
	private String pubName;//���������ƣ���ͼ����в��洢���ƣ�ֻ�洢������ID
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getBookname() {
		return bookname;
	}
	public void setBookname(String bookname) {
		this.bookname = bookname;
	}
	public String getPubid() {
		return pubid;
	}
	public void setPubid(String pubid) {
		this.pubid = pubid;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPubName() {
		return pubName;
	}
	public void setPubName(String pubName) {
		this.pubName = pubName;
	}
	
}
