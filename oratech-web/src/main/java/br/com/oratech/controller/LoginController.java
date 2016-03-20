package br.com.oratech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.axxiom.core.service.interfaces.CustomCrudService;
import br.com.axxiom.core.web.controller.AbstractCrudController;
import br.com.oratech.domain.Usuario;
import br.com.oratech.service.dto.UsuarioDTO;
import br.com.oratech.service.interfaces.UsuarioService;
@Controller
@RequestMapping("/login")
public class LoginController extends AbstractCrudController<Usuario, UsuarioDTO> {
	@Autowired
	UsuarioService service;

	protected LoginController() {
		super("login", "form", "", Usuario.class, UsuarioDTO.class);
	}

	@Override
	protected CustomCrudService<Usuario, Long> getService() {
		return service;
	}
	
	//Spring Security see this :
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid username and password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.addObject("object", new UsuarioDTO());
		model.setViewName("login/form");

		return model;

	}
		
	public static float calculo(float fator1, float fator2){
		return fator1 + fator2;
	}
	

}
