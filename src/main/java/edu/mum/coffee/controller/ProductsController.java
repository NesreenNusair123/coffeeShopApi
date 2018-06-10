package edu.mum.coffee.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import edu.mum.coffee.domain.*;
import edu.mum.coffee.service.ProductService;


@RestController
@RequestMapping("/products")
public class ProductsController{
	@Autowired
	private ProductService service;
	
	
	@RequestMapping(value = "*", method = RequestMethod.GET)
	public List<Product> redirectDefault() {
		return get();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Product> get() {
		return service.getAllProduct();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Product get(@PathVariable int id) {
		return service.getProduct(id);
	}

	@RequestMapping(value = "*", method = RequestMethod.POST)
	public Product redirectDefault(@Valid Product entity, BindingResult result, HttpServletResponse response) {
		return post(entity, result, response);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Product post(@RequestBody @Valid Product entity, BindingResult result, HttpServletResponse response) {
		if (result.hasErrors()) {
			return unprocessableEntity(response);
		}

		return service.save(entity);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Product put(@PathVariable int id, @RequestBody @Valid Product entity, BindingResult result, HttpServletResponse response) {
		if (result.hasErrors()) {
			return unprocessableEntity(response);
		}

		if (service.getProduct(id) == null) {
			return notFound(response);
		}
		
		entity.setId(id);
		return service.save(entity);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id, HttpServletResponse response) {
		Product entity = service.getProduct(id);

		if (entity == null) {
			notFound(response);
			return;
		}

		service.delete(entity);
	}

	public Product unprocessableEntity(HttpServletResponse response) {
		response.setStatus(422);
		return null;
	}

	public Product notFound(HttpServletResponse response) {
		response.setStatus(404);
		return null;
	}
}
