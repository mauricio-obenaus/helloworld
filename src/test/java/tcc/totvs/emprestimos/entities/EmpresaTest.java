package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.junit.Test;

public class EmpresaTest {
	
	private PessoaFisica pessoaFisica = PessoaFisica.builder().cpf(Cpf.of("111.111.111-11")).nome("Onzinio das Onze").build();
	private MonetaryAmount valorMercado = FastMoney.of(100000, "BRL");
	private Cnpj cnpj = Cnpj.of("09.456.584/0001-89");

	@Test
	public void testBuildEmpresaCompleta() {
		Empresa empresa = Empresa.builder()
				.cnpj(cnpj)
				.nome("Empresa dos Devedores")
				.numeroEmpregados(30)
				.responsavel(pessoaFisica)
				.valorMercado(valorMercado)
				.build();
		assertEquals(cnpj, empresa.getCnpj());
		assertEquals("Empresa dos Devedores", empresa.getNome());
		assertEquals(30, empresa.getNumeroEmpregados());
		assertEquals(pessoaFisica, empresa.getResponsavel());
		assertEquals(valorMercado, empresa.getValorMercado());
	}

	@Test
	public void testBuildEmpresaMinima() {
		Empresa empresa = Empresa.builder()
				.cnpj(cnpj)
				.nome("Empresa dos Devedores")
				.responsavel(pessoaFisica)
				.build();
		assertEquals(cnpj, empresa.getCnpj());
		assertEquals("Empresa dos Devedores", empresa.getNome());
		assertEquals(0, empresa.getNumeroEmpregados());
		assertEquals(pessoaFisica, empresa.getResponsavel());
		assertNull(empresa.getValorMercado());
	}

	@Test(expected = NullPointerException.class)
	public void testBuildEmpresaSemResponsavel() {
		Empresa empresa = Empresa.builder()
				.cnpj(cnpj)
				.nome("Empresa dos Devedores")
				.build();
		assertNull("Não deve chegar aqui",empresa);
	}

	@Test(expected = NullPointerException.class)
	public void testBuildEmpresaSemNome() {
		Empresa empresa = Empresa.builder()
				.cnpj(cnpj)
				.responsavel(pessoaFisica)
				.build();
		assertNull("Não deve chegar aqui",empresa);
	}

	@Test(expected = NullPointerException.class)
	public void testBuildEmpresaSemCnpj() {
		Empresa empresa = Empresa.builder()
				.nome("Empresa dos Devedores")
				.responsavel(pessoaFisica)
				.build();
		assertNull("Não deve chegar aqui",empresa);
	}
}
