package tcc.totvs.emprestimos.entities;

import java.util.regex.Pattern;

import lombok.Getter;

@Getter
public class Cpf {
	private String value;
	private static Pattern formatted = Pattern.compile("(^\\d{3}\\x2E\\d{3}\\x2E\\d{3}\\x2D\\d{2}$)");
	private static Pattern unformatted = Pattern.compile("(^\\d{11}$)");

	private Cpf(String value) {
		if (!formatted.matcher(value).matches() &&
		    !unformatted.matcher(value).matches()) {
			throw new IllegalArgumentException("CPF não foi passado no formato correto: 999.999.999-99 ou 99999999999");
		}
		String numbers = value.replace(".", "").replace("-","");
		if (!isCpf(numbers))
			throw new IllegalArgumentException("Valor informado não é CPF válido");
		this.value = numbers;
	}

	public static Cpf of(String cpf) {
		return  new Cpf(cpf);
	}
	
	private boolean isCpf(String cpf) {		
		Integer digito1 = calcularDigito(cpf.substring(0, 9));
		Integer digito2 = calcularDigito(cpf.substring(0, 9) + digito1);
		return cpf.equals(cpf.substring(0, 9) + digito1.toString() + digito2.toString());
	}

	private static int calcularDigito(String str) {
		final int[] peso = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
		int soma = 0;
		for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
			digito = Integer.parseInt(str.substring(indice, indice + 1));
			soma += digito * peso[peso.length - str.length() + indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

}
