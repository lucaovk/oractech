package br.com.axxiom.core.web.controller;

import br.com.axxiom.core.db.Identifiable;

public abstract class AbstractSimpleCrudController<T extends Identifiable> extends AbstractCrudController<T, T> {

    public AbstractSimpleCrudController(String viewPath, String formView, String listView, Class<T> entityClass) {
        super(viewPath, formView, listView, entityClass, entityClass);
    }

    public AbstractSimpleCrudController(String viewPath, Class<T> entityClass) {
        this(viewPath, "form", "list", entityClass);
    }

    @Override
    protected void entityPreSaveFillerHook(T entity, T searchEntity) {
        entityPreSaveFillerHook(entity);
    }

    protected void entityPreSaveFillerHook(T entity) {

    }
}
