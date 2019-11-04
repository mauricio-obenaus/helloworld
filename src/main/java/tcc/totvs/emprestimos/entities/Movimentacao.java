package tcc.totvs.emprestimos.entities;

import java.time.LocalDate;

import javax.money.MonetaryAmount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
		EMPRESTIMO_SOLICITADO(Operacao.NOOP),
		EMPRESTIMO_REPROVADO(Operacao.NOOP),
		EMPRESTIMO_APROVADO(Operacao.SUBTRACAO);

		@Getter
		@Setter
		private Operacao operacao;
		
		
	}

	@AllArgsConstructor
	static enum StatusDevolucao implements Status{
		DEVOLUCAO(Operacao.SOMA);

		@Getter
		@Setter
		private Operacao operacao;
		
		
	}

	@AllArgsConstructor
	static enum StatusEmergencial implements Status{
		LIMITE_SOLICITADO(Operacao.NOOP),
		LIMITE_REPROVADO(Operacao.NOOP),
		LIMITE_APROVADO(Operacao.LIMITE_EXTRA);

		@Getter
		@Setter
		private Operacao operacao;
		
		
	}

	static enum Operacao {
		SOMA {
			public void aplica(Conta conta, MonetaryAmount valor) {
				conta.adicionaLimite(valor);
			}
		},
		SUBTRACAO {
			public void aplica(Conta conta, MonetaryAmount valor) {
				conta.substraiLimite(valor);
			}
		},
		NOOP {
			public void aplica(Conta conta, MonetaryAmount valor) {	
			}
		},
		LIMITE_EXTRA {
			public void aplica(Conta conta, MonetaryAmount valor) {
				conta.adicionaLimiteExtra(valor);
			}
		};
		
		public abstract void aplica(Conta conta, MonetaryAmount valor);
	}
}
