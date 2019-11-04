package tcc.totvs.emprestimos.entities;

import javax.money.MonetaryAmount;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Empresa {
	
	@NonNull
	private Cnpj cnpj;
	@NonNull
	private String nome;
	@NonNull
	private PessoaFisica responsavel;

	private MonetaryAmount valorMercado;
	private int numeroEmpregados;
	
}
