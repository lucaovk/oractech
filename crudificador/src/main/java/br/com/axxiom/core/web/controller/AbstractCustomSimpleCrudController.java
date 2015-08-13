package br.com.axxiom.core.web.controller;

import java.io.Serializable;

import br.com.axxiom.core.db.CustomIdentifiable;

public abstract class AbstractCustomSimpleCrudController<T extends CustomIdentifiable<D>, D extends Serializable> extends AbstractCustomCrudController<T, T, D> {

    protected AbstractCustomSimpleCrudController(String viewPath, String formView, String listView, Class<T> entityClass) {
        super(viewPath, formView, listView, entityClass, entityClass);
    }

    protected AbstractCustomSimpleCrudController(String viewPath, Class<T> entityClass) {
        this(viewPath, "form", "list", entityClass);
    }

    @Override
    protected void entityPreSaveFillerHook(T entity, T searchEntity) {
        entityPreSaveFillerHook(entity);
    }

    protected void entityPreSaveFillerHook(T entity) {

    }
}
