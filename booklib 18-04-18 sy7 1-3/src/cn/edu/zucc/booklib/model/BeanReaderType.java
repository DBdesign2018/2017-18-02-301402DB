package cn.edu.zucc.booklib.model;

public class BeanReaderType {
	private int readerTypeId;
	private String readerTypeName;
	private int lendBookLimitted;
	public int getReaderTypeId() {
		return readerTypeId;
	}
	public void setReaderTypeId(int readerTypeId) {
		this.readerTypeId = readerTypeId;
	}
	public String getReaderTypeName() {
		return readerTypeName;
	}
	public void setReaderTypeName(String readerTypeName) {
		this.readerTypeName = readerTypeName;
	}
	public int getLendBookLimitted() {
		return lendBookLimitted;
	}
	public void setLendBookLimitted(int lendBookLimitted) {
		this.lendBookLimitted = lendBookLimitted;
	}
	
}
