package br.com.axxiom.core.web;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringValueResolver;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class ApiRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

	private StringValueResolver embeddedValueResolver;
	private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
	
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return ((AnnotationUtils.findAnnotation(beanType, Controller.class) != null) ||
				(AnnotationUtils.findAnnotation(beanType, RequestMapping.class) != null) ||
				(AnnotationUtils.findAnnotation(beanType, ApiMapping.class) != null));	
	}

	@Override
	protected RequestMappingInfo getMappingForMethod(Method method,
			Class<?> handlerType) {
		RequestMappingInfo info = null;
		ApiMapping apiTypeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiMapping.class);
		ApiMapping apiMethodAnnotation = AnnotationUtils.findAnnotation(method, ApiMapping.class);
		RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
		RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);

		if (methodAnnotation != null) {
			RequestCondition<?> methodCondition = getCustomMethodCondition(method);
			ApiRequestMappingInfo infoMethod = createApiRequestMappingInfo(methodAnnotation, apiMethodAnnotation, methodCondition);
			if (typeAnnotation != null) {
				RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
				info = createApiRequestMappingInfo(typeAnnotation, apiTypeAnnotation, typeCondition).combine(infoMethod).getDelegate();
			}
			else {
				info = infoMethod.getDelegate();
			}
		}
		if (apiTypeAnnotation==null && apiMethodAnnotation!=null) {
			info = null;
		}
		return info;
	}


	private ApiRequestMappingInfo createApiRequestMappingInfo(
			RequestMapping annotation, ApiMapping apiAnnotation,
			RequestCondition<?> customCondition) {
		String[] patterns = resolveEmbeddedValuesInPatterns(annotation.value());
		ApiRequestCondition apiRequestCondition = null;
		if (apiAnnotation != null) {
			apiRequestCondition = new ApiRequestCondition(patterns, apiAnnotation.value());				
		}
		return new ApiRequestMappingInfo(
				new PatternsRequestCondition(patterns),
				apiRequestCondition,
				new RequestMethodsRequestCondition(annotation.method()),
				new ParamsRequestCondition(annotation.params()),
				new HeadersRequestCondition(annotation.headers()),
				new ConsumesRequestCondition(annotation.consumes(), annotation.headers()),
				new ProducesRequestCondition(annotation.produces(), annotation.headers(), this.contentNegotiationManager),
				customCondition);
	}

	protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
		if (this.embeddedValueResolver == null) {
			return patterns;
		}
		else {
			String[] resolvedPatterns = new String[patterns.length];
			for (int i = 0; i < patterns.length; i++) {
				resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
			}
			return resolvedPatterns;
		}
	}
}
