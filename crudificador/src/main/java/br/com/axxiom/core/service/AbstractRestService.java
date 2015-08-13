package br.com.axxiom.core.service;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import br.com.axxiom.core.db.CustomIdentifiable;
import br.com.axxiom.core.service.interfaces.CustomCrudService;

public abstract class AbstractRestService<T extends CustomIdentifiable<D>, D extends Serializable> implements CustomCrudService<T,D> {

    public abstract RestTemplate getRestTemplate();

    protected abstract String getUrl();

    protected abstract Class<T> getEntityClass();

    @Override
    public T save(T bean) {
        URI uri = getRestTemplate().postForLocation(getUrl(), bean, String.class);
        return getRestTemplate().getForEntity(uri, getEntityClass()).getBody();
    }

    @Override
    public T get(D id) {
        return getRestTemplate().getForObject(getUrl() + "/{id}", getEntityClass(), id);
    }

    @Override
    public void delete(D id) {
        getRestTemplate().delete(getUrl() + "/{id}", id);
    }

    @Override
    public <F> void deleteByExample(F entity) {
        throw new NotYetImplementedException();
    }

    @Override
    public <F> List<T> find(F entity) {
        throw new NotYetImplementedException();
    }

    @Override
    public <F>  List<T> find(F entity, Sort sort) {
        throw new NotYetImplementedException();
    }

    @Override
    public <F> Page<T> find(F entity, Pageable page) {
        throw new NotYetImplementedException();
    }

    @Override
    public List<T> findAll() {
        throw new NotYetImplementedException();
    }

    @Override
    public List<T> findAll(Sort sort) {
        throw new NotYetImplementedException();
    }

    @Override
    public Page<T> findAll(Pageable page) {
        throw new NotYetImplementedException();
    }

    @Override
    public String getUsername() {
    	UserDetails details = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
    	return details.getUsername();
    }

}
