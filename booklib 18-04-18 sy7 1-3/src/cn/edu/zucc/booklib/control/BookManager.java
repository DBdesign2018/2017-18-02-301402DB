package cn.edu.zucc.booklib.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.booklib.model.BeanBook;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.BusinessException;
import cn.edu.zucc.booklib.util.DBUtil;
import cn.edu.zucc.booklib.util.DBUtil2;
import cn.edu.zucc.booklib.util.DbException;

public class BookManager {
	public List<BeanBook> searchBook(String keyword,String bookState)throws BaseException{
		List<BeanBook> result=new ArrayList<BeanBook>();
		Connection conn=null;
		try {
			conn=DBUtil2.getInstance().getConnection();
			String sql="select barcode,bookname,pubid,price,state,publishername"
					+ " from view_book where state='"+bookState+"' ";
			if(keyword!=null && !"".equals(keyword))
				sql+=" and (bookname like ? or barcode like ?)";
			sql+=" order by barcode";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			if(keyword!=null && !"".equals(keyword)){
				pst.setString(1, "%"+keyword+"%");
				pst.setString(2, "%"+keyword+"%");
				
			}
				
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
	public  void createBook(BeanBook b) throws BaseException{
		
		
		if(b.getBarcode()==null || "".equals(b.getBarcode()) || b.getBarcode().length()>20){
			throw new BusinessException("条码必须是1-20个字");
		}
		if(b.getBookname()==null || "".equals(b.getBookname()) || b.getBookname().length()>50){
			throw new BusinessException("图书名称必须是1-50个字");
		}
		Connection conn=null;
		try {
			conn=DBUtil2.getInstance().getConnection();
			String sql="select * from BeanBook where barcode=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, b.getBarcode());
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()) throw new BusinessException("条码已经被占用");
			rs.close();
			pst.close();
			sql="insert into BeanBook(barcode,bookname,pubid,price,state) values(?,?,?,?,'在库')";
			pst=conn.prepareStatement(sql);
			pst.setString(1, b.getBarcode());
			pst.setString(2, b.getBookname());
			pst.setString(3, b.getPubid());
			pst.setDouble(4, b.getPrice());
		//	pst.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.execute();
			pst.close();
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
	public void modifyBook(BeanBook b) throws BaseException{
		if(b.getBookname()==null || "".equals(b.getBookname()) || b.getBookname().length()>50){
			throw new BusinessException("图书名称必须是1-50个字");
		}
		Connection conn=null;
		try {
			conn=DBUtil2.getInstance().getConnection();
			String sql="select * from BeanBook where barcode=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, b.getBarcode());
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()) throw new BusinessException("图书不存在");
			rs.close();
			pst.close();
			sql="update BeanBook set bookname=?,pubid=?,price=?,state=? where barcode=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,b.getBookname());
			pst.setString(2, b.getPubid());
			pst.setDouble(3,b.getPrice());
			pst.setString(4, b.getState());
			pst.setString(5, b.getBarcode());
			pst.execute();
			pst.close();
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
	public BeanBook loadBook(String barcode) throws DbException {
		Connection conn=null;
		try {
			conn=DBUtil2.getInstance().getConnection();
			String sql="select barcode,bookname,pubid,price,state,publishername "
					+ "from view_book " +
					" where  barcode=? ";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1,barcode);	
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()){
				BeanBook b=new BeanBook();
				b.setBarcode(rs.getString(1));
				b.setBookname(rs.getString(2));
				b.setPubid(rs.getString(3));
				b.setPrice(rs.getDouble(4));
				b.setState(rs.getString(5));
				b.setPubName(rs.getString(6));
				return b;
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
	public void setCover(String barcode,String filepath) throws DbException, FileNotFoundException
	{
		Connection conn=null;
		try {
			conn=DBUtil2.getInstance().getConnection();
			String sql="update BeanBook set book_cover=? where barcode=? ";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			File file =new File(filepath);
			Reader reader =new BufferedReader(new FileReader(file));
			pst.setCharacterStream(1,reader,(int)file.length());
			pst.setString(2,barcode);	
			pst.executeUpdate();
			
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

	public static void main(String[] args) throws FileNotFoundException
	{
		BookManager book=new BookManager();
		try {
			long begin=System.currentTimeMillis();
			for(int i=1;i<=1000;i++)
			{
				BeanBook b=new BeanBook();
				b.setBarcode("bk"+i);
				b.setBookname("book"+i);
				b.setPrice(20);
				b.setPubid("331");
				book.createBook(b);
			}
			System.out.println("耗时："+(System.currentTimeMillis()-begin)+"毫秒");
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
