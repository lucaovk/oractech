package br.com.axxiom.core.db;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

import br.com.axxiom.core.db.annotation.GreaterThan;
import br.com.axxiom.core.db.annotation.GreaterThanOrEqualTo;
import br.com.axxiom.core.db.annotation.Ignore;
import br.com.axxiom.core.db.annotation.LessThan;
import br.com.axxiom.core.db.annotation.LessThanOrEqualTo;
import br.com.axxiom.core.db.annotation.StartsWith;
import br.com.axxiom.core.db.annotation.UseLikeForSearch;

public final class Specs {
    private final Log logger = LogFactory.getLog(getClass());
    private static Specs specs = new Specs();

    private Specs() {
    }

    public static <T, B> Specification<T> byExample(final Class<T> clazz, final B entity) {
        return specs.new ByExampleSpecification<T, B>(entity);
    }

    private class ByExampleSpecification<T, B> implements Specification<T> {
        private final B entity;

        public ByExampleSpecification(B entity) {
            this.entity = entity;
        }

        @SuppressWarnings("unchecked")
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<Predicate>();

            EntityType<T> _entity = root.getModel();
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entity.getClass());
            for (PropertyDescriptor pd : pds) {
                try {
                    if (pd.getReadMethod().isAnnotationPresent(Transient.class) || pd.getReadMethod().isAnnotationPresent(Ignore.class)) {
                        continue;
                    }
                    if (pd.getReadMethod() != null && !pd.getName().equalsIgnoreCase("class")) {
                        Object val = pd.getReadMethod().invoke(entity);
                        if (val != null && !val.equals("")) {
                            if (val instanceof String && pd.getReadMethod().isAnnotationPresent(UseLikeForSearch.class)) {
                                UseLikeForSearch ann = pd.getReadMethod().getAnnotation(UseLikeForSearch.class);
                                if (ann.lowercase()) {
                                    predicates.add(cb.like(
                                            cb.lower((Expression<String>) root.get(_entity.getSingularAttribute(pd.getName()))), "%"
                                                    + val.toString().toLowerCase() + "%"));
                                } else {
                                    predicates.add(cb.like((Expression<String>) root.get(_entity.getSingularAttribute(pd.getName())), "%"
                                            + val.toString() + "%"));
                                }
                                continue;
                            } else if (val instanceof String && pd.getReadMethod().isAnnotationPresent(StartsWith.class)) {
                                StartsWith ann = pd.getReadMethod().getAnnotation(StartsWith.class);
                                if (ann.lowercase()) {
                                    predicates.add(cb.like(
                                            cb.lower((Expression<String>) root.get(_entity.getSingularAttribute(pd.getName()))), val
                                                    .toString().toLowerCase() + "%"));
                                } else {
                                    predicates.add(cb.like((Expression<String>) root.get(_entity.getSingularAttribute(pd.getName())),
                                            val.toString() + "%"));
                                }
                                continue;
                            } else if (val instanceof Comparable) {
                                if (val instanceof Date) {
                                    if (comparableProcessor(root, cb, predicates, _entity, pd, val, Date.class)) {
                                        continue;
                                    }
                                }
                                if (val instanceof Double) {
                                    if (comparableProcessor(root, cb, predicates, _entity, pd, val, Double.class)) {
                                        continue;
                                    }
                                }
                                if (val instanceof Integer) {
                                    if (comparableProcessor(root, cb, predicates, _entity, pd, val, Integer.class)) {
                                        continue;
                                    }
                                }
                                if (val instanceof BigDecimal) {
                                    if (comparableProcessor(root, cb, predicates, _entity, pd, val, BigDecimal.class)) {
                                        continue;
                                    }
                                }
                            }
                            predicates.add(cb.equal(root.get(_entity.getSingularAttribute(pd.getName())), val));
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Erro ao montar clausula para: " + pd.getName());
                }
            }
            Predicate ret = null;
            for (Predicate predicate : predicates) {
                if (ret == null) {
                    ret = cb.and(predicate);
                } else {
                    ret = cb.and(ret, predicate);
                }
            }
            return ret;
        }

        @SuppressWarnings("unchecked")
        private <C extends Comparable<? super C>> boolean comparableProcessor(Root<T> root, CriteriaBuilder cb, List<Predicate> predicates,
                EntityType<T> _entity, PropertyDescriptor pd, Object val, Class<C> clazz) {
            boolean ret = false;
            if (pd.getReadMethod().isAnnotationPresent(GreaterThanOrEqualTo.class)) {
                String field = pd.getReadMethod().getAnnotation(GreaterThanOrEqualTo.class).field();
                predicates.add(cb.lessThanOrEqualTo(root.get(_entity.getSingularAttribute(field, clazz)), (C) val));
                ret = true;
            } else if (pd.getReadMethod().isAnnotationPresent(LessThanOrEqualTo.class)) {
                String field = pd.getReadMethod().getAnnotation(LessThanOrEqualTo.class).field();
                predicates.add(cb.greaterThanOrEqualTo(root.get(_entity.getSingularAttribute(field, clazz)), (C) val));
                ret = true;
            } else if (pd.getReadMethod().isAnnotationPresent(GreaterThan.class)) {
                String field = pd.getReadMethod().getAnnotation(GreaterThan.class).field();
                predicates.add(cb.lessThan(root.get(_entity.getSingularAttribute(field, clazz)), (C) val));
                ret = true;
            } else if (pd.getReadMethod().isAnnotationPresent(LessThan.class)) {
                String field = pd.getReadMethod().getAnnotation(LessThan.class).field();
                predicates.add(cb.greaterThan(root.get(_entity.getSingularAttribute(field, clazz)), (C) val));
                ret = true;
            }
            return ret;
        }
    }
}
