package cn.edu.zucc.booklib.control;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.booklib.model.BeanPublisher;
import cn.edu.zucc.booklib.model.BeanReaderType;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.BusinessException;
import cn.edu.zucc.booklib.util.DBUtil;
import cn.edu.zucc.booklib.util.DbException;

public class PublisherManager {
	public List<BeanPublisher> loadAllPublisher()throws BaseException{
		List<BeanPublisher> result=new ArrayList<BeanPublisher>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select pubid,publisherName,address from BeanPublisher order by pubid";
			java.sql.Statement st=conn.createStatement();
			java.sql.ResultSet rs=st.executeQuery(sql);
			while(rs.next()){
				BeanPublisher p=new BeanPublisher();
				p.setPubid(rs.getString(1));
				p.setPublisherName(rs.getString(2));
				p.setAddress(rs.getString(3));
				result.add(p);
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
	public  void createPublisher(BeanPublisher p) throws BaseException{
		if(p.getPubid()==null || "".equals(p.getPubid()) || p.getPubid().length()>20){
			throw new BusinessException("出版社编号必须是1-20个字");
		}
		if(p.getPublisherName()==null || "".equals(p.getPublisherName()) || p.getPublisherName().length()>50){
			throw new BusinessException("出版社名称必须是1-50个字");
		}
		if(p.getAddress()==null || "".equals(p.getAddress()) || p.getAddress().length()>100){
			throw new BusinessException("出版地址必须是1-100个字");
		}
		
		
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from BeanPublisher where pubid=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1,p.getPubid());
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()) throw new BusinessException("出版社编号已经被占用");
			rs.close();
			pst.close();
			sql="select * from BeanPublisher where publisherName=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, p.getPublisherName());
			rs=pst.executeQuery();
			if(rs.next()) throw new BusinessException("出版社名称已经存在");
			rs.close();
			pst.close();
			sql="insert into BeanPublisher(pubid,publisherName,address) values(?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, p.getPubid());
			pst.setString(2, p.getPublisherName());
			pst.setString(3,p.getAddress());
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
	public void modifyPublisher(BeanPublisher p)throws BaseException{
		if(p.getPubid()==null || "".equals(p.getPubid()) || p.getPubid().length()>20){
			throw new BusinessException("出版社编号必须是1-20个字");
		}
		if(p.getPublisherName()==null || "".equals(p.getPublisherName()) || p.getPublisherName().length()>50){
			throw new BusinessException("出版社名称必须是1-50个字");
		}
		if(p.getAddress()==null || "".equals(p.getAddress()) || p.getAddress().length()>100){
			throw new BusinessException("出版地址必须是1-100个字");
		}
		
			Connection conn=null;
			try {
				conn=DBUtil.getConnection();
				String sql="select * from BeanPublisher where pubid=?";
				java.sql.PreparedStatement pst=conn.prepareStatement(sql);
				pst.setString(1, p.getPubid());
				java.sql.ResultSet rs=pst.executeQuery();
				if(!rs.next()) throw new BusinessException("出版社不存在");
				rs.close();
				pst.close();
				sql="select * from BeanPublisher where publisherName=? and pubid<>?";
				pst=conn.prepareStatement(sql);
				pst.setString(1, p.getPublisherName());
				pst.setString(2, p.getPubid());
				rs=pst.executeQuery();
				if(rs.next()) throw new BusinessException("同名出版社已经存在");
				rs.close();
				pst.close();
				sql="update  BeanPublisher set publisherName=?,address=? where pubid=?";
				pst=conn.prepareStatement(sql);
				pst.setString(1, p.getPublisherName());
				pst.setString(2,p.getAddress());
				pst.setString(3, p.getPubid());
				pst.execute();
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
	public void deletePublisher(String id)throws BaseException{
		if(id==null || "".equals(id) ||id.length()>20){
			throw new BusinessException("出版社编号必须是1-20个字");
		}
			Connection conn=null;
			try {
				conn=DBUtil.getConnection();
				String sql="select publisherName from BeanPublisher where pubid=?";
				java.sql.PreparedStatement pst=conn.prepareStatement(sql);
				pst.setString(1, id);
				java.sql.ResultSet rs=pst.executeQuery();
				if(!rs.next()) throw new BusinessException("出版社不存在");
				String publisherName=rs.getString(1);
				rs.close();
				pst.close();
				sql="select count(*) from BeanBook where pubid=?";
				pst=conn.prepareStatement(sql);
				pst.setString(1, id);
				rs=pst.executeQuery();
				rs.next();
				int n=rs.getInt(1);
				pst.close();
				if(n>0) throw new BusinessException("已经有"+n+"本图书的出版社是"+publisherName+"了，不能删除");
				pst=conn.prepareStatement("delete from BeanPublisher where pubid=?");
				pst.setString(1, id);
				pst.execute();
				
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
	
	public static void main(String[] args){
		BeanPublisher p=new BeanPublisher();
		p.setAddress("测试地址haha");
		p.setPubid("testpubid");
		p.setPublisherName("测试出版社haha");
		PublisherManager pm=new PublisherManager();
		try {
			List<BeanPublisher> lst=pm.loadAllPublisher();
			for(int i=0;i<lst.size();i++){
				p=lst.get(i);
				System.out.println(p.getPubid()+","+p.getPublisherName()+","+p.getAddress());
			}
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pm.deletePublisher("testpubid");
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
