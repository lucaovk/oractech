package br.com.axxiom.core.web.controller;

import br.com.axxiom.core.db.Identifiable;

public abstract class AbstractSimpleListController<T extends Identifiable> extends AbstractListController<T, T> {

    public AbstractSimpleListController(String viewPath, String listView, Class<T> entityClass) {
        super(viewPath, listView, entityClass, entityClass);
    }

    public AbstractSimpleListController(String viewPath, Class<T> entityClass) {
        this(viewPath, "list", entityClass);
    }

}
