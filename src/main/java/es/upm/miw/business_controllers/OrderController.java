package es.upm.miw.business_controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import es.upm.miw.business_services.RestBuilder;
import es.upm.miw.business_services.RestService;
import es.upm.miw.documents.Order;
import es.upm.miw.documents.OrderLine;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;
import es.upm.miw.exceptions.NotFoundException;
import es.upm.miw.repositories.OrderRepository;

@Controller
public class OrderController {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RestService restService;

	@Value("${provider.microservice}")
	private String providerMicroservice;

	@Value("${article.microservice}")
	private String articleMicroservice;

	public List<OrderSearchDto> readAll() {
		List<OrderSearchDto> orderSearchDtos = new ArrayList<>();
		for (OrderDto dto : orderRepository.findAllOrdersByOpeningDateDesc()) {
			for (OrderLine orderLine : dto.getOrderLines()) {
				OrderSearchDto orderSearchDto = new OrderSearchDto(dto.getId(), dto.getDescription(),
						orderLine.getArticleId(), orderLine.getRequiredAmount(), orderLine.getFinalAmount(),
						dto.getOpeningDate(), dto.getClosingDate());
				orderSearchDtos.add(orderSearchDto);
			}
		}
		return orderSearchDtos;
	}

	public OrderDto create(String descriptionOrder, String providerId, String[] idArticles, Integer[] requiredAmount,
			String token) {
		checkProviderId(providerId, token);
		OrderLine[] orderLines = new OrderLine[idArticles.length];
		for (int i = 0; i < idArticles.length; i++) {
			if (checkArticleId(idArticles[i], token)) {
				orderLines[i] = new OrderLine(idArticles[i], requiredAmount[i]);
			}
		}

		Order order = new Order(descriptionOrder, providerId, orderLines);
		this.orderRepository.save(order);
		return new OrderDto(order);
	}

	public void delete(String id) {
		Optional<Order> order = this.orderRepository.findById(id);
		if (order.isPresent()) {
			this.orderRepository.delete(order.get());
		}
	}

	private boolean checkProviderId(String providerId, String token) {
		if (providerId == null || providerId == "") {
			return false;
		}

		try {
			return this.restService.setToken(token).restBuilder(new RestBuilder<Boolean>(providerMicroservice)).heroku()
					.clazz(Boolean.class).path("/providers/" + providerId + "/validate").get().build();
		} catch (Exception e) {
			throw new NotFoundException("Product id (" + providerId + ") does not exist");
		}
	}

	private boolean checkArticleId(String articleId, String token) {
		if (articleId == null || articleId == "") {
			return false;
		}

		try {
			return this.restService.setToken(token).restBuilder(new RestBuilder<Boolean>(articleMicroservice)).heroku()
					.clazz(Boolean.class).path("/articles/" + articleId + "/validate").get().build();
		} catch (Exception e) {
			throw new NotFoundException("Article id (" + articleId + ") does not exist");
		}
	}
}
