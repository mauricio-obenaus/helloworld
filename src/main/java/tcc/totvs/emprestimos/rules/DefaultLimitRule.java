package tcc.totvs.emprestimos.rules;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;

import tcc.totvs.emprestimos.entities.Empresa;

public class DefaultLimitRule implements LimiteRule {

	static MonetaryAmount MAX_LIMIT = FastMoney.of(15000, "BRL");
	@Override
	public MonetaryAmount calculate(Empresa empresa) {
		MonetaryAmount limite = empresa.getValorMercado().divide(empresa.getNumeroEmpregados());
		if (limite.isGreaterThan(MAX_LIMIT))
			return MAX_LIMIT;
		return limite;
	}

}
