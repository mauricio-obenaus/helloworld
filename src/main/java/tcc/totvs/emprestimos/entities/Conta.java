package tcc.totvs.emprestimos.entities;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;

import lombok.Getter;
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
	private List<Emprestimo> emprestimos;

	private Conta (String id, Empresa empresa, PessoaFisica superior) {
		this.id = Objects.requireNonNull(id);
		this.empresa = Objects.requireNonNull(empresa);
		this.superior = Objects.requireNonNull(superior);
		this.status = Status.ABERTA;
		this.limite = LimiteRule.defaultRule().calculate(empresa);
		this.emprestimos = new ArrayList<>();
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

	public void fazerEmprestimo(MonetaryAmount valor) {
		Emprestimo emprestimo = Emprestimo.builder()
				.conta(this)
				.data(LocalDate.now())
				.valor(valor)
				.build();
		emprestimos.add(emprestimo);
		emprestimo.checkState();
	}
	
	public void suspender() {
		this.status = Status.SUSPENSA;
	}

	public void reativar() {
		this.status = Status.ABERTA;
	}

}
