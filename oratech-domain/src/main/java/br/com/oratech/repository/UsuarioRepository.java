package br.com.oratech.repository;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import br.com.axxiom.core.db.CustomRepository;
import br.com.oratech.domain.Usuario;

@Repository
public interface UsuarioRepository extends CustomRepository<Usuario, Long> {

}
