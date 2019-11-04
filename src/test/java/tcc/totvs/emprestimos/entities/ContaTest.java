package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.assertEquals;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.junit.Test;

public class ContaTest {
	private PessoaFisica superior = PessoaFisica.builder().cpf(Cpf.of("222.222.222-22")).nome("Doisentos Dois").build();
	private PessoaFisica responsavel = PessoaFisica.builder().cpf(Cpf.of("111.111.111-11")).nome("Onzinio das Onze").build();
	private Cnpj cnpj = Cnpj.of("09.456.584/0001-89");
	private Empresa empresa = Empresa.builder()
			.cnpj(cnpj)
			.nome("Empresa dos Devedores")
			.responsavel(responsavel)
			.valorMercado(FastMoney.of(150000, "BRL"))
			.numeroEmpregados(10)
			.build();
	private MonetaryAmount limite = FastMoney.of(15000, "BRL");
	private MonetaryAmount zero = FastMoney.zero(Monetary.getCurrency("BRL"));

	@Test
	public void testCriaConta() {
		String id = "212134";
		Conta conta = Conta.of(id, empresa, superior);
		assertEquals(id, conta.getId());
		assertEquals(empresa, conta.getEmpresa());
		assertEquals(superior, conta.getSuperior());
		assertEquals(Conta.Status.ABERTA, conta.getStatus());
		assertEquals(limite, conta.getLimite());
		assertEquals(limite, conta.getLimiteDisponivel());
		assertEquals(zero, conta.getSaldo());		
		assertEquals(zero, conta.getLimiteEmergencial());				
	}
	
	@Test
	public void testFazerEmpresito() {
		MonetaryAmount valor = FastMoney.of(1000, "BRL");
		MonetaryAmount disponivel = FastMoney.of(14000, "BRL");
		Conta conta = Conta.of("1234", empresa, superior);
		conta.fazerEmprestimo(valor);
		assertEquals(valor, conta.getSaldo());
		assertEquals(disponivel, conta.getLimiteDisponivel());
	}

	@Test
	public void testFazerEmpresitoContaSuspensa() {
		MonetaryAmount valor = FastMoney.of(1000, "BRL");
		Conta conta = Conta.of("1234", empresa, superior);
		conta.suspender();
		conta.fazerEmprestimo(valor);
		assertEquals("Não deve chegar aqui", valor, conta.getSaldo());
	}
}
