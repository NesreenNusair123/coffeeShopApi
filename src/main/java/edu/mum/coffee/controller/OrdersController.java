package edu.mum.coffee.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import edu.mum.coffee.domain.*;
import edu.mum.coffee.service.OrderService;
import edu.mum.coffee.service.PersonService;

@RestController
@RequestMapping("/orders")
public class OrdersController {
	
	OrderService orderService;
	PersonService personService;
	
	public OrdersController(OrderService service, PersonService personService) {
		
		this.orderService = service;
		this.personService = personService;
	}
	
	
	
	@RequestMapping(value = "*", method = RequestMethod.GET)
	public List<Order> redirectDefault() {
		return get();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Order> get() {
		return orderService.findAll();
	}

	

	@RequestMapping(value = "*", method = RequestMethod.POST)
	public Order redirectDefault(@Valid Order entity, BindingResult result, HttpServletResponse response) {
		return post(entity, result, response);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Order post(@RequestBody @Valid Order entity, BindingResult result, HttpServletResponse response) {
		for(Orderline l : entity.getOrderLines()) {
			l.setOrder(entity);
		}
		
		orderService.save(entity);

		for(Orderline l : entity.getOrderLines()) {
			l.setOrder(null);
		}
		
		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Order put(@PathVariable int id, @RequestBody @Valid Order entity, BindingResult result, HttpServletResponse response) {
		if (result.hasErrors()) {
			return unprocessableEntity(response);
		}

		if (orderService.findById(id) == null) {
			return notFound(response);
		}
		entity.setId(id);
		return orderService.save(entity);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id, HttpServletResponse response) {
		Order entity = orderService.findById(id);

		if (entity == null) {
			notFound(response);
			return;
		}

		orderService.delete(entity);
	}

	public Order unprocessableEntity(HttpServletResponse response) {
		response.setStatus(422);
		return null;
	}

	public Order notFound(HttpServletResponse response) {
		response.setStatus(404);
		return null;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Order get(@PathVariable int id) {
		Order order = orderService.findById(id);

		for(Orderline l : order.getOrderLines()) {
			l.setOrder(null);
		}
		
		return order;
	}

	@RequestMapping(value = "/getByPerson/{personId}", method = RequestMethod.GET)
	public List<Order> getByPerson(@PathVariable long personId) {
		Person p = personService.findById(personId);
		List<Order> orders = orderService.findByPerson(p);

		for(Order o : orders) {
			for(Orderline l : o.getOrderLines()) {
				l.setOrder(null);
			}
		}
		
		return orders;
	}
}
