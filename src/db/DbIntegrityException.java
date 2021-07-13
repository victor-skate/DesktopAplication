package db;

public class DbIntegrityException extends RuntimeException{
	/* Tratamento de excess�o que trata problemas de integridade dos dados.
	Se dois objetos estiverem vinculados e houver uma tentativa de exclus�o de algum dos dados, pode gerar excess�o, 
	e isso deve obrigatoriamente ser tratado.
	um vendedor esta vinculado � um departamento, se tentarmos deletar o departamento no qual o vendedor est� vinculado
	isso causar� a excess�o.
	*/
	
	private static final long serialVersionUID = 1L;
	
	
	public DbIntegrityException(String msg) {
		super(msg);
	}

}
