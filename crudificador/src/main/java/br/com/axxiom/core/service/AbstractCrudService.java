package br.com.axxiom.core.service;

import br.com.axxiom.core.db.Identifiable;
import br.com.axxiom.core.service.interfaces.CrudService;

public abstract class AbstractCrudService<T extends Identifiable> extends AbstractCustomCrudService<T, Long> implements CrudService<T> {

	protected AbstractCrudService(Class<T> entityClass) {
		super(entityClass);
	}


}
