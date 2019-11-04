package tcc.totvs.emprestimos.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder 
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PessoaFisica {
	
	@NonNull
	private final Cpf cpf;
	@NonNull
	private final String nome;
		
}
