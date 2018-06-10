package edu.mum.coffee.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import edu.mum.coffee.domain.*;
import edu.mum.coffee.service.PersonService;


@RestController
@RequestMapping("/people")
public class PeopleController {
	
	@Autowired
	private PersonService service;
	
	@RequestMapping(value = "*", method = RequestMethod.GET)
	public List<Person> redirectDefault() {
		return get();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Person> get() {
		return service.getAllPeople();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Person get(@PathVariable long id) {
		return service.findById(id);
	}

	@RequestMapping(value = "*", method = RequestMethod.POST)
	public Person redirectDefault(@Valid Person entity, BindingResult result, HttpServletResponse response) {
		return post(entity, result, response);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Person post(@RequestBody @Valid Person entity, BindingResult result, HttpServletResponse response) {
		if (result.hasErrors()) {
			return unprocessableEntity(response);
		}

		return service.savePerson(entity);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Person put(@PathVariable long id, @RequestBody @Valid Person entity, BindingResult result, HttpServletResponse response) {
		if (result.hasErrors()) {
			return unprocessableEntity(response);
		}

		if (service.findById(id) == null) {
			return notFound(response);
		}
		
		entity.setId(id);
		return service.savePerson(entity);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable long id, HttpServletResponse response) {
		Person entity = service.findById(id);

		if (entity == null) {
			notFound(response);
			return;
		}

		service.removePerson(service.findById(id));
	}

	public Person unprocessableEntity(HttpServletResponse response) {
		response.setStatus(422);
		return null;
	}

	public Person notFound(HttpServletResponse response) {
		response.setStatus(404);
		return null;
	}

	@PostMapping({"/getByEmail"})
	public Person getByEmail(@RequestBody String email) {
		return service.findByEmail(email).stream().findFirst().orElse(null);
	}
	
	@PostMapping({"/login"})
	public Person login(@RequestBody Person user) {
		List<Person> personDb = service.findByEmail(user.getEmail());
		return personDb.stream().filter(p -> p.getPassword().equals(user.getPassword())).findFirst().orElse(null);
	}
	
	@PostMapping({"/admin"})
	public Person postAdmin(@RequestBody @Valid Person entity, BindingResult result, HttpServletResponse response) {
		if (result.hasErrors()) {
			return unprocessableEntity(response);
		}
		
		entity.setAdmin(true);
		
		return service.savePerson(entity);
	}
}
