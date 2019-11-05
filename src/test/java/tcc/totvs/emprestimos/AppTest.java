package tcc.totvs.emprestimos;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AppTest {

	@Test
	public void testMain() {
		App a = new App();
		App.main(null);
		assertNotNull(a);
	}

}
