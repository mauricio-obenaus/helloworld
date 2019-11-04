package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.*;

import org.junit.Test;

public class PessoaFisicaTest {

	@Test
	public void testBuilder() {
		final Cpf cpf = Cpf.of("111.111.111-11");
		final String nome = "Onzinio das Onze";
		PessoaFisica pf = PessoaFisica.builder()
				.cpf(cpf)
				.nome(nome)
				.build();
		assertEquals(cpf, pf.getCpf());
		assertEquals(nome, pf.getNome());
	}

	@Test(expected = NullPointerException.class)
	public void testSemCpf() {
		final String nome = "Onzinio das Onze";
		PessoaFisica pf = PessoaFisica.builder()
				.nome(nome)
				.build();
		assertEquals("Não deve chegar aqui", nome, pf.getNome());
	}

	@Test(expected = NullPointerException.class)
	public void testSemNome() {
		final Cpf cpf = Cpf.of("111.111.111-11");
		PessoaFisica pf = PessoaFisica.builder()
				.cpf(cpf)
				.build();
		assertEquals("Não deve chegar aqui", cpf, pf.getCpf());
	}

}
