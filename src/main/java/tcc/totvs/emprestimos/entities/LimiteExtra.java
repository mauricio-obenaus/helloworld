package tcc.totvs.emprestimos.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.money.MonetaryAmount;

import lombok.Getter;

@Getter
public class LimiteExtra implements Movimentacao {
	private LocalDate data;
	private Conta conta;
	private MonetaryAmount valor;
	
	private Tipo tipo = Movimentacao.Tipo.LIMITE_EXTRA;
	private StatusLimiteEmergencial status = StatusLimiteEmergencial.LIMITE_SOLICITADO;

	private LocalDateTime aprovacao;
	private LocalDateTime reprovacao;

	private LimiteExtra(LocalDate data, Conta conta, MonetaryAmount valor) {
		
		if (conta.temLimiteEmergencial())
			throw new IllegalStateException("Limite emergencial já solicitado anteriormente");
		MonetaryAmount valorMaximo = conta.getLimiteDisponivel().divide(2);
		if (valor.isGreaterThan(valorMaximo))
			throw new IllegalArgumentException("Solicitação de limite emergencial é maior que o permitido (50% do disponível)");
		
		this.data = data;
		this.conta = conta;
		this.valor = valor;
	}
	
	public static LimiteExtra from(LocalDate data, Conta conta, MonetaryAmount valor) {
		return new LimiteExtra(data, conta, valor);
	}
	
	public void checkState() {		
		StatusLimiteEmergencial newState = status.rule().next(this);
		if (newState != this.status) {
			this.status = newState;
			newState.getOperacao().aplica(conta, valor);
			checkState();
		}
	}

	public boolean isAprovado() {
		return this.aprovacao != null;
	}

	public boolean isReprovado() {
		return this.reprovacao != null;
	}

	
	public void aprovar() {
		if (this.status != StatusLimiteEmergencial.LIMITE_SOLICITADO)
			throw new IllegalStateException("Limite emergencial ja foi aprovado ou reprovado");
		this.aprovacao = LocalDateTime.now();
		this.checkState();
	}
	

	public void reprovar() {
		if (this.status != StatusLimiteEmergencial.LIMITE_SOLICITADO)
			throw new IllegalStateException("Limite emergencial ja foi aprovado ou reprovado");
		this.reprovacao = LocalDateTime.now();
		this.checkState();
	}
	
}
