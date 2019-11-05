package tcc.totvs.emprestimos.entities;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;

import lombok.Getter;
import tcc.totvs.emprestimos.entities.Movimentacao.Tipo;
import tcc.totvs.emprestimos.rules.LimiteRule;

@Getter
public class Conta {
	private String id;
	private Empresa empresa;
	private PessoaFisica superior;
	private Status status;
	private MonetaryAmount limite;
	private MonetaryAmount saldo;
	private MonetaryAmount limiteEmergencial;
	private List<Movimentacao> movimentos;

	private Conta (String id, Empresa empresa, PessoaFisica superior) {
		this.id = Objects.requireNonNull(id);
		this.empresa = Objects.requireNonNull(empresa);
		this.superior = Objects.requireNonNull(superior);
		this.status = Status.ABERTA;
		this.limite = LimiteRule.defaultRule().calculate(empresa);
		this.movimentos = new ArrayList<>();
		this.saldo = FastMoney.zero(Monetary.getCurrency("BRL"));	
		this.limiteEmergencial = FastMoney.zero(Monetary.getCurrency("BRL"));
	}
	
	public static enum Status {
		ABERTA,
		SUSPENSA
	}
	
	public static Conta of(String id, Empresa empresa, PessoaFisica superior) {
		return new Conta(id, empresa, superior);
	}

	public MonetaryAmount getLimiteDisponivel() {
		return this.limite
				.add(limiteEmergencial)
				.subtract(this.saldo);
	}

	public Emprestimo fazerEmprestimo(MonetaryAmount valor) {
		Emprestimo emprestimo = Emprestimo.from(LocalDate.now(), this, valor);
		movimentos.add(emprestimo);
		emprestimo.checkState();
		return emprestimo;
	}
	
	public void suspender() {
		this.status = Status.SUSPENSA;
	}

	public void reativar() {
		this.status = Status.ABERTA;
	}

	public boolean semSaldo(MonetaryAmount valor) {
		return this.getLimiteDisponivel().isLessThan(valor);
	}

	public boolean temLimiteEmergencial() {
		return this.getLimiteEmergencial().isPositive();
	}

	public void adicionaSaldo(MonetaryAmount valor) {
		this.saldo = this.saldo.add(valor);
	}

	public void substraiSaldo(MonetaryAmount valor) {
		this.saldo = this.saldo.subtract(valor);
	}

	public LimiteExtra solicitarLimiteExtra(MonetaryAmount valor) {
		LimiteExtra limiteExtra = LimiteExtra.from(LocalDate.now(), this, valor);
		limiteExtra.checkState();
		movimentos.add(limiteExtra);
		return limiteExtra;
	}

	public void aplicarLimiteExtra(MonetaryAmount valor) {
		this.limiteEmergencial = valor;
		verificarEmprestimos();
	}
	
	public void verificarEmprestimos() {
		movimentos.stream()
			.filter(m -> m.getTipo().equals(Tipo.EMPRESTIMO))
			.map(m -> (Emprestimo) m)
			.forEach(e -> e.checkState());		
	}

	public Optional<LimiteExtra> getLimiteExtra() {
		return movimentos.stream()
			.filter(m -> m.getTipo().equals(Tipo.LIMITE_EXTRA))
			.map(m -> (LimiteExtra) m)
			.findFirst();
	}
	
	public boolean isLimiteExtraAprovado() {
		if (getLimiteExtra().isEmpty())
			throw new IllegalStateException("Limite extra não solicitado");
		return getLimiteExtra().get().isAprovado();
	}

	public boolean isLimiteExtraReprovado() {
		if (getLimiteExtra().isEmpty())
			throw new IllegalStateException("Limite extra não solicitado");
		return getLimiteExtra().get().isReprovado();
	}

	public void devolucao(Devolucao devolucao) {
		movimentos.add(devolucao);
	}

}
