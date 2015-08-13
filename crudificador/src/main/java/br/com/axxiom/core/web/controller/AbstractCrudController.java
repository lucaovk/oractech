package br.com.axxiom.core.web.controller;


import java.io.Serializable;

import br.com.axxiom.core.db.Identifiable;
/**
 * Essa classe implementa operações básicas de CRUD, assim como a lógica de navegação entre tela de pesquisa e
 * telas de edição/criação. Caso não exista necessidade de uma classe especifica para mapear o formulário de pesquisa
 * utilize {@link AbstractSimpleCrudController}
 * @author Thiago Lage
 *
 * @param <T> Entidade mapeada JPA
 * @param <F> Entidade que mapeia formulário de pesquisa
 * 
 * @see AbstractSimpleListController
 * @see AbstractCrudController
 * @see AbstractSimpleCrudController
 */
public abstract class AbstractCrudController<T extends Identifiable, F extends Serializable> extends AbstractCustomCrudController<T, F, Long> {
    /**
     * Construtor utilizado caso seja necessário customizar o nome das views de listagem e formulario
     * @param viewPath Caminho para as views
     * @param formView Nome da view do formulário
     * @param listView Nome da view de listagem
     * @param entityClass Classe da entidade persistida
     * @param searchClass Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractCrudController(String, Class, Class)
     */
    protected AbstractCrudController(String viewPath, String formView, String listView, Class<T> entityClass, Class<F> searchClass) {
        super(viewPath, formView, listView, entityClass, searchClass);
    }

    /**
     * Construtor que utiliza o nome default das views "list" e "form"
     * @param viewPath Caminho para as views
     * @param entityClass Classe da entidade persistida
     * @param searchClass Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractCrudController(String, String, String, Class, Class)
     */
    protected AbstractCrudController(String viewPath, Class<T> entityClass, Class<F> searchClass) {
        super(viewPath, entityClass, searchClass);
    }


}
