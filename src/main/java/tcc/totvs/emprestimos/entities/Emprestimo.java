package tcc.totvs.emprestimos.entities;

import java.time.LocalDate;

import javax.money.MonetaryAmount;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Emprestimo implements Movimentacao {
	private LocalDate data;
	private Conta conta;
	private MonetaryAmount valor;
	@Builder.Default 
	private Tipo tipo = Movimentacao.Tipo.EMPRESTIMO;
	@Builder.Default 
	private Status status = Movimentacao.StatusEmprestimo.EMPRESTIMO_SOLICITADO;
	
	public void checkState() {
		
	}
	
	
	
}
