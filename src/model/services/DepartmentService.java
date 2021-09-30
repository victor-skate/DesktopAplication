package model.services;

import java.util.List;

import model.DAO.DaoFactory;
import model.DAO.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao =  DaoFactory.createDepartmentDao();
		
		public List<Department> findAll(){
			return dao.findAll();
		}
		
		public void saveOrUpdate(Department obj) {
			if(obj.getId() == null) {
				dao.insert(obj);
			}else {
				dao.update(obj);
			}
		}
		
		public void remove(Department obj) {
			dao.deletByID(obj.getId());
		}
	}
 