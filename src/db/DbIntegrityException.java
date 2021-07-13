package db;

public class DbIntegrityException extends RuntimeException{
	/* Tratamento de excessão que trata problemas de integridade dos dados.
	Se dois objetos estiverem vinculados e houver uma tentativa de exclusão de algum dos dados, pode gerar excessão, 
	e isso deve obrigatoriamente ser tratado.
	um vendedor esta vinculado á um departamento, se tentarmos deletar o departamento no qual o vendedor está vinculado
	isso causará a excessão.
	*/
	
	private static final long serialVersionUID = 1L;
	
	
	public DbIntegrityException(String msg) {
		super(msg);
	}

}
