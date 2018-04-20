package cn.edu.zucc.booklib;

import java.sql.Connection;
import java.sql.SQLException;

import cn.edu.zucc.booklib.control.BookLendManager;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.DBUtil;

public class IndexTest {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws BaseException {
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			long begin=System.currentTimeMillis();
			//编写代码，随意选择一个读者，查询该读者的借阅的图书总量，并输出
			String sql="select readerid from beanreader order by rand() limit 1";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next())
			{
				throw new BaseException("没有读者");
			}
			String id= rs.getString(1);
			pst.close();
			rs.close();
			
			sql="select * from beanreader  where readerid='"+id+"'" ;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			if(!rs.next())
			{
				throw new BaseException("不存在该读者");
			}
			pst.close();
			rs.close();
			
			int i=0;
//			sql="SELECT count(*) FROM beanbooklendrecord where readerid='"+id+"' group by readerid ";
			sql="select count from view_reader_static where readerid='"+ id+"'" ;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			if(rs.next())
			{
				i=rs.getInt(1);
			}
			pst.close();
			rs.close();
			System.out.println("读者"+id+"借阅图书总量为:"+i);
			pst.close();
			rs.close();
			System.out.println("耗时："+(System.currentTimeMillis()-begin)+"毫秒");
			
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
