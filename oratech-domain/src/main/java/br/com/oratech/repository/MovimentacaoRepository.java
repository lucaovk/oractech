package br.com.oratech.repository;

import org.springframework.stereotype.Repository;

import br.com.axxiom.core.db.CustomRepository;
import br.com.oratech.domain.Movimentacao;

@Repository
public interface MovimentacaoRepository  extends CustomRepository<Movimentacao, Long> {
}
