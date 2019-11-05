package tcc.totvs.emprestimos.rules;

import tcc.totvs.emprestimos.entities.LimiteExtra;
import tcc.totvs.emprestimos.entities.Movimentacao;
import tcc.totvs.emprestimos.entities.Movimentacao.StatusLimiteEmergencial;

public interface LimiteEmergencialRule {
	Movimentacao.StatusLimiteEmergencial next(LimiteExtra limiteExtra);

	static class Solicitado implements LimiteEmergencialRule {
		@Override
		public StatusLimiteEmergencial next(LimiteExtra limiteExtra) {
			if (limiteExtra.isAprovado())
				return StatusLimiteEmergencial.LIMITE_APROVADO;
			if (limiteExtra.isReprovado())
				return StatusLimiteEmergencial.LIMITE_REPROVADO;
			return StatusLimiteEmergencial.LIMITE_SOLICITADO;
		}
	}
	
	static class Aprovado implements LimiteEmergencialRule {
		@Override
		public StatusLimiteEmergencial next(LimiteExtra limiteExtra) {
			return StatusLimiteEmergencial.LIMITE_APROVADO;
		}
	}

	static class Reprovado implements LimiteEmergencialRule {
		@Override
		public StatusLimiteEmergencial next(LimiteExtra limiteExtra) {
			return StatusLimiteEmergencial.LIMITE_REPROVADO;
		}
	}
}
