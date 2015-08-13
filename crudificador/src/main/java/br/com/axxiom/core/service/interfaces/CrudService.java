package br.com.axxiom.core.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface CrudService<T> extends CustomCrudService<T, Long> {


}
