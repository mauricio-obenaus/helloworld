package tcc.totvs.emprestimos.entities;

import java.time.LocalDate;

import javax.money.MonetaryAmount;

import lombok.Getter;

@Getter
public class Devolucao  implements Movimentacao {
	private LocalDate data;
	private Conta conta;
	private MonetaryAmount valor;
	
	private Tipo tipo = Movimentacao.Tipo.DEVOLUCAO;
	private StatusDevolucao status = StatusDevolucao.DEVOLUCAO;
	
	Emprestimo emprestimo;
	
	private Devolucao(LocalDate data, Emprestimo emprestimo, MonetaryAmount valor) {
		this.data = data;
		this.conta = emprestimo.getConta();
		this.emprestimo = emprestimo;
		this.valor = valor;
	}
	
	public static Devolucao from(LocalDate data, Emprestimo emprestimo, MonetaryAmount valor) {
		return new Devolucao(data, emprestimo, valor);
	}
	
}
