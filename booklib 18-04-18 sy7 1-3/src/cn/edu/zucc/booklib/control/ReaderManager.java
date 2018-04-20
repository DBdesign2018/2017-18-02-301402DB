package cn.edu.zucc.booklib.control;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.booklib.model.BeanReader;
import cn.edu.zucc.booklib.model.BeanReaderType;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.BusinessException;
import cn.edu.zucc.booklib.util.DBUtil;
import cn.edu.zucc.booklib.util.DbException;

public class ReaderManager {
	public List<BeanReaderType> loadAllReaderType()throws BaseException{
		List<BeanReaderType> result=new ArrayList<BeanReaderType>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select readerTypeId,readerTypeName,lendBookLimitted from BeanReaderType order by readerTypeId";
			java.sql.Statement st=conn.createStatement();
			java.sql.ResultSet rs=st.executeQuery(sql);
			while(rs.next()){
				int id=rs.getInt(1);
				String name=rs.getString(2);
				int n=rs.getInt(3);
				BeanReaderType rt=new BeanReaderType();
				rt.setReaderTypeId(id);
				rt.setReaderTypeName(name);
				rt.setLendBookLimitted(n);
				result.add(rt);
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
	public  void createReaderType(BeanReaderType rt) throws BaseException{
		if(rt.getReaderTypeName()==null || "".equals(rt.getReaderTypeName()) || rt.getReaderTypeName().length()>20){
			throw new BusinessException("����������Ʊ�����1-20����");
		}
		if(rt.getLendBookLimitted()<0 || rt.getLendBookLimitted()>100){
			throw new BusinessException("����ͼ������������0-100֮��");
		}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from BeanReaderType where readerTypeName=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, rt.getReaderTypeName());
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()) throw new BusinessException("������������Ѿ���ռ��");
			rs.close();
			pst.close();
			sql="insert into BeanReaderType(readerTypeName,lendBookLimitted) values(?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, rt.getReaderTypeName());
			pst.setInt(2,rt.getLendBookLimitted());
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
	public void modifyReaderType(BeanReaderType rt)throws BaseException{
		if(rt.getReaderTypeId()<=0){
			throw new BusinessException("�������ID�����Ǵ���0������");
		}
		if(rt.getReaderTypeName()==null || "".equals(rt.getReaderTypeName()) || rt.getReaderTypeName().length()>20){
				throw new BusinessException("����������Ʊ�����1-20����");
			}
			if(rt.getLendBookLimitted()<0 || rt.getLendBookLimitted()>100){
				throw new BusinessException("����ͼ������������0-100֮��");
			}
			Connection conn=null;
			try {
				conn=DBUtil.getConnection();
				String sql="select * from BeanReaderType where readerTypeId="+rt.getReaderTypeId();
				java.sql.Statement st=conn.createStatement();
				java.sql.ResultSet rs=st.executeQuery(sql);
				if(!rs.next()) throw new BusinessException("������𲻴���");
				rs.close();
				st.close();
				sql="select * from BeanReaderType where readerTypeName=? and readerTypeId<>"+rt.getReaderTypeId();
				java.sql.PreparedStatement pst=conn.prepareStatement(sql);
				pst.setString(1, rt.getReaderTypeName());
				rs=pst.executeQuery();
				if(rs.next()) throw new BusinessException("������������Ѿ���ռ��");
				rs.close();
				pst.close();
				sql="update  BeanReaderType set readerTypeName=?,lendBookLimitted=? where readerTypeId=?";
				pst=conn.prepareStatement(sql);
				pst.setString(1, rt.getReaderTypeName());
				pst.setInt(2,rt.getLendBookLimitted());
				pst.setInt(3, rt.getReaderTypeId());
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
	public void deleteReaderType(int id)throws BaseException{
		if(id<=0){
			throw new BusinessException("�������ID�����Ǵ���0������");
		}
			Connection conn=null;
			try {
				conn=DBUtil.getConnection();
				String sql="select readerTypeName from BeanReaderType where readerTypeId="+id;
				java.sql.Statement st=conn.createStatement();
				java.sql.ResultSet rs=st.executeQuery(sql);
				if(!rs.next()) throw new BusinessException("������𲻴���");
				String readerTypeName=rs.getString(1);
				rs.close();
				sql="select count(*) from BeanReader where readerTypeId="+id;
				rs=st.executeQuery(sql); 
				rs.next();
				int n=rs.getInt(1);
				if(n>0) throw new BusinessException("�Ѿ���"+n+"��������"+readerTypeName+"�ˣ�����ɾ��");
				st.execute("delete from BeanReaderType where readerTypeId="+id);
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
	
	public List<BeanReader> searchReader(String keyword,int readerTypeId)throws BaseException{
		List<BeanReader> result=new ArrayList<BeanReader>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select readerid,readerName,readerTypeId,lendBookLimitted,"
					+ "createDate,creatorUserId,stopDate,stopUserId,readerTypeName "
					+ "from view_reader where removeDate is null ";
			if(readerTypeId>0) sql+=" and readerTypeId="+readerTypeId;
			if(keyword!=null && !"".equals(keyword))
				sql+=" and (readerid like ? or readerName like ?)";
			sql+=" order by readerid";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			if(keyword!=null && !"".equals(keyword)){
				pst.setString(1, "%"+keyword+"%");
				pst.setString(2, "%"+keyword+"%");
				
			}
				
			java.sql.ResultSet rs=pst.executeQuery();
			while(rs.next()){
				BeanReader r=new BeanReader();
				r.setReaderid(rs.getString(1));
				r.setReaderName(rs.getString(2));
				r.setReaderTypeId(rs.getInt(3));
				r.setLendBookLimitted(rs.getInt(4));
				r.setCreateDate(rs.getDate(5));
				r.setCreatorUserId(rs.getString(6));
				r.setStopDate(rs.getDate(7));
				r.setStopUserId(rs.getString(8));
				r.setReaderTypeName(rs.getString(9));
				result.add(r);
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
	public  void createReader(BeanReader r) throws BaseException{
		if(r.getReaderTypeId()<=0){
			throw new BusinessException("����ָ���������");
		}
		if(r.getReaderid()==null || "".equals(r.getReaderid()) || r.getReaderid().length()>20){
			throw new BusinessException("����֤�ű�����1-20����");
		}
		if(r.getReaderName()==null || "".equals(r.getReaderName()) || r.getReaderName().length()>20){
			throw new BusinessException("��������������1-20����");
		}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select lendBookLimitted from BeanReaderType where readerTypeId="+r.getReaderTypeId();
			java.sql.Statement st=conn.createStatement();
			java.sql.ResultSet rs=st.executeQuery(sql);
			if(!rs.next()) throw new BusinessException("������𲻴���");
			int lendBookLimitted=rs.getInt(1);
			rs.close();
			st.close();
			sql="select * from BeanReader where readerid=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, r.getReaderid());
			rs=pst.executeQuery();
			if(rs.next()) throw new BusinessException("���߱���Ѿ���ռ��");
			rs.close();
			pst.close();
			sql="insert into BeanReader(readerid,readerName,readerTypeId,lendBookLimitted,createDate,creatorUserId) values(?,?,?,?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, r.getReaderid());
			pst.setString(2, r.getReaderName());
			pst.setInt(3, r.getReaderTypeId());
			pst.setInt(4, lendBookLimitted);
			pst.setTimestamp(5,new java.sql.Timestamp(System.currentTimeMillis()));
			r.setCreatorUserId(SystemUserManager.currentUser.getUserid());
			pst.setString(6, r.getCreatorUserId());
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
	public void renameReader(String id,String name) throws BaseException{
		if(id==null || "".equals(id) || id.length()>20){throw new BusinessException("����֤�ű�����1-20����");}
		if(name==null || "".equals(name) || name.length()>20){throw new BusinessException("��������������1-20����");}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from BeanReader where readerid=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, id);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()) throw new BusinessException("���߲�����");
			if(rs.getDate("removeDate")!=null) throw new BusinessException("�����Ѿ�ע��");
			rs.close();
			pst.close();
			sql="update BeanReader set readerName=? where readerid=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,name);
			pst.setString(2, id);
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
	
	public void changeReaderType(String id,int readerTypeId) throws BaseException{
		if(id==null || "".equals(id) || id.length()>20){throw new BusinessException("����֤�ű�����1-20����");}
		if(readerTypeId<0){throw new BusinessException("��������Ų�����");}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from BeanReader where readerid=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, id);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()) throw new BusinessException("���߲�����");
			if(rs.getDate("removeDate")!=null) throw new BusinessException("�����Ѿ�ע��");
			if(rs.getInt("readerTypeId")==readerTypeId) throw new BusinessException("û�иı�������");
			rs.close();
			pst.close();
			sql="select lendBookLimitted from BeanReaderType where readerTypeId="+readerTypeId;
			java.sql.Statement st=conn.createStatement();
			rs=st.executeQuery(sql);
			if(!rs.next()) throw new BusinessException("������𲻴���");
			int lendBookLimitted=rs.getInt(1);
			
			sql="update BeanReader set readerTypeId=?,lendBookLimitted=? where readerid=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, readerTypeId);
			pst.setInt(2, lendBookLimitted);
			pst.setString(3, id);
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

	public void stopReader(String id,String doUserId)throws BaseException{
		if(id==null || "".equals(id) || id.length()>20){throw new BusinessException("����֤�ű�����1-20����");}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from BeanReader where readerid=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, id);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()) throw new BusinessException("���߲�����");
			if(rs.getDate("removeDate")!=null) throw new BusinessException("�����Ѿ�ע��");
			if(rs.getDate("stopDate")!=null) throw new BusinessException("�ö����Ѿ���ʧ");
			rs.close();
			pst.close();
			sql="update BeanReader set stopDate=?,stopUserId=? where readerid=?";
			pst=conn.prepareStatement(sql);
			pst.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.setString(2, doUserId);
			pst.setString(3, id);
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
	public void reuseReader(String id,String doUserId)throws BaseException{
		if(id==null || "".equals(id) || id.length()>20){throw new BusinessException("����֤�ű�����1-20����");}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from BeanReader where readerid=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, id);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()) throw new BusinessException("���߲�����");
			if(rs.getDate("removeDate")!=null) throw new BusinessException("�����Ѿ�ע��");
			if(rs.getDate("stopDate")==null) throw new BusinessException("�ö���δ��ʧ");
			rs.close();
			pst.close();
			sql="update BeanReader set stopDate=null,stopUserId=? where readerid=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, doUserId);
			pst.setString(2, id);
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
	public void removeReader(String id,String doUserId)throws BaseException{
		if(id==null || "".equals(id) || id.length()>20){throw new BusinessException("����֤�ű�����1-20����");}
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from BeanReader where readerid=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, id);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()) throw new BusinessException("���߲�����");
			if(rs.getDate("removeDate")!=null) throw new BusinessException("�����Ѿ�ע��");
			rs.close();
			pst.close();
			sql="update BeanReader set removeDate=?,removerUserId=? where readerid=?";
			pst=conn.prepareStatement(sql);
			pst.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.setString(2, doUserId);
			pst.setString(3, id);
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
	public BeanReader loadReader(String readerid) throws DbException {
		List<BeanReader> result=new ArrayList<BeanReader>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select readerid,readerName,readerTypeId,lendBookLimitted,"
					+ "createDate,creatorUserId,stopDate,stopUserId,readerTypeName,removeDate"
					+ " from view_reader where readerid=?";
			sql+=" order by readerid";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, readerid);
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()){
				BeanReader r=new BeanReader();
				r.setReaderid(rs.getString(1));
				r.setReaderName(rs.getString(2));
				r.setReaderTypeId(rs.getInt(3));
				r.setLendBookLimitted(rs.getInt(4));
				r.setCreateDate(rs.getDate(5));
				r.setCreatorUserId(rs.getString(6));
				r.setStopDate(rs.getDate(7));
				r.setStopUserId(rs.getString(8));
				r.setReaderTypeName(rs.getString(9));
				r.setRemoveDate(rs.getDate(10));
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

}
