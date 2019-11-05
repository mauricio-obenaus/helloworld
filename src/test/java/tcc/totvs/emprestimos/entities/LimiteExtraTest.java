package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.junit.Test;

public class LimiteExtraTest {
	
	private Conta conta = Conta.of(
			//id
			"1234",
			//empresa
			Empresa.builder()
				.cnpj(Cnpj.of("09.456.584/0001-89"))
				.nome("Empresa dos Devedores")
				.responsavel(PessoaFisica.builder()
						.cpf(Cpf.of("111.111.111-11"))
						.nome("Onzinio das Onze")
						.build())
				.valorMercado(FastMoney.of(150000, "BRL"))
				.numeroEmpregados(10)
				.build(), 
			//superior
			PessoaFisica.builder()
				.cpf(Cpf.of("222.222.222-22"))
				.nome("Doisentos Dois")
				.build());
	private MonetaryAmount valor = FastMoney.of(5000, "BRL");
	

	@Test
	public void testAprovar() {
		LimiteExtra limiteExtra = conta.solicitarLimiteExtra(valor);
		limiteExtra.aprovar();
		assertTrue(limiteExtra.isAprovado());
		assertFalse(limiteExtra.isReprovado());
	}

	@Test
	public void testReprovar() {
		LimiteExtra limiteExtra = conta.solicitarLimiteExtra(valor);
		limiteExtra.reprovar();
		assertTrue(limiteExtra.isReprovado());
		assertFalse(limiteExtra.isAprovado());
	}

	@Test(expected = IllegalStateException.class)
	public void testReaprovar() {
		LimiteExtra limiteExtra = conta.solicitarLimiteExtra(valor);
		limiteExtra.aprovar();
		limiteExtra.aprovar();
		assertTrue("Não deve chegar aqui",limiteExtra.isAprovado());
	}

	@Test(expected = IllegalStateException.class)
	public void testRereprovar() {
		LimiteExtra limiteExtra = conta.solicitarLimiteExtra(valor);
		limiteExtra.reprovar();
		limiteExtra.reprovar();
		assertTrue("Não deve chegar aqui",limiteExtra.isReprovado());
	}

}
