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
			//��д���룬����ѡ��һ�����ߣ���ѯ�ö��ߵĽ��ĵ�ͼ�������������
			String sql="select readerid from beanreader order by rand() limit 1";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next())
			{
				throw new BaseException("û�ж���");
			}
			String id= rs.getString(1);
			pst.close();
			rs.close();
			
			sql="select * from beanreader  where readerid='"+id+"'" ;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			if(!rs.next())
			{
				throw new BaseException("�����ڸö���");
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
			System.out.println("����"+id+"����ͼ������Ϊ:"+i);
			pst.close();
			rs.close();
			System.out.println("��ʱ��"+(System.currentTimeMillis()-begin)+"����");
			
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
