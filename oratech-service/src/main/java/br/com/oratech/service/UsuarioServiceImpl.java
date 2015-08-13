package br.com.oratech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.oratech.domain.Usuario;
import br.com.oratech.repository.UsuarioRepository;
import br.com.oratech.service.interfaces.UsuarioService;

@Service("UsuarioService")
public class UsuarioServiceImpl implements UsuarioService{
	
	@Autowired
	UsuarioRepository repository;

	public Boolean validaUsuario(String usuario, String pass) {
		Usuario usuarioEntity = new Usuario();
		
		usuarioEntity.setMatricula(usuario);
		usuarioEntity.setNome("Lucas");
		usuarioEntity.setEmail("teste@teste.com");
		
		repository.save(usuarioEntity);
		
		return true;
	}

}
