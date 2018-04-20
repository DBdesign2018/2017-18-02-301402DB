package cn.edu.zucc.booklib.model;

import java.util.Date;

public class BeanBookLendRecord {
	private int id;
	private String readerid;
	private String bookBarcode;
	private Date lendDate;
	private Date returnDate;
	private String lendOperUserid;
	private String returnOperUserid;
	private double penalSum;// ·£½ð
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReaderid() {
		return readerid;
	}
	public void setReaderid(String readerid) {
		this.readerid = readerid;
	}
	public String getBookBarcode() {
		return bookBarcode;
	}
	public void setBookBarcode(String bookBarcode) {
		this.bookBarcode = bookBarcode;
	}
	public Date getLendDate() {
		return lendDate;
	}
	public void setLendDate(Date lendDate) {
		this.lendDate = lendDate;
	}
	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	public String getLendOperUserid() {
		return lendOperUserid;
	}
	public void setLendOperUserid(String lendOperUserid) {
		this.lendOperUserid = lendOperUserid;
	}
	public String getReturnOperUserid() {
		return returnOperUserid;
	}
	public void setReturnOperUserid(String returnOperUserid) {
		this.returnOperUserid = returnOperUserid;
	}
	public double getPenalSum() {
		return penalSum;
	}
	public void setPenalSum(double penalSum) {
		this.penalSum = penalSum;
	}
	
}
