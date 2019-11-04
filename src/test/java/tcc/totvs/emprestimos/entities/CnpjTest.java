package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.*;

import org.junit.Test;

public class CnpjTest {

	@Test
	public void testOfValidFormattedCnpj() {
		Cnpj cnpj = Cnpj.of("09.456.584/0001-89");
		assertEquals("09456584000189", cnpj.getValue());
	}

	@Test
	public void testOfValidUnformattedCnpj() {
		Cnpj cnpj = Cnpj.of("09456584000189");
		assertEquals("09456584000189", cnpj.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOfInvalidCnpj() {
		Cnpj cnpj = Cnpj.of("09.456.584/0001-90");
		assertEquals("Não deve chegar aqui", "09456584000190", cnpj.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOfWrongFormatCnpj() {
		Cnpj cnpj = Cnpj.of("09.456.584/000189");
		assertEquals("Não deve chegar aqui", "09456584000189", cnpj.getValue());
	}

}
