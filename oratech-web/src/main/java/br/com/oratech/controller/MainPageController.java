package br.com.oratech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.axxiom.core.service.interfaces.CustomCrudService;
import br.com.axxiom.core.web.controller.AbstractCrudController;
import br.com.oratech.domain.Movimentacao;
import br.com.oratech.service.dto.MovimentacaoDTO;
import br.com.oratech.service.interfaces.MovimentacaoService;
import br.com.oratech.service.interfaces.UsuarioService;

@Controller
@RequestMapping("/oratech")
public class MainPageController extends AbstractCrudController<Movimentacao, MovimentacaoDTO> {

	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	MovimentacaoService service;
	
	protected MainPageController() {
		super("movimentacao", "form", "main", Movimentacao.class, MovimentacaoDTO.class);
	}	
	

	@Override
	protected CustomCrudService<Movimentacao, Long> getService() {
		return service;
	}

	@RequestMapping(value="/main", method= RequestMethod.GET)
	public String mainPage(Model model){		
		return "main";
	}
}
