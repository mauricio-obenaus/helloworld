package tcc.totvs.emprestimos.entities;

import java.time.LocalDate;

import javax.money.MonetaryAmount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tcc.totvs.emprestimos.rules.EmprestimoRule;
import tcc.totvs.emprestimos.rules.LimiteEmergencialRule;

public interface Movimentacao {
	LocalDate getData();
	MonetaryAmount getValor();
	Tipo getTipo();
	Status getStatus();
	Conta getConta();
	
	static enum Tipo {
		EMPRESTIMO,
		DEVOLUCAO,
		LIMITE_EXTRA
	}
	
	static interface Status {
		
		Operacao getOperacao();
			
	}	
	
	@AllArgsConstructor
	static enum StatusEmprestimo implements Status{
		EMPRESTIMO_SOLICITADO(Operacao.NOOP) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.Solicitado();
			}			
		},
		EMPRESTIMO_AGUARDANDO_APROVACAO(Operacao.NOOP) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.AguardandoAprovacao();
			}			
		},
		EMPRESTIMO_AGUARDANDO_QUITACAO(Operacao.NOOP) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.AguardandoQuitacao();
			}			
		},
		EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA(Operacao.NOOP) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.AguardandoLimiteEmergencia();
			}			
		},
		EMPRESTIMO_REPROVADO(Operacao.NOOP) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.Reprovado();
			}			
		},
		EMPRESTIMO_LIBERADO(Operacao.SOMA) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.Liberado();
			}			
		},
		EMPRESTIMO_QUITADO(Operacao.NOOP) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.Quitado();
			}			
		}, 
		EMPRESTIMO_AGUARDANDO_APROVACAO_LIMITE(Operacao.NOOP) {
			@Override
			public EmprestimoRule rule() {
				return new EmprestimoRule.AguardandoAprovacaoLimite();
			}
		};

		@Getter
		private Operacao operacao;
		
		public abstract EmprestimoRule rule();
		
	}

	@AllArgsConstructor
	static enum StatusDevolucao implements Status{
		DEVOLUCAO(Operacao.SUBTRACAO);

		@Getter
		private Operacao operacao;
		
		
	}

	@AllArgsConstructor
	static enum StatusLimiteEmergencial implements Status{
		LIMITE_SOLICITADO(Operacao.NOOP) {
			@Override
			public LimiteEmergencialRule rule() {
				return new LimiteEmergencialRule.Solicitado();
			}
		},
		LIMITE_REPROVADO(Operacao.LIMITE_EXTRA_REPROVADO) {
			@Override
			public LimiteEmergencialRule rule() {
				return new LimiteEmergencialRule.Reprovado();
			}
		},
		LIMITE_APROVADO(Operacao.LIMITE_EXTRA_APROVADO) {
			@Override
			public LimiteEmergencialRule rule() {
				return new LimiteEmergencialRule.Aprovado();
			}
		};

		@Getter
		private Operacao operacao;
		
		public abstract LimiteEmergencialRule rule();
		
		
	}

	static enum Operacao {
		SOMA {
			public void aplica(Conta conta, MonetaryAmount valor) {
				conta.adicionaSaldo(valor);
			}
		},
		SUBTRACAO {
			public void aplica(Conta conta, MonetaryAmount valor) {
				conta.substraiSaldo(valor);
			}
		},
		NOOP {
			public void aplica(Conta conta, MonetaryAmount valor) {
			}
		},
		LIMITE_EXTRA_APROVADO {
			public void aplica(Conta conta, MonetaryAmount valor) {
				conta.aplicarLimiteExtra(valor);				
			}
		},
		LIMITE_EXTRA_REPROVADO {
			public void aplica(Conta conta, MonetaryAmount valor) {
				conta.verificarEmprestimos();				
			}
		};
		
		public abstract void aplica(Conta conta, MonetaryAmount valor);
	}
}
