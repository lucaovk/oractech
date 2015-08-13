package br.com.oratech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.oratech.service.interfaces.UsuarioService;

@Controller
@RequestMapping("/teste")
public class testeController {
	
	@Autowired
	UsuarioService service;
	
	@RequestMapping(value="/testeExec", method= RequestMethod.GET)
	public String teste(){
		
		System.out.println(service.validaUsuario("teste", "teste"));
		return "teste";
	}

}
