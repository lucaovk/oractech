package br.com.axxiom.core.service.dto;

import org.springframework.data.domain.Sort;

public interface OrderedSearch {

    Sort.Direction getDirection();

    String getSortedProperty();
}
