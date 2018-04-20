package cn.edu.zucc.booklib.model;

public class StaticBeanReaderLend {
	private String readerId;
	private String readerName;
	private int count;
	private double penalSum;
	public String getReaderId() {
		return readerId;
	}
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	public String getReaderName() {
		return readerName;
	}
	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getPenalSum() {
		return penalSum;
	}
	public void setPenalSum(double penalSum) {
		this.penalSum = penalSum;
	}
	
}
