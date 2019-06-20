package es.upm.miw.rest_controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upm.miw.business_controllers.OrderController;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(OrderResource.ORDERS)

public class OrderResource {
	public static final String ORDERS = "/orders";
	public static final String ID = "/{id}";

	@Autowired
	private OrderController orderController;

	@GetMapping
	public List<OrderSearchDto> readAll() {
		return this.orderController.readAll();
	}

	@PostMapping()
	public OrderDto create(@Valid @RequestBody OrderDto orderDto, @RequestHeader("Authorization") String token) {
		return this.orderController.create(orderDto, token);
	}
}