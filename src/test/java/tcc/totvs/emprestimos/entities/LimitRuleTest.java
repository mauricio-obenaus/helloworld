package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.assertEquals;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.junit.Test;

import tcc.totvs.emprestimos.entities.Empresa.EmpresaBuilder;
import tcc.totvs.emprestimos.rules.LimiteRule.DefaultLimitRule;

public class LimitRuleTest {
	private PessoaFisica pessoaFisica = PessoaFisica.builder().cpf(Cpf.of("111.111.111-11")).nome("Onzinio das Onze").build();
	private Cnpj cnpj = Cnpj.of("09.456.584/0001-89");
	private EmpresaBuilder empresaBuilder = Empresa.builder()
			.cnpj(cnpj)
			.nome("Empresa dos Devedores")
			.responsavel(pessoaFisica);


	
	@Test
	public void testCalculateDefault() {
		Empresa empresa = empresaBuilder
				.numeroEmpregados(30)
				.valorMercado(FastMoney.of(30000, "BRL"))
				.build();
		DefaultLimitRule rule = new DefaultLimitRule();
		MonetaryAmount limite = rule.calculate(empresa);
		assertEquals(1000, limite.getNumber().intValue());
	}

	@Test
	public void testCalculateLesserLimit() {
		Empresa empresa = empresaBuilder
				.numeroEmpregados(300)
				.valorMercado(FastMoney.of(30000, "BRL"))
				.build();
		DefaultLimitRule rule = new DefaultLimitRule();
		MonetaryAmount limite = rule.calculate(empresa);
		assertEquals(100, limite.getNumber().intValue());
	}

	@Test
	public void testCalculateExactLimit() {
		Empresa empresa = empresaBuilder
				.numeroEmpregados(10)
				.valorMercado(FastMoney.of(150000, "BRL"))
				.build();
		DefaultLimitRule rule = new DefaultLimitRule();
		MonetaryAmount limite = rule.calculate(empresa);
		assertEquals(15000, limite.getNumber().intValue());
	}

	@Test
	public void testCalculateMaxLimit() {
		Empresa empresa = empresaBuilder
				.numeroEmpregados(1)
				.valorMercado(FastMoney.of(150000, "BRL"))
				.build();
		DefaultLimitRule rule = new DefaultLimitRule();
		MonetaryAmount limite = rule.calculate(empresa);
		assertEquals(15000, limite.getNumber().intValue());
	}
}
