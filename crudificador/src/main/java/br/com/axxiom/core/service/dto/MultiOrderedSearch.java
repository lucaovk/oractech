package br.com.axxiom.core.service.dto;

import java.util.List;

import org.springframework.data.domain.Sort.Order;

public interface MultiOrderedSearch {

    List<Order> getDirections();
}
