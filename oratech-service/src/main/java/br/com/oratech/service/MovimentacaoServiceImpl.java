package br.com.oratech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.axxiom.core.service.AbstractCustomCrudService;
import br.com.oratech.domain.Movimentacao;
import br.com.oratech.repository.MovimentacaoRepository;
import br.com.oratech.service.interfaces.MovimentacaoService;

@Service
public class MovimentacaoServiceImpl extends AbstractCustomCrudService<Movimentacao,Long> implements MovimentacaoService {
	
	@Autowired
	MovimentacaoRepository repository;
 
	protected MovimentacaoServiceImpl() {
		super(Movimentacao.class);
	}

	@Override
	protected MovimentacaoRepository getRepository() {
		return repository;
	}

}
