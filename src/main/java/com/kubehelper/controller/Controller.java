package com.kubehelper.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {

	@GetMapping("/mvvm")
	public String mvvmExample() {
		return "mvvm";
	}

	@GetMapping("/resources")
	public String resourcesExample() {
		return "resources";
	}

}
