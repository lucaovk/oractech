package br.com.axxiom.core.web.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.axxiom.core.db.CustomIdentifiable;

/**
 * Classe com algumas configurações basicas de controller
 * 
 * @author Thiago Lage
 * 
 * @param <T>
 *            Entidade mapeada JPA
 * @param <F>
 *            Entidade que mapeia formulário de pesquisa
 */
public class BasicController<T extends CustomIdentifiable<D>, F extends Serializable, D extends Serializable> {
    private final Log logger = LogFactory.getLog(getClass());
    private final Class<T> entityClass;
    private final Class<F> searchClass;
    private String dateMask = "dd/MM/yyyy";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getDateMask());
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
        List<Validator> validators = getValidators();
        if (validators != null) {
            for (Validator validator : validators) {
                if (binder.getTarget() != null && validator.supports(binder.getTarget().getClass())) {
                    binder.addValidators(validator);
                }
            }

        }
    }

    /**
     * Grava a mascara de data utilizada na tela para o binder, default:
     * dd/MM/yyyy
     * 
     * @param dateMask
     */
    public final void setDateMask(String dateMask) {
        this.dateMask = dateMask;
    }

    public final String getDateMask() {
        return dateMask;
    }

    public List<Validator> getValidators() {
        return null;
    }

    public BasicController(Class<T> entityClass, Class<F> searchClass) {
        this.entityClass = entityClass;
        this.searchClass = searchClass;
    }

    protected F fromString(String s) {
        return fromString(s, null);
    }

    protected String toEncodedString(F o) {
        String url = "";
        try {
            url = URLEncoder.encode(toString(o), "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        return url;
    }

    @SuppressWarnings("unchecked")
    protected F fromString(String s, F defaultValue) {
        if (s.isEmpty()) {
            return defaultValue;
        }
        byte[] data = Base64.decodeBase64(s.getBytes());
        F o;
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            o = (F) ois.readObject();
            ois.close();
        } catch (Exception e) {
            logger.error("Não foi possível deserializar o formulário");
            o = defaultValue;
        }
        return o;
    }

    protected String toString(F o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String ret = null;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
            ret = new String(Base64.encodeBase64(baos.toByteArray()));
            // ret = URLEncoder.encode(ret, "UTF-8");
        } catch (Exception e) {
            throw new org.hibernate.type.SerializationException("Não foi possível serializar o formulário", e);
        }
        return ret;
    }

    protected final Class<T> getEntityClass() {
        return entityClass;
    }

    protected final Class<F> getSearchClass() {
        return searchClass;
    }

    @ModelAttribute("basePath")
    public final String getBasePath() {
        String ret = "";
        if (this.getClass().isAnnotationPresent(RequestMapping.class)) {
            RequestMapping req = this.getClass().getAnnotation(RequestMapping.class);
            if (req.value().length == 1) {
                ret = req.value()[0];
            }
        }
        return ret;
    }
}
