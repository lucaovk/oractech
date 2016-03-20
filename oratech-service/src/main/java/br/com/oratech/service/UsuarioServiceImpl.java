package br.com.oratech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.axxiom.core.service.AbstractCustomCrudService;
import br.com.oratech.domain.Usuario;
import br.com.oratech.repository.UsuarioRepository;
import br.com.oratech.service.interfaces.UsuarioService;

@Service("UsuarioService")
public class UsuarioServiceImpl extends AbstractCustomCrudService<Usuario,Long> implements UsuarioService{
	
	@Autowired
	UsuarioRepository repository;
	
	protected UsuarioServiceImpl() {
		super(Usuario.class);
	}
	
	@Override
	protected UsuarioRepository getRepository() {
		return repository;
	}

	public Boolean validaUsuario(String usuario, String pass) {
		Usuario usuarioEntity = new Usuario();
		
		usuarioEntity.setUsuario(usuario);
		usuarioEntity.setNome("Tião");
		usuarioEntity.setEmail("teste@teste.com");
		usuarioEntity.setSenha(pass);
		
		//repository.save(usuarioEntity);
		
		return true;
	}

	

}
