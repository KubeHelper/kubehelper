package com.kubehelper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KubeHelperController {

	@GetMapping("/index")
	public String index() {
		return "index";
	}

}
