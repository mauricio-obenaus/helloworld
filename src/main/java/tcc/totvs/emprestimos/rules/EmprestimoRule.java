package tcc.totvs.emprestimos.rules;

import tcc.totvs.emprestimos.entities.Conta;
import tcc.totvs.emprestimos.entities.Emprestimo;
import tcc.totvs.emprestimos.entities.Movimentacao;
import tcc.totvs.emprestimos.entities.Movimentacao.StatusEmprestimo;

public interface EmprestimoRule {
	Movimentacao.StatusEmprestimo next(Emprestimo emprestimo);

	static class Solicitado implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			Conta conta = emprestimo.getConta();
			if (conta.semSaldo(emprestimo.getValor())) {
				if (conta.temLimiteEmergencial()) {
					return StatusEmprestimo.EMPRESTIMO_REPROVADO;
				}
				return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA;
			}
			if (conta.getLimiteDisponivel().multiply(0.25).isGreaterThanOrEqualTo(emprestimo.getValor())) {
				return StatusEmprestimo.EMPRESTIMO_LIBERADO;
			}
			return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_APROVACAO;
		}
	}

	static class AguardandoAprovacao implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			if (emprestimo.isAprovado())
				return StatusEmprestimo.EMPRESTIMO_LIBERADO;
			if (emprestimo.isReprovado())
				return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA;
			return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_APROVACAO;
		}
	}
	
	static class AguardandoQuitacao implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			if (emprestimo.isQuitado()) 
				return StatusEmprestimo.EMPRESTIMO_QUITADO;
			return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_QUITACAO;
		}
		
	}

	static class AguardandoLimiteEmergencia implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			Conta conta = emprestimo.getConta();
			if (conta.temLimiteEmergencial()) 
				return StatusEmprestimo.EMPRESTIMO_REPROVADO;
			if (emprestimo.isLimiteEmergenciaSolicitado())
				return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_APROVACAO_LIMITE;
			return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_LIMITE_EMERGENCIA;
		}
		
	}
	
	static class AguardandoAprovacaoLimite implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			Conta conta = emprestimo.getConta();
			if (conta.isLimiteExtraAprovado())
				return StatusEmprestimo.EMPRESTIMO_LIBERADO;
			if (conta.isLimiteExtraReprovado())
				return StatusEmprestimo.EMPRESTIMO_REPROVADO;
			return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_APROVACAO_LIMITE;
		}

	}
	

	static class Reprovado implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			return StatusEmprestimo.EMPRESTIMO_REPROVADO;
		}
		
	}

	static class Quitado implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			return StatusEmprestimo.EMPRESTIMO_QUITADO;
		}
		
	}
	static class Liberado implements EmprestimoRule {

		@Override
		public StatusEmprestimo next(Emprestimo emprestimo) {
			return StatusEmprestimo.EMPRESTIMO_AGUARDANDO_QUITACAO;
		}
		
	}

}
