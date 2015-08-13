package br.com.axxiom.core.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.axxiom.core.db.CustomIdentifiable;
import br.com.axxiom.core.db.Identifiable;
import br.com.axxiom.core.exception.ResourceNotFoundException;
import br.com.axxiom.core.service.dto.MultiOrderedSearch;
import br.com.axxiom.core.service.dto.OrderedSearch;
import br.com.axxiom.core.service.interfaces.CustomCrudService;
import br.com.axxiom.core.web.ApiMapping;
import br.com.axxiom.core.web.DatatablePageInfo;
import br.com.axxiom.core.web.DatatablesResponse;

import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.base.CaseFormat;

/**
 * O Controller deve herdar esta classe caso ele apenas implemente operações de
 * pesquisa, e não altere os dados no banco. Nos casos onde o formulário de
 * pesquisa possuir os mesmos campos e validações que a entidade mapeada,
 * utilize o {@link AbstractSimpleListController}
 * 
 * @author Thiago Lage
 * 
 * @param <T>
 *            Entidade mapeada JPA
 * @param <F>
 *            Entidade que mapeia formulário de pesquisa
 * 
 * @see AbstractSimpleListController
 * @see AbstractCrudController
 * @see AbstractSimpleCrudController
 */
public abstract class AbstractCustomListController<T extends CustomIdentifiable<D>, F extends Serializable, D extends Serializable> extends
        BasicController<T, F, D> {
    protected static final String ELEMENTOS = "elementos";
    private final Log logger = LogFactory.getLog(getClass());
    private final String listView;
    private final F searchObject;
    private final T entity;
    private Boolean autosearch = false;
    private Boolean paged = false;
    private final String searchClassName;
    private final String entityClassName;
    protected static final String SEARCH_STRING = "ss";
    protected static final String SEARCH_STRING_ENCODED = "ssEncoded";
    protected static final String OBJECT = "object";
    protected static final String SEARCH_OBJECT = "searchobject";
    protected static final String MSGS = "msgs";
    protected static final String ERRORS = "errors";
    protected CustomCrudService<T, D> service;

    /**
     * Construtor caso seja necessário customizar o nome da view
     * 
     * @param viewPath
     *            Caminho para as views
     * @param listView
     *            Nome da view
     * @param entityClass
     *            Classe da entidade persistida
     * @param searchClass
     *            Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractListController(String, Class, Class)
     * @see AbstractSimpleListController
     */
    public AbstractCustomListController(String viewPath, String listView, Class<T> entityClass, Class<F> searchClass) {
        super(entityClass, searchClass);
        this.listView = "/" + viewPath + "/" + listView;
        T tempEntity = null;
        F search = null;
        try {
            tempEntity = entityClass.newInstance();
            search = searchClass.newInstance();
        } catch (Exception e) {
            logger.fatal("A entidade e o dto precisam ter um construtor público sem parametros");
        }
        this.entity = tempEntity;
        this.searchObject = search;
        this.searchClassName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, searchClass.getSimpleName());
        this.entityClassName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityClass.getSimpleName());
    }

    /**
     * Construtor que utiliza o nome default da view de listagem "list"
     * 
     * @param viewPath
     *            Caminho para as views
     * @param entityClass
     *            Classe da entidade persistida
     * @param searchClass
     *            Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractListController(String, String, Class, Class)
     * @see AbstractSimpleListController
     */
    public AbstractCustomListController(String viewPath, Class<T> entityClass, Class<F> dtoClass) {
        this(viewPath, "list", entityClass, dtoClass);
    }

    /**
     * Metodo para carregar a tela de pesquisa vazia. É mapeado para a url
     * /{entidade}/index.
     * 
     * @param model
     * @return view
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET, params = "!ss")
    public String listGet(Model model) {
        if (isAutosearch()) {
            return listPost(searchObject, new BeanPropertyBindingResult(searchObject, searchClassName), model);
        }
        model.addAttribute(ELEMENTOS, new ArrayList<Object>());
        model.addAttribute(searchClassName, searchObject);
        model.addAttribute(OBJECT, searchObject);
        model.addAttribute(SEARCH_STRING, "");
        model.addAttribute(SEARCH_STRING_ENCODED, "");
        listModelFiller(model);
        return listView;
    }

    /**
     * Metodo para executar a tela de pesquisa, via GET com os campos
     * preenchidos pelo parametro ss, que contem o formulário de pesquisa
     * serializado. É mapeado para a url /{entidade}/index?ss=.
     * 
     * @param ss
     *            entidade de pesquisa (searchDTO) serializada.
     * @param model
     * @return view
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET, params = "ss")
    public String listGet(@RequestParam String ss, Model model) {
        F search = fromString(ss, searchObject);
        if (search.equals(searchObject) && !isAutosearch()) {
            return listGet(model);
        } else {
            return listPost(search, new BeanPropertyBindingResult(search, searchClassName), model);
        }
    }

    /**
     * Metodo para executar a tela de pesquisa, via POST recebe o formulário de
     * pesquisa e executa a validação É mapeado para a url /{entidade}/index.
     * 
     * @param searchDTO
     *            Objeto que mapeia formulário de pesquisa
     * @param result
     *            Resultado da validação, que é acrescentado ao model para
     *            exibição de mensagens de erro na tela
     * @param model
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public String listPost(@Valid F searchDTO, BindingResult result, Model model) {
        model.addAttribute(searchClassName, searchDTO);
        model.addAttribute(OBJECT, searchDTO);
        listModelFiller(model);
        addParameters(searchDTO);
        if (result.hasErrors()) {
            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "object", result);
            return listView;
        } else {
            try {
                if (!getPaged()) {
                    doListPost(searchDTO, model);
                }
            } catch (Exception e) {
                logger.error(e.getCause());
                result.addError(new ObjectError("", "Erro ao realizar pesquisa."));
            }
        }
        model.addAttribute(SEARCH_STRING, toString(searchDTO));
        model.addAttribute(SEARCH_STRING_ENCODED, toEncodedString(searchDTO));
        return listView;
    }

    protected void addParameters(F searchDTO) {
    }

    /**
     * Metodo para exibição da tabela com paginação server-side, para utilizar
     * na tela, deve apontar para /{entidade}/page?ss={ss}
     * 
     * @param criterias
     *            Informações passadas automaticamente pelo datatables, contendo
     *            numero da página, registros por página,etc
     * @param ss
     *            formulario de pesquisa serializado
     * @param model
     * @return json com a lista de entidades da pagina segundo parametros
     *         recebidos
     */
    @RequestMapping(value = "/page")
    public @ResponseBody
    DatatablesResponse<T> page(@DatatablesParams DatatablesCriterias criterias, @RequestParam String ss, Model model) {
        Page<T> lst = new PageImpl<T>(new ArrayList<T>());
        if (!StringUtils.isEmpty(ss) || isAutosearch()) {
            F searchDTO = fromString(ss, searchObject);
            if (searchDTO != null) {
                lst = getService().find(searchDTO, new DatatablePageInfo<F>(criterias, searchDTO));
            }
        }
        return DatatablesResponse.build(lst, criterias);
    }

    /**
     * Metodo que executa a pesquisa efetivamente, pode ser sobrescrito caso
     * seja necessária alguma customização Para a ordenação, verifica primeiro
     * se o controller implementa {@link OrderedSearch} ou
     * {@link MultiOrderedSearch}. Caso não implemente, verifica se a entidade
     * de pesquisa implementa uma dessas interfaces, caso contrário, executa a
     * pesquisa sem ordenação
     * 
     * @param searchDTO
     *            Entidade contendo os parametros do formulário de pesquisa
     * @param model
     */
    protected void doListPost(F searchDTO, Model model) {
        try {
            if (this instanceof OrderedSearch) {
                doOrderedSearch((OrderedSearch) this, searchDTO, model);
            } else if (this instanceof MultiOrderedSearch) {
                model.addAttribute(ELEMENTOS, get(searchDTO, ((MultiOrderedSearch) this).getDirections()));
            } else if (searchDTO instanceof OrderedSearch) {
                doOrderedSearch((OrderedSearch) searchDTO, searchDTO, model);
            } else if (searchDTO instanceof MultiOrderedSearch) {
                model.addAttribute(ELEMENTOS, get(searchDTO, ((MultiOrderedSearch) searchDTO).getDirections()));
            } else {
                model.addAttribute(ELEMENTOS, getBySearchObject(searchDTO, null, null));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo para ser reescrito caso seja necessário acrescentar elementos
     * adicionais ao model da tela de pesquisa
     * 
     * @param model
     */
    protected void listModelFiller(Model model) {
    }

    /**
     * Operação REST de get por id
     * 
     * @param id
     *            id da entidade
     * @return Retorna o json com a entidade selecionada pelo id
     * @throws ResourceNotFoundException
     *             Caso o id não seja encontrado
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ApiMapping
    public final T get(@PathVariable D id) throws ResourceNotFoundException {
        T ret = getService().get(id);
        if (ret != null)
            return ret;
        else
            throw new ResourceNotFoundException();
    }

    /**
     * Operação REST para pesquisa de elementos sem nenhum tipo de filtro,
     * apenas ordenação
     * 
     * @param orderByAsc
     *            Parametro opcional, nome da propriedade para ordenar
     *            ascendente
     * @param orderByDesc
     *            Parametro opcional, nome da propriedade para ordenar
     *            descendente
     * @return Lista da entidade ordenada
     * @see #get(Identifiable, String, String)
     */
    @RequestMapping(value = "", params = "!_filtered_", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ApiMapping
    public final List<T> get(@RequestParam(required = false) String orderByAsc, @RequestParam(required = false) String orderByDesc) {
        List<T> response;
        if (orderByAsc != null) {
            Sort sort = new Sort(Sort.Direction.ASC, orderByAsc);
            response = getService().findAll(sort);
        } else if (orderByDesc != null) {
            Sort sort = new Sort(Sort.Direction.DESC, orderByDesc);
            response = getService().findAll(sort);
        } else {
            response = getService().findAll();
        }
        return response;
    }

    /**
     * Operação REST para pesquisa de elementos com filtro by example, e
     * ordenação. Deve-se passar na url o indicador que é filtrado. Ex.:
     * /{entidade}/?_filtered_&orderByAsc=prop&prop1=...
     * 
     * @param entidade
     *            Entidade para query by example
     * @param orderByAsc
     *            Parametro opcional, nome da propriedade para ordenar
     *            ascendente
     * @param orderByDesc
     *            Parametro opcional, nome da propriedade para ordenar
     *            descendente
     * @return Lista da entidade ordenada e filtrada
     * @see AbstractCustomListController#get(String, String)
     */
    @RequestMapping(value = "", params = "_filtered_", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ApiMapping
    public final List<T> get(@ModelAttribute T entidade, @RequestParam(required = false) String orderByAsc,
            @RequestParam(required = false) String orderByDesc) {
        List<T> response;
        if (orderByAsc != null) {
            Sort sort = new Sort(Sort.Direction.ASC, orderByAsc);
            response = getService().find(entidade, sort);
        } else if (orderByDesc != null) {
            Sort sort = new Sort(Sort.Direction.DESC, orderByDesc);
            response = getService().find(entidade, sort);
        } else {
            response = getService().find(entidade);
        }
        return response;
    }

    protected final T getEntity() {
        return entity;
    }

    protected final String getEntityClassName() {
        return entityClassName;
    }

    protected final F getSearchObject() {
        return searchObject;
    }

    protected final String getSearchClassName() {
        return searchClassName;
    }

    public String getListView() {
        return listView;
    }

    private final List<T> get(F searchDTO, List<Order> orders) {
        return getService().find(searchDTO, new Sort(orders));
    }

    private void doOrderedSearch(OrderedSearch os, F searchDTO, Model model) {
        if (os.getDirection().equals(Sort.Direction.ASC)) {
            model.addAttribute(ELEMENTOS, getBySearchObject(searchDTO, os.getSortedProperty(), null));
        } else {
            model.addAttribute(ELEMENTOS, getBySearchObject(searchDTO, null, os.getSortedProperty()));
        }
    }

    private final List<T> getBySearchObject(@ModelAttribute F searchDTO, String orderByAsc, String orderByDesc) {
        List<T> response;
        if (orderByAsc != null) {
            Sort sort = new Sort(Sort.Direction.ASC, orderByAsc);
            sort.getOrderFor(orderByAsc).ignoreCase();
            response = getService().find(searchDTO, sort);
        } else if (orderByDesc != null) {
            Sort sort = new Sort(Sort.Direction.DESC, orderByDesc);
            sort.getOrderFor(orderByDesc).ignoreCase();
            response = getService().find(searchDTO, sort);
        } else {
            response = getService().find(searchDTO);
        }
        return response;
    }

    @ModelAttribute("pagePath")
    public final String getPagePath() {
        return getBasePath() + "/page";
    }

    @ModelAttribute("listPath")
    public final String getListPath() {
        return getBasePath() + "/index";
    }

    protected abstract CustomCrudService<T, D> getService();

    private final Boolean isAutosearch() {
        return autosearch;
    }

    /**
     * Metodo que habilita a pesquisa automatica, tanto na paginação quando na
     * url index?ss Isso significa que não é necessário executar o post do
     * formulário de pesquisa. Deve ser usado no construtor dos controllers que
     * não possuem formuláro de pesquisa.
     */
    public void enableAutoSearch() {
        this.autosearch = true;
    }

    public void setService(CustomCrudService<T, D> service) {
        this.service = service;
    }

    public Boolean getPaged() {
        return paged;
    }

    public void setPaged(Boolean paged) {
        this.paged = paged;
    }

}
