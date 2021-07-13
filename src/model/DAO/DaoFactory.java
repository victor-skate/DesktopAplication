package model.DAO;

import db.DB;
import model.DAO.implementation.DepartmentDaoJDBC;
import model.DAO.implementation.SellerDaoJDBC;

public class DaoFactory {
	public static SellerDao createSellerDao() {
		//instanciando a implementação da interface SellerDao e passando uma conexão como parâmetro;
		return new SellerDaoJDBC(DB.getConnection());
	}

	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
		
	}
}
