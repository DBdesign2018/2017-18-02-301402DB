package cn.edu.zucc.booklib.control;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.zucc.booklib.model.BeanBook;
import cn.edu.zucc.booklib.model.BeanBookLendRecord;
import cn.edu.zucc.booklib.model.BeanReader;
import cn.edu.zucc.booklib.model.BeanSystemUser;
import cn.edu.zucc.booklib.model.StaticBeanBookLend;
import cn.edu.zucc.booklib.model.StaticBeanReaderLend;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.BusinessException;
import cn.edu.zucc.booklib.util.DBUtil;
import cn.edu.zucc.booklib.util.DbException;

public class BookLendManager {

	public List<BeanBook> loadReaderLentBooks(String readerId) throws DbException {
		List<BeanBook> result=new ArrayList<BeanBook>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select b.barcode,b.bookname,b.pubid,b.price,b.state,p.publishername " +
					" from beanbook b left outer join beanpublisher p on (b.pubid=p.pubid)" +
					" where  b.barcode in (select bookBarcode from BeanBookLendRecord where returnDate is null and readerid=?) ";
			sql+=" order by b.barcode";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, readerId);
			java.sql.ResultSet rs=pst.executeQuery();
			while(rs.next()){
				BeanBook b=new BeanBook();
				b.setBarcode(rs.getString(1));
				b.setBookname(rs.getString(2));
				b.setPubid(rs.getString(3));
				b.setPrice(rs.getDouble(4));
				b.setState(rs.getString(5));
				b.setPubName(rs.getString(6));
				result.add(b);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result;
	}

