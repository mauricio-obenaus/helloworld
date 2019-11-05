package tcc.totvs.emprestimos.entities;

import static org.junit.Assert.*;

import java.time.LocalDate;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.junit.Before;
import org.junit.Test;

import tcc.totvs.emprestimos.entities.Movimentacao.StatusEmprestimo;

public class EmprestimoTest {

	private Conta conta;
	private MonetaryAmount valor;
	
	@Before
	public void init() {
		valor = FastMoney.of(5000, "BRL");
		conta = Conta.of(
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
	}

	@Test
	public void testIsAprovado() {
		Emprestimo emprestimo = Emprestimo.from(LocalDate.now(), conta, valor);
		assertFalse(emprestimo.isAprovado());
	}

	@Test
	public void testIsReprovado() {
		Emprestimo emprestimo = Emprestimo.from(LocalDate.now(), conta, valor);
		assertFalse(emprestimo.isReprovado());
	}

	@Test
	public void testIsQuitado() {
		Emprestimo emprestimo = Emprestimo.from(LocalDate.now(), conta, valor);
		assertFalse(emprestimo.isQuitado());
	}

	@Test(expected = IllegalStateException.class)
	public void testAprovarIlegal() {
		Emprestimo emprestimo = Emprestimo.from(LocalDate.now(), conta, valor);
		emprestimo.aprovar();
		assertTrue(emprestimo.isAprovado());
	}

	@Test(expected = IllegalStateException.class)
	public void testReprovarIlegal() {
		Emprestimo emprestimo = Emprestimo.from(LocalDate.now(), conta, valor);
		emprestimo.reprovar();
		assertTrue(emprestimo.isReprovado());
	}

	@Test(expected = IllegalStateException.class)
	public void testSolicitarLimiteIlegal() {
		Emprestimo emprestimo = Emprestimo.from(LocalDate.now(), conta, valor);
		emprestimo.solicitarLimiteEmergencia(FastMoney.of(1500, "BRL"));
		assertTrue(emprestimo.isReprovado());
	}

	@Test
	public void testSemSaldo() {
		Emprestimo emprestimo = conta.fazerEmprestimo(FastMoney.of(150000, "BRL"));
		assertEquals(StatusEmprestimo.EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA, emprestimo.getStatus());
	}

	@Test
	public void testReprovarSemSaldo() {
		conta.solicitarLimiteExtra(FastMoney.of(7500, "BRL"));
		conta.getLimiteExtra().ifPresent(l -> l.aprovar());
		Emprestimo emprestimo = conta.fazerEmprestimo(FastMoney.of(150000, "BRL"));
		assertEquals(StatusEmprestimo.EMPRESTIMO_REPROVADO, emprestimo.getStatus());
	}

	@Test
	public void testAprovar() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.aprovar();
		assertTrue(emprestimo.isAprovado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_AGUARDANDO_QUITACAO, emprestimo.getStatus());
	}

	@Test
	public void testReprovar() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.reprovar();
		assertTrue(emprestimo.isReprovado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA, emprestimo.getStatus());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testReaprovar() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.aprovar();
		emprestimo.aprovar();
		assertTrue("Não deve chegar aqui", emprestimo.isAprovado());
	}

	@Test(expected = IllegalStateException.class)
	public void testRereprovar() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.reprovar();
		emprestimo.reprovar();
		assertTrue("Não deve chegar aqui", emprestimo.isReprovado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA, emprestimo.getStatus());
	}

	@Test
	public void testSolicitarLimiteEmergencia() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.reprovar();
		emprestimo.solicitarLimiteEmergencia(FastMoney.of(1500, "BRL"));
		assertTrue(emprestimo.isLimiteEmergenciaSolicitado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_AGUARDANDO_APROVACAO_LIMITE, emprestimo.getStatus());
	}

	@Test
	public void testResolicitarLimiteEmergencia() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		conta.solicitarLimiteExtra(FastMoney.of(1500, "BRL"));
		conta.getLimiteExtra().ifPresent(l -> l.aprovar());
		emprestimo.reprovar();
		assertFalse(emprestimo.isLimiteEmergenciaSolicitado());
	}
	
	@Test
	public void testSolicitarLimiteEmergenciaParaReprovar() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.reprovar();
		emprestimo.solicitarLimiteEmergencia(FastMoney.of(1500, "BRL"));
		conta.getLimiteExtra().ifPresent(l -> l.reprovar());
		assertTrue(emprestimo.isLimiteEmergenciaSolicitado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_REPROVADO, emprestimo.getStatus());
	}
	
	@Test
	public void testSolicitarLimiteEmergenciaParaAprovar() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.reprovar();
		emprestimo.solicitarLimiteEmergencia(FastMoney.of(7500, "BRL"));
		conta.getLimiteExtra().ifPresent(l -> l.aprovar());
		assertTrue(emprestimo.isLimiteEmergenciaSolicitado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_AGUARDANDO_QUITACAO, emprestimo.getStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSolicitarLimiteEmergencialExcessivo() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.reprovar();
		emprestimo.solicitarLimiteEmergencia(FastMoney.of(15000, "BRL"));
		assertTrue("Não deve chegar aqui",emprestimo.isLimiteEmergenciaSolicitado());
	}
	
	@Test
	public void testQuitar() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.aprovar();
		emprestimo.devolucao(valor);
		assertTrue(emprestimo.isQuitado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_QUITADO, emprestimo.getStatus());
	}

	@Test
	public void testQuitarParcial() {
		Emprestimo emprestimo = conta.fazerEmprestimo(valor);
		emprestimo.aprovar();
		emprestimo.devolucao(FastMoney.of(2000, "BRL"));
		assertFalse(emprestimo.isQuitado());
		assertEquals(StatusEmprestimo.EMPRESTIMO_AGUARDANDO_QUITACAO, emprestimo.getStatus());
	}
	
	
}
