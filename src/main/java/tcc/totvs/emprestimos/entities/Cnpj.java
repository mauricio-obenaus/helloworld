package tcc.totvs.emprestimos.entities;

import java.util.regex.Pattern;

import lombok.Getter;

@Getter
public class Cnpj {
	private String value;
	private static Pattern formatted = Pattern.compile("(^\\d{2}.\\d{3}.\\d{3}/\\d{4}-\\d{2}$)");
	private static Pattern unformatted = Pattern.compile("(^\\d{14}$)");

	private Cnpj(String value) {
		if (!formatted.matcher(value).matches() &&
		    !unformatted.matcher(value).matches()) {
			throw new IllegalArgumentException("CNPJ não foi passado no formato correto: 99.999.999/9999-99 ou 999999999999999");
		}
		String numbers = value.replace(".", "").replace("/","").replace("-","");
		if (!isCnpj(numbers))
			throw new IllegalArgumentException("Valor informado não é CNPJ válido");
		this.value = numbers;
	}

	public static Cnpj of(String cnpj) {
		return new Cnpj(cnpj);
	}
	
	private boolean isCnpj(String cnpj) {		
		Integer digito1 = calcularDigito(cnpj.substring(0, 12));
		Integer digito2 = calcularDigito(cnpj.substring(0, 12) + digito1);
		return cnpj.equals(cnpj.substring(0, 12) + digito1.toString() + digito2.toString());
	}

	private static int calcularDigito(String str) {
		final int[] peso = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
		int soma = 0;
		for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
			digito = Integer.parseInt(str.substring(indice, indice + 1));
			soma += digito * peso[peso.length - str.length() + indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

}
