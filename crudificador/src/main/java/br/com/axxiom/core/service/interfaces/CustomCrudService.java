package br.com.axxiom.core.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface CustomCrudService<T, D> {
    T save(T bean);

    T get(D id);

    void delete(D id);

    <F> void deleteByExample(F entity);

    <F> List<T> find(F entity);

    <F> List<T> find(F entity, Sort sort);

    <F> Page<T> find(F entity, Pageable page);

    List<T> findAll();

    List<T> findAll(Sort sort);

    Page<T> findAll(Pageable page);

    String getUsername();

}
