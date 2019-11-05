package tcc.totvs.emprestimos.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.function.MonetaryFunctions;

import lombok.Getter;

@Getter
public class Emprestimo implements Movimentacao {
	private LocalDate data;
	private Conta conta;
	private MonetaryAmount valor;
	
	private Tipo tipo = Movimentacao.Tipo.EMPRESTIMO;
	private StatusEmprestimo status = StatusEmprestimo.EMPRESTIMO_SOLICITADO;

	private LocalDateTime aprovacao;
	private LocalDateTime reprovacao;
	private LocalDateTime limiteEmergencial;
	private List<Devolucao> devolucoes = new ArrayList<>();
	
	private Emprestimo(LocalDate data, Conta conta, MonetaryAmount valor) {
		this.data = data;
		this.conta = conta;
		this.valor = valor;
	}
	
	public static Emprestimo from(LocalDate data, Conta conta, MonetaryAmount valor) {
		return new Emprestimo(data, conta, valor);
	}
	
	public void checkState() {		
		StatusEmprestimo newState = status.rule().next(this);
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

	public boolean isLimiteEmergenciaSolicitado() {
		return this.limiteEmergencial != null;
	}
	
	public boolean isQuitado() {
		return devolucoes.stream()
			.map(d -> d.getValor())
			.reduce(MonetaryFunctions.sum())
			.orElseGet(() -> FastMoney.of(0,"BRL"))
			.isGreaterThanOrEqualTo(getValor());
	}
	
	public void aprovar() {
		if (this.status != StatusEmprestimo.EMPRESTIMO_AGUARDANDO_APROVACAO)
			throw new IllegalStateException("Emprestimo não está aguardando aprovação");
		this.aprovacao = LocalDateTime.now();
		this.checkState();
	}
	

	public void reprovar() {
		if (this.status != StatusEmprestimo.EMPRESTIMO_AGUARDANDO_APROVACAO)
			throw new IllegalStateException("Emprestimo não está aguardando aprovação");
		this.reprovacao = LocalDateTime.now();
		this.checkState();
	}
	
	public void solicitarLimiteEmergencia(MonetaryAmount valor) {
		if (this.status != StatusEmprestimo.EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA)
			throw new IllegalStateException("Emprestimo não está aguardando solicitação de limite emergencial");
		this.conta.solicitarLimiteExtra(valor);
		this.limiteEmergencial = LocalDateTime.now();
		this.checkState();
	}

	public void devolucao(MonetaryAmount valor) {
		Devolucao devolucao = Devolucao.from(LocalDate.now(), this, valor);
		this.conta.devolucao(devolucao);
		this.devolucoes.add(devolucao);
		devolucao.getStatus().getOperacao().aplica(conta, valor);
		this.checkState();
	}
	
	
}
