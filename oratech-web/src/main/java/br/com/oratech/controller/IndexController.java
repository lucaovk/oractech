package br.com.oratech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {
	
	private static final String TEMPLATE_INDEX="teste";
	
	//welcome method
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index(){
		return TEMPLATE_INDEX;
	}
	
	//welcome method
		@RequestMapping(value="/index", method=RequestMethod.GET)
		public String index(Model model){
			model.addAttribute("mensagemBemVindo", "Bem vindo!");
			return TEMPLATE_INDEX;
		}
	
	
	
	

}
