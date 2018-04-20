package cn.edu.zucc.booklib;

import java.sql.Connection;
import java.sql.SQLException;

import cn.edu.zucc.booklib.control.SystemUserManager;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.DBUtil;

public class IndexTest_initData {

	/**
	 * @param args
	 * @throws BaseException 
	 */
	public static void main(String[] args) throws BaseException {
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			//��д���룬�����ݿ������1000�����ߣ�����idΪ:r1-r1000����������Ϊ������1-����1000�����������������ݿ���ȡһ��
			String sql="INSERT into BeanReader(readerid,readerName,readerTypeId,lendBookLimitted,createDate,creatorUserId) values(?,?,?,?,?,?)";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			String sql2="select ReaderTypeId,lendBookLimitted from beanreadertype order by rand() limit 1 ";
			java.sql.PreparedStatement pst1=conn.prepareStatement(sql2);
			java.sql.ResultSet rs= pst1.executeQuery();
			String sql3="select userid from beansystemuser order by rand() limit 1 ";
			java.sql.PreparedStatement pst2=conn.prepareStatement(sql3);
			java.sql.ResultSet rs1= pst2.executeQuery();
			if(!rs.next())
			{
				throw new BaseException("û�ж������");
			}
			if(!rs1.next())
			{
				throw new BaseException("û�й���Ա");
			}
			for(int i=1;i<=1000;i++)
			{
				pst.setString(1, "r"+i);
				pst.setString(2, "����"+i);
				pst.setInt(3, rs.getInt(1));
				pst.setInt(4, rs.getInt(2));
				pst.setTimestamp(5,new java.sql.Timestamp(System.currentTimeMillis()));
				pst.setString(6, rs1.getString(1));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			pst1.close();
			pst2.close();
			rs.close();
			rs1.close();
			//��д���룬�����ݿ���������1000���������Զ���
			sql="insert into BeanBook(barcode,bookname,pubid,price,state) values(?,?,?,?,'�ڿ�')";
			pst=conn.prepareStatement(sql);
			sql2="select pubid from beanpublisher order by rand() limit 1 ";
			pst1=conn.prepareStatement(sql2);
			rs= pst1.executeQuery();
			if(!rs.next())
			{
				throw new BaseException("û�ж������");
			}
			for(int i=1;i<=1000;i++)
			{
				pst.setString(1,"b"+i);
				pst.setString(2, "ͼ��"+i);
				pst.setString(3,rs.getString(1));
				pst.setDouble(4,10);
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();
			pst1.close();
			rs.close();
			//��д���룬�������û����������鼮������returnDateҲ�õ�ǰʱ��
			sql="insert into BeanBookLendRecord(readerid,bookBarcode,lendDate,lendOperUserid,returnDate,returnOperUserid,penalSum) values(?,?,?,?,?,?,0)";
			pst=conn.prepareStatement(sql);
			
			String sql4="select userid from beansystemuser order by rand() limit 1 ";	
			pst1=conn.prepareStatement(sql4);
			rs= pst1.executeQuery();
			if(!rs.next())
			{
				throw new BaseException("û�й���Ա");
			}
			
			
			for(int i=1;i<=1000;i++)
			{
				for(int j=1;j<=1000;j++)
				{
					pst.setString(1, "r"+i);
					pst.setString(2, "b"+j);
					pst.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
					pst.setString(4, rs.getString(1));
					pst.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
					pst.setString(6, rs.getString(1));
					pst.execute();
					System.out.println(1);
				}
				
			}
			
			pst.close();
			pst1.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
