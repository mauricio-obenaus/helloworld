package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.*;

import org.junit.Test;

public class CpfTest {

	@Test
	public void testOfValidFormattedCpf() {
		Cpf cpf = Cpf.of("111.111.111-11");
		assertEquals("11111111111", cpf.getValue());
	}

	@Test
	public void testOfValidUnformattedCpf() {
		Cpf cpf = Cpf.of("11111111111");
		assertEquals("11111111111", cpf.getValue());
	}

	@Test
	public void testOfCpf() {
		Cpf cpf = Cpf.of("749.840.500-80");
		assertEquals("74984050080", cpf.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOfInvalidCpf() {
		Cpf cpf = Cpf.of("111.111.111-12");
		assertEquals("Não deve chegar aqui", "11111111112", cpf.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOfWrongFormatCpf() {
		Cpf cpf = Cpf.of("111.111.11111");
		assertEquals("Não deve chegar aqui", "11111111111", cpf.getValue());
	}

}
