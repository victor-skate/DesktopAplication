package model.DAO.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.DAO.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	private Connection conn;

	// CONSTRUTOR RECEBE A CONEXÃO COMO PARÂMETRO POSSIBILITANDO ACESSO AOS DADOS
	// IMEDIATAMENTE
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	
	
	
	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES " + "(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);// RETORNA O ID GERADO PARA O seller
																					// ADICIONADO AO DB

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDep().getId());
			int linhasAfetadas = st.executeUpdate();

			/*
			 * PEGANDO O ID GERADO PARA O VENDEDOR ATRAVÉS DA INSERÇÃO NO DB E ATRIBUINDO AO
			 * OBJETO INSTANCIADO "obj"
			 */
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);

			} else {
				// SE NENHUMA LINHA FOI AFETADA É SINAL DE QUE NÃO HOUVE INSERÇÃO NO DB
				throw new DbException("Erro inesperado! nenhuma linha afetada");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}
	
	
	

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE Seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDep().getId());
			st.setInt(6,obj.getId());
			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	
		
	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement(
				"DELETE FROM seller \r\n"
				+ "WHERE Id = ?");
	
			st.setInt(1,id);
			st.executeUpdate();
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}	
	
	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs, dep);
				return seller;

			} else {
				return null;

			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			// não fechei a conexão porque pode ser que a conexão seja utilizada para
			// realizar outra operação
			// a conexão será fechada no programa principal
		}
	}

	
	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
	
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setBirthDate(rs.getDate("BirthDate"));

		seller.setDep(dep);

		return seller;
	}

	
	
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	
	
	
	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*,department.name as DepName "
					+ "FROM seller INNER JOIN department " + "ON seller.DepartmentId = department.Id ");

			rs = st.executeQuery();
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {
				/*A PRIMEIRA VEZ QUE O BLOCO FOR EXECUTADO O dep PERMANECERÁ NULL
				 * map.put ADICIONARÁ O dep AO MAP E NA SEGUNDA VEZ QUE O BLOCO FOR EXECUTADO
				 * SE O DEPARTAMENTO DO VENDEDOR JA EXISTIR NA COLEÇÃO MAP, ESSE DEPARTAMENTO INSTANCIARÁ 
				 * O dep, FAZENDO COM QUE O O TESTE IF RETORNE FALSE E NÃO EXECUTE O BLOCO IF
				*/
				Department dep = map.get(rs.getInt("DepartmentId"));//BUSCANDO NA COLEÇÃO MAP UM DEPARTAMENTO CUJA 
				//CHAVE SEJA IGUAL A CHAVE VINDA DO RESULTSET, SE NÃO HOUVER ESSA CHAVE NA COLEÇÃO, O DEP NÃO SERÁ
				//INSTANCIADO, FAZENDO COM QUE O TESTE IF RETORNE TRUE E EXECUTE O BLOCO INSTANCIANDO O dep E ADICIONANDO
				//A COLEÇÃO MAP PARA QUE, UMA VEZ INSTANCIADO E ADICIONADO, NÃO SEJA INSTANCIADO UM DEPARTAMENTO EXISTENTE
				//QUALQUER COISA OLHA LA NO MATERIAL DE APOIO VITÃO
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					// ADICIONA O DEPARTAMENTO AO MAP
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Seller seller = instantiateSeller(rs, dep);
				seller.setDep(dep);
				list.add(seller);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			// não fechei a conexão porque pode ser que a conexão seja utilizada para
			// realizar outra operação
			// a conexão será fechada no programa principal
		}
	}
	
	
	@Override
	public List<Seller> findByDepartment(Department dep) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");

			st.setInt(1, dep.getId());
			rs = st.executeQuery();

			List<Seller> list = new ArrayList<>();
			// MAP VAZIO QUE ARMAZENA O ID E O DEPARTAMENTO PARA EVITAR QUE HAJA MAIS DE UMA
			// INSTANCIA DO DEPARTAMENTO
			Map<Integer, Department> map = new HashMap<Integer, Department>();

			while (rs.next()) {

				// CAPTURANDO O DEPARTAMENTO DO RESULTSET COM O ID '?', E ARMAZENANDO EM
				// DEPARTMENT
				Department department = map.get(rs.getInt("DepartmentId"));

				if (department == null) {
					department = instantiateDepartment(rs);

					// ADICIONA O DEPARTAMENTO AO MAP
					map.put(rs.getInt("DepartmentId"), department);
				}

				Seller seller = instantiateSeller(rs, department);
				list.add(seller);
			}

			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			// não fechei a conexão porque pode ser que a conexão seja utilizada para
			// realizar outra operação
			// a conexão será fechada no programa principal
		}

	}

}
