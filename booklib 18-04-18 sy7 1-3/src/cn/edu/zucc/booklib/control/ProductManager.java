package cn.edu.zucc.booklib.control;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.edu.zucc.booklib.model.BeanProduct;
import cn.edu.zucc.booklib.util.BaseException;
import cn.edu.zucc.booklib.util.BusinessException;
import cn.edu.zucc.booklib.util.DBUtil;
import cn.edu.zucc.booklib.util.DbException;

public class ProductManager {
	public BeanProduct loadProductsById(int productId) throws BaseException{
		//要求根据productId返回封装好的产品信息
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select ProductName,productType,UnitsInStock from products where ProductID = ?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setInt(1,productId);
			java.sql.ResultSet rs = pst.executeQuery();
			if(!rs.next())
			{
				throw new BusinessException("未找到该产品信息");
			}
			BeanProduct  bp=new BeanProduct();
			bp.setProductID(productId);
			bp.setProductName(rs.getString(1));
			bp.setProductType(rs.getString(2));
			bp.setUnitsInStock(rs.getInt(3));
			pst.close();
			rs.close();
			return bp;
			
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


public List<BeanProduct> searchProducts(String keyword) throws BaseException{
	   //要求返回名称中包含keyword关键字的所有产品，要求采用模糊查询方式
	Connection conn=null;
	try {
		conn=DBUtil.getConnection();
		String sql="select productId,ProductName,productType,UnitsInStock from products where ProductName like ?";
		java.sql.PreparedStatement pst=conn.prepareStatement(sql);
		pst.setString(1,"%"+keyword+"%");
		java.sql.ResultSet rs = pst.executeQuery();
		List<BeanProduct> lbp =new ArrayList<BeanProduct>();
		
		while(rs.next())
		{
			BeanProduct  bp=new BeanProduct();
			bp.setProductID(rs.getInt(1));
			bp.setProductName(rs.getString(2));
			bp.setProductType(rs.getString(3));
			bp.setUnitsInStock(rs.getInt(4));
			lbp.add(bp);
		}
		pst.close();
		rs.close();
		return lbp;
		
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
	public void addProduct(BeanProduct p)throws BaseException{
	//将p参数对应的产品信息加入数据库
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select * from products where ProductID = ?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setInt(1,p.getProductID());
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next())
			{
				throw new BaseException("产品Id已存在");
			}
			sql ="insert into products(ProductID,ProductName,productType,UnitsInStock) values(?,?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,p.getProductID());
			pst.setString(2, p.getProductName());
			pst.setString(3, p.getProductType());
			pst.setInt(4, p.getUnitsInStock());
			pst.executeUpdate();
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
	}
//	public Map<String,Integer> loadTypeProductsCount()throws BaseException{
//		   //要求返回的map中，key为产品类型名称，value为这种类别产品的总库存量。
//		Connection conn=null;
//		Map<String,Integer> mp =new HashMap<String,Integer>();
//		try {
//			conn=DBUtil.getConnection();
//			String sql="select productType,UnitsInStock from products";
//			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
//			java.sql.ResultSet rs = pst.executeQuery();			
//			while(rs.next())
//			{
//				Set<String> keys = mp.keySet();
//				String k=null;
//		        for(String key: keys){
//		           if(key.equals(rs.getString(1)))
//		           {
//		        	   k=key;
//		        	   break;
//		           }
//		        }
//		        if(k==null)
//		        {
//		        	mp.put(rs.getString(1), rs.getInt(2));
//		        }
//		        else
//		        {
//		        	mp.put(rs.getString(1), mp.get(k)+rs.getInt(2));
//		        }
//			}	
//			pst.close();
//			rs.close();		
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new DbException(e);
//		}
//		finally{
//			if(conn!=null)
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}
//		return mp;
//		} 

		public Set<String> loadProductNames(String typeName)throws BaseException{
		//提取所有指定类型的产品名称
			Connection conn=null;
			Set<String> set =new  HashSet<String>();
			try {
				conn=DBUtil.getConnection();
				String sql="select ProductName from products where productType = ?";
				java.sql.PreparedStatement pst=conn.prepareStatement(sql);
				pst.setString(1, typeName);
				java.sql.ResultSet rs = pst.executeQuery();
				while(rs.next())
				{
					set.add(rs.getString(1));
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
			return set;
		}
		public int loadProductCount(String typeName)throws BaseException{
		   //提取指定产品类别的产品种类数（非库存量） 
			Connection conn=null;
			int i=0;
			try {
				conn=DBUtil.getConnection();
				String sql="select count(*) from products  where productType = ? group by productType";
				java.sql.PreparedStatement pst=conn.prepareStatement(sql);
				pst.setString(1, typeName);
				java.sql.ResultSet rs = pst.executeQuery();
				if(!rs.next())
				{
					throw new BaseException("未找到该类型的产品");
				}
				i= rs.getInt(1);
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

//		public Map<String,String> loadMaxCountProductInType()throws BaseException{
//		   //要求返回的map中，key为产品名称，value为对应的产品类型名称。要求提取在每种产品类型中，库存量最多的产品名称。
//			Connection conn=null;
//			Map<String,String> mp =new HashMap<String,String>();
//			Map<String,Integer> smp =new HashMap<String,Integer>();
//			try {
//				conn=DBUtil.getConnection();
//				String sql="select ProductName,ProductType,UnitsInStock from products";
//				java.sql.PreparedStatement pst=conn.prepareStatement(sql);
//				java.sql.ResultSet rs = pst.executeQuery();			
//				while(rs.next())
//				{
//					String v=null;
//					String k=null;
//			        for(Map.Entry<String, String> entry : mp.entrySet()){
//			           if(entry.getValue().equals(rs.getString(2)))
//			           {
//			        	   v=entry.getValue();
//			        	   k=entry.getKey();
//			        	   break;
//			           }
//			        }
//			        if(k==null)
//			        {
//			        	mp.put(rs.getString(1), rs.getString(2));
//			        	smp.put(rs.getString(2), rs.getInt(3));
//			        	
//			        }
//			        else if(smp.get(rs.getString(2))<rs.getInt(3))
//			        	{
//			        		mp.remove(k);
//			        		smp.remove(rs.getString(2));
//			        		mp.put(rs.getString(1), rs.getString(2));
//				        	smp.put(rs.getString(2), rs.getInt(3));
//			        	}
//				}	
//				pst.close();
//				rs.close();		
//			} catch (SQLException e) {
//				e.printStackTrace();
//				throw new DbException(e);
//			}
//			finally{
//				if(conn!=null)
//					try {
//						conn.close();
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			}
//			return mp;
//		}

//	public static void main(String[] args) throws FileNotFoundException
//	{
//		int id=15;
//		 ProductManager pm=new  ProductManager();
//		try {
//			BeanProduct bp=pm.loadProductsById(id);
//			System.out.println(bp.getProductID());
//			System.out.println(bp.getProductName());
//			System.out.println(bp.getProductType());
//			System.out.println(bp.getUnitsInStock());
//		} catch (BaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) throws FileNotFoundException
//	{
//		String id="产";
//		 ProductManager pm=new  ProductManager();
//		 BeanProduct bp=new BeanProduct();
//		try {
//			List<BeanProduct> lbp=pm.searchProducts(id);
//			for(int i=0;i<lbp.size();i++){
//				bp=lbp.get(i);
//				System.out.println(bp.getProductID()+","+bp.getProductName()+","+bp.getProductType()+","+bp.getUnitsInStock());
//			}
//		} catch (BaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) 
//	{
//		 ProductManager pm=new  ProductManager();
//		 BeanProduct bp=new BeanProduct();
//		 bp.setProductID(16);
//		 bp.setProductName("chan");
//		 bp.setProductType("类型1");
//		 bp.setUnitsInStock(121);
//		try {
//			pm.addProduct(bp);
//		} catch (BaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) 
//	{
//		 ProductManager pm=new  ProductManager();
//		try {
//			Map<String,Integer> mp=pm.loadTypeProductsCount();
//			 for(Map.Entry<String, Integer> entry : mp.entrySet()){
//		            System.out.println("key = " +entry.getKey() + ", value = " + entry.getValue());
//		        }
//		} catch (BaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) 
//	{
//		 ProductManager pm=new  ProductManager();
//		try {
//			Set<String> mp=pm.loadProductNames("类型2");
//			for(String key: mp){
//				System.out.println(key);
//			}
//		} catch (BaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) 
//	{
//		 ProductManager pm=new  ProductManager();
//		try {
//			int i=pm.loadProductCount("类型1");
//			 System.out.print(i);
//		} catch (BaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//		public static void main(String[] args) 
//		{
//			 ProductManager pm=new  ProductManager();
//			try {
//				Map<String,String> mp=pm.loadMaxCountProductInType();
//				 for(Map.Entry<String, String> entry : mp.entrySet()){
//			            System.out.println("key = " +entry.getKey() + ", value = " + entry.getValue());
//			        }
//			} catch (BaseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		public Map<String,Integer> loadTypeProductsCount()throws BaseException{
			   //要求返回的map中，key为产品类型名称，value为这种类别产品的总库存量。
			 Map<String,Integer> r =new  HashMap<String,Integer>();
			Connection conn=null;
			try {
				conn=DBUtil.getConnection();
				String sql="SELECT ProductType,sum(UnitsInStock) from products group by ProductType";
				java.sql.PreparedStatement pst=conn.prepareStatement(sql);
				java.sql.ResultSet rs=pst.executeQuery();
				while(rs.next()){
					r.put(rs.getString(1), rs.getInt(2));
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
			return r;
			} 

		public static void main(String[] args){
			 ProductManager mp= new  ProductManager();
			 try {
				 for(Entry<String, Integer> entry: mp.loadTypeProductsCount().entrySet())
				 {
					 System.out.println(entry.getKey()+" "+entry.getValue());
				 }
			 }catch (BaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	public Map<String,String> loadMaxCountProductInType()throws BaseException{
			   //要求返回的map中，key为产品名称，value为对应的产品类型名称。要求提取在每种产品类型中，库存量最多的产品名称。
			 Map<String,String> r =new  HashMap<String,String>();
				Connection conn=null;
				try {
					conn=DBUtil.getConnection();
					String sql="SELECT ProductName,ProductType from products a where a.UnitsInStock>=all( select b.UnitsInStock from products b where a.ProductType = b.ProductType)";
					java.sql.PreparedStatement pst=conn.prepareStatement(sql);
					java.sql.ResultSet rs=pst.executeQuery();
					while(rs.next()){
						r.put(rs.getString(1), rs.getString(2));
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
				return r;
			}
//		public static void main(String[] args){
//			 ProductManager mp= new  ProductManager();
//			 try {
//				 for(Entry<String, String> entry: mp.loadMaxCountProductInType().entrySet())
//				 {
//					 System.out.println(entry.getKey()+" "+entry.getValue());
//				 }
//			 }catch (BaseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}

}
