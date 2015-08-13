package br.com.axxiom.core.web.controller;

import java.io.Serializable;

import br.com.axxiom.core.db.Identifiable;
import br.com.axxiom.core.service.interfaces.CrudService;

/**
 * O Controller deve herdar esta classe caso ele apenas implemente operações de pesquisa, 
 * e não altere os dados no banco. Nos casos onde o formulário de pesquisa possuir os mesmos
 * campos e validações que a entidade mapeada, utilize o {@link AbstractSimpleListController}
 * 
 * @author Thiago Lage
 *
 * @param <T> Entidade mapeada JPA
 * @param <F> Entidade que mapeia formulário de pesquisa
 * 
 * @see AbstractSimpleListController
 * @see AbstractCrudController
 * @see AbstractSimpleCrudController
 */
public abstract class AbstractListController<T extends Identifiable, F extends Serializable> extends AbstractCustomListController<T, F, Long> {

	@Override
	protected abstract CrudService<T> getService();
	
    /**
     * Construtor caso seja necessário customizar o nome da view
     * @param viewPath Caminho para as views
     * @param listView Nome da view 
     * @param entityClass Classe da entidade persistida
     * @param searchClass Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractListController(String, Class, Class)
     * @see AbstractSimpleListController
     */
    public AbstractListController(String viewPath, String listView, Class<T> entityClass, Class<F> searchClass) {
        super(viewPath, listView, entityClass, searchClass);
    }

    /**
     * Construtor que utiliza o nome default da view de listagem "list"
     * @param viewPath Caminho para as views
     * @param entityClass Classe da entidade persistida
     * @param searchClass Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractListController(String, String, Class, Class)
     * @see AbstractSimpleListController
     */
    public AbstractListController(String viewPath, Class<T> entityClass, Class<F> dtoClass) {
        super(viewPath, entityClass, dtoClass);
    }

}
