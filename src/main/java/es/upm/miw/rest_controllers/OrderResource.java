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
import es.upm.miw.dtos.OrderArticleDto;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(OrderResource.ORDERS)

public class OrderResource {
	public static final String ORDERS = "/orders";
	public static final String ID = "/{id}";
	public static final String SEARCH = "/search";
	public static final String CLOSE = "/close";
	public static final String ORDERS_art = "/art";
	public static final String ARTICLE = "/article";
	public static final String ORDER_ID = "/{idOrder}";

	@Autowired
	private OrderController orderController;

	@GetMapping
	public List<OrderSearchDto> readAll() {
		return this.orderController.readAll();
	}

	@PostMapping()
	public OrderDto createOrder(@Valid @RequestBody OrderArticleDto[] articleDto,
			@RequestHeader("Authorization") String token) {
		int size = articleDto.length;
		String[] articlesId = new String[size];
		Integer[] requiredAmount = new Integer[size];
		String desc = "ORDER-" + String.valueOf((int) (Math.random() * 10000));
		String idProvider = "";
		int i = 0;
		for (OrderArticleDto dto : articleDto) {
			articlesId[i] = dto.getCode();
			requiredAmount[i] = dto.getAmount();
			idProvider = dto.getProviderId();
			i++;
		}
		return this.orderController.create(desc, idProvider, articlesId, requiredAmount, token);
	}

	/*
	 * @PostMapping(value = CLOSE) public List<OrderDto>
	 * closeOrder(@Valid @RequestBody OrderDto orderDto) {
	 * System.out.println("Order: " + orderDto); List<OrderDto> closedOrder = new
	 * ArrayList<>(); if (orderDto.getOrderLines() == null) { throw new
	 * BadRequestException("orderLine is empty"); } else { closedOrder.add(new
	 * OrderDto(this.orderController.closeOrder(orderDto.getId(),
	 * orderDto.getOrderLines()))); } return closedOrder; }
	 * 
	 * /*@PostMapping(value = SEARCH) public List<OrderSearchDto>
	 * findByAttributesLike(@Valid @RequestBody OrderSearchInputDto
	 * orderSearchInputDto) { String descriptionOrders =
	 * orderSearchInputDto.getDescriptionOrders(); String descriptionArticles =
	 * orderSearchInputDto.getDescriptionArticles(); Boolean onlyClosingDate =
	 * orderSearchInputDto.isOnlyClosingDate(); return
	 * this.orderController.searchOrder(descriptionOrders, descriptionArticles,
	 * onlyClosingDate); }
	 * 
	 * @GetMapping(value = ID) public List<OrderSearchDto> read(@PathVariable String
	 * id) { return this.orderController.findByDescription(id); }
	 * 
	 * @PostMapping(value = ARTICLE) public List<OrderArticleDto>
	 * findById(@Valid @RequestBody OrderDto orderDto) {
	 * System.out.println("find By id: " + orderDto.getId()); return
	 * this.orderController.findById(orderDto.getId()); }
	 * 
	 * @DeleteMapping(value = ORDER_ID) public void delete(@PathVariable String
	 * idOrder) { this.orderController.delete(idOrder); }
	 */
}