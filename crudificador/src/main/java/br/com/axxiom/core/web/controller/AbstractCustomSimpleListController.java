package br.com.axxiom.core.web.controller;

import java.io.Serializable;

import br.com.axxiom.core.db.CustomIdentifiable;

public abstract class AbstractCustomSimpleListController<T extends CustomIdentifiable<D>, D extends Serializable> extends AbstractCustomListController<T, T, D> {

    public AbstractCustomSimpleListController(String viewPath, String listView, Class<T> entityClass) {
        super(viewPath, listView, entityClass, entityClass);
    }

    public AbstractCustomSimpleListController(String viewPath, Class<T> entityClass) {
        this(viewPath, "list", entityClass);
    }

}
