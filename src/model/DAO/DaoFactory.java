package model.DAO;

import db.DB;
import model.DAO.implementation.DepartmentDaoJDBC;
import model.DAO.implementation.SellerDaoJDBC;

public class DaoFactory {
	public static SellerDao createSellerDao() {
		//instanciando a implementa��o da interface SellerDao e passando uma conex�o como par�metro;
		return new SellerDaoJDBC(DB.getConnection());
	}

	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
		
	}
}
