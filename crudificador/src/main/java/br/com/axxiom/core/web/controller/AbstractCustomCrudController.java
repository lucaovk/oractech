package br.com.axxiom.core.web.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.axxiom.core.db.CustomIdentifiable;
import br.com.axxiom.core.exception.ResourceNotFoundException;
import br.com.axxiom.core.web.ApiMapping;

/**
 * Essa classe implementa operações básicas de CRUD, assim como a lógica de
 * navegação entre tela de pesquisa e telas de edição/criação. Caso não exista
 * necessidade de uma classe especifica para mapear o formulário de pesquisa
 * utilize {@link AbstractSimpleCrudController}
 * 
 * @author Thiago Lage
 * 
 * @param <T>
 *            Entidade mapeada JPA
 * @param <F>
 *            Entidade que mapeia formulário de pesquisa
 * 
 * @see AbstractSimpleListController
 * @see AbstractCustomCrudController
 * @see AbstractSimpleCrudController
 */
public abstract class AbstractCustomCrudController<T extends CustomIdentifiable<D>, F extends Serializable, D extends Serializable> extends
        AbstractCustomListController<T, F, D> {

    private final String formView;
    private String editMessage = "Item alterado com sucesso";
    private String createMessage = "Item criado com sucesso";
    private String deleteMessage = "Item excluido com sucesso";
    private String deleteErrorMessage = "Erro na exclusão do item";

    /**
     * Construtor utilizado caso seja necessário customizar o nome das views de
     * listagem e formulario
     * 
     * @param viewPath
     *            Caminho para as views
     * @param formView
     *            Nome da view do formulário
     * @param listView
     *            Nome da view de listagem
     * @param entityClass
     *            Classe da entidade persistida
     * @param searchClass
     *            Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractCrudController(String, Class, Class)
     */
    protected AbstractCustomCrudController(String viewPath, String formView, String listView, Class<T> entityClass, Class<F> searchClass) {
        super(viewPath, listView, entityClass, searchClass);
        this.formView = "/" + viewPath + "/" + formView;
    }

    /**
     * Construtor que utiliza o nome default das views "list" e "form"
     * 
     * @param viewPath
     *            Caminho para as views
     * @param entityClass
     *            Classe da entidade persistida
     * @param searchClass
     *            Classe da entidade que mapeia o formulário de pesquisa
     * @see #AbstractCrudController(String, String, String, Class, Class)
     */
    protected AbstractCustomCrudController(String viewPath, Class<T> entityClass, Class<F> searchClass) {
        this(viewPath, "form", "list", entityClass, searchClass);
    }

    /**
     * Mapeia a url /{entidade}/edit/{id}. Para tela de edição.
     * 
     * @param id
     *            Id da entidade
     * @param ss
     *            Formulário de pesquisa serializado
     * @param model
     * @return form view
     * @throws NoSuchRequestHandlingMethodException
     */
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable D id, @RequestParam(defaultValue = "") String ss, Model model) throws ResourceNotFoundException {
        T entity = get(id);
        F search = getSearchObject();
        if (!ss.equals("")) {
            search = (F) fromString(ss);
        }
        return edit(model, entity, search);
    }

    /**
     * Método para preencher o model do formulário de edição
     * 
     * @param model
     * @param entity
     *            Entidade a ser editada
     * @param search
     *            Objeto mapeando formulário de pesquisa
     * @return form view
     */
    private String edit(Model model, T entity, F search) {
        model.addAttribute(getEntityClassName(), entity);
        model.addAttribute(OBJECT, entity);
        model.addAttribute(SEARCH_STRING, toString(search));
        model.addAttribute(SEARCH_OBJECT, search);
        formModelFiller(model);
        return formView;
    }

    /**
     * Mapeia a url /{entidade}/create. Para tela de criação.
     * 
     * @param ss
     *            Formulário de pesquisa serializado
     * @param model
     * @return form view
     * @see #create(Serializable, Model)
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam(defaultValue = "") String ss, Model model) {
        F search = (F) fromString(ss, getSearchObject());
        return create(search, model);
    }

    /**
     * Metodo que preenche o model com os dados para criação de novo elemento.
     * 
     * @param search
     *            Objeto com parametros de pesquisa da tela anterior
     * @param model
     * @return form view
     */
    protected String create(F search, Model model) {
        T entity = newEntityFillerHook(search);
        return edit(model, entity, search);
    }

    /**
     * Sobrescreva este método caso seja necessário acrescentar elementos ao
     * model da tela de criação e edição, como por exemplo listas de combos.
     * 
     * @param model
     */
    protected void formModelFiller(Model model) {
    }

    /**
     * Sobrescreva este metodo caso a entidade nova tenha algum valor
     * préviamente definido, para ser utilizado na tela de criação
     * 
     * @param searchObject
     *            Entidade preenchida com os dados da pesquisa
     * @return Entidade com os valores preenchidos
     */
    protected T newEntityFillerHook(F searchObject) {
        return this.getEntity();
    }

    /**
     * Metodo executado no post do formulário de edição/criação da entidade,
     * para persisti-la
     * 
     * @param ss
     *            Formulário da tela de pesquisa serializado
     * @param entidade
     *            Entidade preenchida com os dados do formulário de edição
     * @param bindingResult
     * @param model
     * @return Retorna form view em caso de erro e list view em caso de sucesso
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam(defaultValue = "") String ss, @Valid T entidade, BindingResult bindingResult,
            RedirectAttributes modelRedirect, Model model) {
        F search = getSearchObject();
        boolean isNew = false;
        if (!ss.equals("")) {
            search = (F) fromString(ss);
        }
        if (bindingResult.hasErrors() || !preSaveValidation(entidade, search, model)) {
            model.addAttribute(getEntityClassName(), entidade);
            model.addAttribute(SEARCH_STRING, toString(search));
            model.addAttribute(OBJECT, entidade);
            model.addAttribute(SEARCH_OBJECT, search);
            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + OBJECT, bindingResult);
            formModelFiller(model);
            return formView;
        }
        if (entidade.getId() == null) {
            isNew = true;
        }
        entityPreSaveFillerHook(entidade, search);
        saveOrUpdate(entidade, model);
        List<String> msgs = Arrays.asList(isNew ? getCreateMessage() : getEditMessage());
        modelRedirect.addFlashAttribute(MSGS, msgs);
        return listPostFromAction(search, entidade, isNew ? ACTION.CREATE : ACTION.EDIT, modelRedirect);
    }

    private boolean preSaveValidation(T entidade, F search, Model model) {
        return true;
    }

    /**
     * Metodo que pode ser sobrescrito caso o comportamento da lista após salvar
     * o objeto seja diferente, por exemplo alterar o redirecionamento da lista
     * para outra tela
     * 
     * @param search
     *            Entidade representando o formulário de pesquisa
     * @param bindingResult
     * @param model
     * @return list view
     */
    protected String listPostFromSave(F search, RedirectAttributes model) {
        return "redirect:" + getListPath() + "?ss=" + toEncodedString(search);
    }

    /**
     * Metodo que pode ser sobrescrito caso o comportamento da lista após
     * salvar/deletar o objeto seja diferente, por exemplo alterar o
     * redirecionamento da lista para outra tela
     * 
     * @param search
     *            Entidade representando o formulário de pesquisa
     * @param bindingResult
     * @param isNew
     *            informa se o elemento sendo salvo é novo ou está sendo editado
     * @param model
     * @return list view
     */
    protected String listPostFromAction(F search, ACTION action, RedirectAttributes model) {
        return listPostFromSave(search, model);
    }

    /**
     * Metodo que pode ser sobrescrito caso o comportamento da lista após
     * salvar/deletar o objeto seja diferente, por exemplo alterar o
     * redirecionamento da lista para outra tela
     * 
     * @param search
     *            Entidade representando o formulário de pesquisa
     * @param entidade
     *            Entidade representando o formulario de edição
     * @param bindingResult
     * @param isNew
     *            informa se o elemento sendo salvo é novo ou está sendo editado
     * @param model
     * @return list view
     */
    protected String listPostFromAction(F search, T entidade, ACTION action, RedirectAttributes model) {
        return listPostFromAction(search, action, model);
    }

    /**
     * Sobrescreva este metodo caso precise preencher outras informações da
     * entidade, que não estão no formulário
     * 
     * @param entity
     *            Entidade preenchida com os dados do formulário
     * @param searchObject
     *            Parametros de pesquisa utilizados antes de chegar na tela de
     *            edição/criação
     */
    protected void entityPreSaveFillerHook(T entity, F searchObject) {
    }

    /**
     * Metodo que mapeia /{entidade}/delete/{id} para excusão de entidade
     * 
     * @param id
     *            Id da entidade
     * @param ss
     *            Formulário de pesquisa serializado
     * @param model
     * @return list view
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable D id, @RequestParam(defaultValue = "") String ss, RedirectAttributes model) {
        F search = getSearchObject();
        if (!ss.equals("")) {
            search = (F) fromString(ss);
        }
        try {
            if (isRemovable(id, search)) {
                delete(id);
                List<String> msgs = Arrays.asList(getDeleteMessage());
                model.addFlashAttribute(MSGS, msgs);
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            List<String> msgs = Arrays.asList(getDeleteErrorMessage());
            model.addFlashAttribute(ERRORS, msgs);
        }
        return listPostFromAction(search, ACTION.DELETE, model);
    }

    protected boolean isRemovable(D id, F search) {
        return true;
    }

    /**
     * Metodo REST para inclusão/alteração de entidade (post/put)
     * 
     * @param entidade
     * @param model
     * @return
     */
    @ApiMapping
    @RequestMapping(value = "", params = "!_list_", method = { RequestMethod.POST, RequestMethod.PUT }, produces = "application/json")
    @ResponseBody
    public final T saveOrUpdate(@Validated @RequestBody T entidade, Model model) {
        return getService().save(entidade);
    }

    /**
     * Metodo rest para inclusão/alteração em massa (put), na url deverá ser
     * passado o parâmetro _list_
     * 
     * @param entidades
     * @param result
     * @param model
     * @return
     */
    @ApiMapping
    @RequestMapping(value = "", params = "_list_", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public final List<T> update(@Validated @RequestBody List<T> entidades, Model model) {
        for (T entidade : entidades) {
            getService().save(entidade);
        }
        return entidades;
    }

    /**
     * Metodo REST para exclusão de item (DELETE)
     * 
     * @param id
     */
    @ApiMapping
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public final void delete(@PathVariable D id) {
        getService().delete(id);
    }

    /**
     * Metodo REST para exclusão by example, deve-se passar além das
     * propriedades da entidade, o parametro _filtered_
     * 
     * @param entity
     *            Entidade utilizada no by Example
     */
    @ApiMapping
    @RequestMapping(value = "", params = "_filtered_", method = RequestMethod.DELETE, produces = "application/json")
    public final void delete(@Valid @RequestBody T entity) {
        getService().deleteByExample(entity);
    }

    public String getFormView() {
        return formView;
    }

    @ModelAttribute("savePath")
    public final String getSavePath() {
        return getBasePath() + "/save";
    }

    @ModelAttribute("editPath")
    public final String getEditPath() {
        return getBasePath() + "/edit/";
    }

    @ModelAttribute("createPath")
    public final String getCreatePath() {
        return getBasePath() + "/create";
    }

    @ModelAttribute("deletePath")
    public final String getdeletePath() {
        return getBasePath() + "/delete/";
    }

    protected String getEditMessage() {
        return editMessage;
    }

    protected void setEditMessage(String editMessage) {
        this.editMessage = editMessage;
    }

    protected String getCreateMessage() {
        return createMessage;
    }

    protected void setCreateMessage(String createMessage) {
        this.createMessage = createMessage;
    }

    protected String getDeleteMessage() {
        return deleteMessage;
    }

    protected void setDeleteMessage(String deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    protected String getDeleteErrorMessage() {
        return deleteErrorMessage;
    }

    protected void setDeleteErrorMessage(String deleteErrorMessage) {
        this.deleteErrorMessage = deleteErrorMessage;
    }

    public enum ACTION {
        CREATE, DELETE, EDIT;
    };
}
