import br.com.oratech.controller.LoginController;
import junit.framework.TestCase;

public class testeCalculo extends TestCase {
	
	public void testeCalculoSomar(){
		
		
		float fator1 = 1.5f;
		float fator2 = 2.5f;
		
		float esperado = 4.1f;//fator1 + fator2;
		
		float teste = LoginController.calculo(fator1, fator2);
		
		assertEquals(esperado, teste, 0);		
	}

}
