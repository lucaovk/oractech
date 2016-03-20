package br.com.oratech.service.interfaces;

import br.com.axxiom.core.service.interfaces.CrudService;
import br.com.oratech.domain.Usuario;

public interface UsuarioService extends CrudService<Usuario> {
	
	public Boolean validaUsuario(String usuario, String pass);
}