	public void lend(String barcode, String readerid) throws BaseException {
		BeanReader r=(new ReaderManager()).loadReader(readerid);
		if(r==null) throw new BusinessException("读者不存在");
		if(r.getRemoveDate()!=null) throw new BusinessException("读者已注销");
		if(r.getStopDate()!=null) throw new BusinessException("读者已挂失");
		BeanBook book=(new BookManager()).loadBook(barcode);
		if(book==null) throw new BusinessException("图书不存在");
		if(!"在库".equals(book.getState())) throw new BusinessException("图书"+book.getState());
		List<BeanBook> lentbooks=this.loadReaderLentBooks(readerid);
		if(r.getLendBookLimitted()<=lentbooks.size()){
			throw new BusinessException("超出限额");
		}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			conn.setAutoCommit(false);
			String sql="insert into BeanBookLendRecord(readerid,bookBarcode,lendDate,lendOperUserid,penalSum) values(?,?,?,?,0)";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, readerid);
			pst.setString(2, barcode);
			//pst.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.setDate(3,new java.sql.Date(System.currentTimeMillis()));//精确到年月日
			//pst.setTime(3, new java.sql.Time(System.currentTimeMillis()));//精确到时分秒
			pst.setString(4, SystemUserManager.currentUser.getUserid());
			pst.execute();
			pst.close();
			sql="update BeanBook set state='已借出' where barcode=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,barcode);
			pst.execute();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.rollback();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
	}
	public void returnBook(String barcode) throws BaseException {
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			conn.setAutoCommit(false);
			//提取借阅记录
			String sql="select id,lendDate from BeanBookLendRecord where bookBarcode=? and returnDate is null";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, barcode);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()){
				throw new BusinessException("该图书没有借阅记录");
			}
			int id=rs.getInt(1);
			Date lendDate=rs.getDate(2);
			rs.close();
			pst.close();
			long x=(System.currentTimeMillis()-lendDate.getTime())/(1000*60*60*24);
			double penalSum=0;
			if(x>60){//超过60天需要处罚
				penalSum=(x-60)*0.1;
			}
			sql="update BeanBookLendRecord set returnDate=?,returnOperUserid=?,penalSum="+penalSum+" where id=?";
			pst=conn.prepareStatement(sql);
			pst.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.setString(2, SystemUserManager.currentUser.getUserid());
			pst.setInt(3, id);
			pst.execute();
			pst.close();
			sql="update BeanBook set state='在库' where barcode=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,barcode);
			pst.execute();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.rollback();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
	}

	public BeanBookLendRecord loadUnReturnRecord(String barcode) throws DbException {
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select id,readerid,bookBarcode,lendDate,lendOperUserid from BeanBookLendRecord where bookBarcode=? and returnDate is null";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, barcode);
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()){
				BeanBookLendRecord r=new BeanBookLendRecord();
				r.setId(rs.getInt(1));
				r.setReaderid(rs.getString(2));
				r.setBookBarcode(rs.getString(3));
				r.setLendDate(rs.getDate(4));
				r.setLendOperUserid(rs.getString(5));
				return r;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return null;
	}

	public List<BeanBookLendRecord> loadBookAllRecode(String barcode) throws DbException {
		List<BeanBookLendRecord> result=new ArrayList<BeanBookLendRecord>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select id,readerid,lendDate,returnDate,penalSum from BeanBookLendRecord where bookBarcode=? order by lendDate desc";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, barcode);
			java.sql.ResultSet rs=pst.executeQuery();
			while(rs.next()){
				BeanBookLendRecord r=new BeanBookLendRecord();
				r.setId(rs.getInt(1));
				r.setReaderid(rs.getString(2));
				r.setBookBarcode(barcode);
				r.setLendDate(rs.getTimestamp(3));
				r.setReturnDate(rs.getTimestamp(4));
				r.setPenalSum(rs.getDouble(5));
				result.add( r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result;
	}
	public List<BeanBookLendRecord> loadReaderAllRecode(String readerid) throws DbException {
		List<BeanBookLendRecord> result=new ArrayList<BeanBookLendRecord>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select id,bookBarcode,lendDate,returnDate,penalSum from BeanBookLendRecord where readerid=? order by lendDate desc";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, readerid);
			java.sql.ResultSet rs=pst.executeQuery();
			while(rs.next()){
				BeanBookLendRecord r=new BeanBookLendRecord();
				r.setId(rs.getInt(1));
				r.setReaderid(readerid);
				r.setBookBarcode(rs.getString(2));
				r.setLendDate(rs.getTimestamp(3));
				r.setReturnDate(rs.getTimestamp(4));
				r.setPenalSum(rs.getDouble(5));
				result.add( r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result;
	}
	public List<StaticBeanReaderLend> staticReaderLend() throws DbException {
		List<StaticBeanReaderLend>  result=new ArrayList<StaticBeanReaderLend>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select r.readerid,r.readerName,count(*),sum(penalSum) from BeanReader r,BeanBookLendRecord rc " +
					" where r.readerid=rc.readerid group by  r.readerid,r.readerName order by count(*) desc";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			java.sql.ResultSet rs=pst.executeQuery();
			while(rs.next()){
				StaticBeanReaderLend r=new StaticBeanReaderLend();
				r.setReaderId(rs.getString(1));
				r.setReaderName(rs.getString(2));
				r.setCount(rs.getInt(3));
				r.setPenalSum(rs.getDouble(4));
				result.add( r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result;
	}
	public List<StaticBeanBookLend> staticBookLend() throws DbException {
		List<StaticBeanBookLend>  result=new ArrayList<StaticBeanBookLend>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select b.barcode,b.bookname,count(*) from BeanBook b,BeanBookLendRecord rc where b.barcode=rc.bookBarcode " +
					" group by  b.barcode,b.bookname order by count(*) desc";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			java.sql.ResultSet rs=pst.executeQuery();
			while(rs.next()){
				StaticBeanBookLend r=new StaticBeanBookLend();
				r.setBarcode(rs.getString(1));
				r.setBookname(rs.getString(2));
				r.setCount(rs.getInt(3));
				result.add( r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result;
	}

	public void CopyLend() throws DbException
	{
		System.out.println("1");
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="insert into  BeanBookLendRecord_backup select * from BeanBookLendRecord";
			java.sql.Statement st=conn.createStatement();
			st.execute(sql);
			System.out.println("1");
			sql ="delete from BeanBookLendRecord";
			st.execute(sql);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException(e);
		}
		finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
public void loadlendbook(String id)throws DbException//全部的代码
{
	Connection conn=null;
	try {
		conn=DBUtil.getConnection();
		String sql="select bookbarcode,lendDate from BeanBookLendRecord  where readerid='"+id+"'" ;
		java.sql.PreparedStatement pst=conn.prepareStatement(sql);
		java.sql.ResultSet rs=pst.executeQuery();
		SimpleDateFormat d=new SimpleDateFormat("yyyy年MM月dd日hh点mm分");//B部分格式改成这样
		//SimpleDateFormat d=new SimpleDateFormat("yyyy年MM月dd日");//A部分的格式
		while(rs.next())
		{
			System.out.print(rs.getString(1)+"    ");
			String date =d.format(rs.getDate(2));
			System.out.println(date);
		}
	} catch (SQLException e) {
		e.printStackTrace();
		throw new DbException(e);
	}
	finally{
		if(conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
public void loadmaxlendDate(String id)throws DbException//具体的代码实现
{
	Connection conn=null;
	try {
		conn=DBUtil.getConnection();
		String sql="select max(datediff(CURDATE(),lendDate)) from beanbooklendrecord  where readerid='"+id+"'" ;
		java.sql.PreparedStatement pst=conn.prepareStatement(sql);
		java.sql.ResultSet rs=pst.executeQuery();
		while(rs.next())
		{
			System.out.print(rs.getString(1));
		}
	} catch (SQLException e) {
		e.printStackTrace();
		throw new DbException(e);
	}
	finally{
		if(conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
//public static void main(String[] args){
//	String id="12";
//	BookLendManager lend=new BookLendManager();
//	try {
//		lend.loadmaxlendDate(id);//改一下面函数的调用
//	} catch (BaseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//}
public int loadReaderLendCount(String readerid) throws BaseException
{
	Connection conn=null;
	int i=0;
	try {
		conn=DBUtil.getConnection();
		String sql="select * from beanreader  where readerid='"+readerid+"'" ;
		java.sql.PreparedStatement pst=conn.prepareStatement(sql);
		java.sql.ResultSet rs=pst.executeQuery();
		if(!rs.next())
		{
			throw new BaseException("不存在该读者");
		}
		pst.close();
		rs.close();
		sql="select count from view_reader_static where readerid='"+ readerid+"'" ;
		pst=conn.prepareStatement(sql);
		rs=pst.executeQuery();
		if(rs.next())
		{
			i=rs.getInt(1);
		}
		pst.close();
		rs.close();
	} catch (SQLException e) {
		e.printStackTrace();
		throw new DbException(e);
	}
	finally{
		if(conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	return i;
}
public static void main(String[] args){
	String id="111";
	BookLendManager lend=new BookLendManager();
	try {
		int i=lend.loadReaderLendCount(id);//改一下面函数的调用
		System.out.print(i);
	} catch (BaseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}