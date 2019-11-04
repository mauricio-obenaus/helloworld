package tcc.totvs.emprestimos.rules;

import javax.money.MonetaryAmount;

import tcc.totvs.emprestimos.entities.Empresa;

public interface LimiteRule {
	
	MonetaryAmount calculate(Empresa empresa);
	
	static LimiteRule defaultRule() {
		return new DefaultLimitRule();
	}

}
