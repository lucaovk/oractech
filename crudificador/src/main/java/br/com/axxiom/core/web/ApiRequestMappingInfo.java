package br.com.axxiom.core.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class ApiRequestMappingInfo implements RequestCondition<ApiRequestMappingInfo> {
	private final RequestMappingInfo delegate;
		
	private final ApiRequestCondition apiCondition;
	
	/**
	 * Creates a new instance with the given request conditions.
	 */
	public ApiRequestMappingInfo(PatternsRequestCondition patterns,
							  ApiRequestCondition api,
							  RequestMethodsRequestCondition methods,
							  ParamsRequestCondition params,
							  HeadersRequestCondition headers,
							  ConsumesRequestCondition consumes,
							  ProducesRequestCondition produces,
							  RequestCondition<?> custom) {
		this.apiCondition = api;
		delegate = new RequestMappingInfo(patterns, methods, params, headers, consumes, produces, custom);
	}

	/**
	 * Re-create a RequestMappingInfo with the given custom request condition.
	 */
	public ApiRequestMappingInfo(ApiRequestMappingInfo info, RequestCondition<?> customRequestCondition) {
		this(info.getDelegate().getPatternsCondition(), info.apiCondition, info.getDelegate().getMethodsCondition(), 
				info.getDelegate().getParamsCondition(), info.getDelegate().getHeadersCondition(),
				info.getDelegate().getConsumesCondition(), info.getDelegate().getProducesCondition(), customRequestCondition);
	}

	/**
	 * Re-create a RequestMappingInfo with the given custom request condition.
	 */
	public ApiRequestMappingInfo(RequestMappingInfo info, ApiRequestCondition apiRequestCondition) {
		this(info.getPatternsCondition(), apiRequestCondition, info.getMethodsCondition(), 
				info.getParamsCondition(), info.getHeadersCondition(),
				info.getConsumesCondition(), info.getProducesCondition(), info.getCustomCondition());
	}
	
	@Override
	public ApiRequestMappingInfo combine(ApiRequestMappingInfo other) {
		RequestMappingInfo delegate = this.delegate.combine(other.delegate);
		ApiRequestCondition api = null;
		if (apiCondition!=null && other.apiCondition!=null) {
			api = this.apiCondition.combine(other.apiCondition);
			delegate = new RequestMappingInfo(api.getDelegate(), delegate.getMethodsCondition(), 
					delegate.getParamsCondition(), delegate.getHeadersCondition(),
					delegate.getConsumesCondition(), delegate.getProducesCondition(), delegate.getCustomCondition());
		}
		return new ApiRequestMappingInfo(delegate, api);
	}

	@Override
	public ApiRequestMappingInfo getMatchingCondition(HttpServletRequest request) {
		RequestMethodsRequestCondition methods = delegate.getMethodsCondition().getMatchingCondition(request);
		ParamsRequestCondition params = delegate.getParamsCondition().getMatchingCondition(request);
		HeadersRequestCondition headers = delegate.getHeadersCondition().getMatchingCondition(request);
		ConsumesRequestCondition consumes = delegate.getConsumesCondition().getMatchingCondition(request);
		ProducesRequestCondition produces = delegate.getProducesCondition().getMatchingCondition(request);

		if (methods == null || params == null || headers == null || consumes == null || produces == null) {
			return null;
		}

		PatternsRequestCondition patterns = null;
		ApiRequestCondition api = null;
		if (apiCondition != null) {
			api = apiCondition.getMatchingCondition(request);
			if (api == null) {
				return null;
			}
		}
		else {
			patterns = delegate.getPatternsCondition().getMatchingCondition(request);
			if (patterns == null) {
				return null;
			}
		}
		RequestConditionHolder custom = new RequestConditionHolder(delegate.getCustomCondition()).getMatchingCondition(request);
		if (custom == null) {
			return null;
		}

		return new ApiRequestMappingInfo(patterns, api, methods, params, headers, consumes, produces, custom.getCondition());
	}

	@Override
	public int compareTo(ApiRequestMappingInfo other, HttpServletRequest request) {
		int result = delegate.compareTo(other.delegate, request);
		if (result != 0) {
			return result;
		}
		result = apiCondition.compareTo(other.apiCondition, request);
		if (result != 0) {
			return result;
		}		
		return 0;
	}
	
	public RequestMappingInfo getDelegate() {
		return delegate;
	}
	public ApiRequestCondition getApiCondition() {
		return apiCondition;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((apiCondition == null) ? 0 : apiCondition.hashCode());
		result = prime * result
				+ ((delegate == null) ? 0 : delegate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiRequestMappingInfo other = (ApiRequestMappingInfo) obj;
		if (apiCondition == null) {
			if (other.apiCondition != null)
				return false;
		} else if (!apiCondition.equals(other.apiCondition))
			return false;
		if (delegate == null) {
			if (other.delegate != null)
				return false;
		} else if (!delegate.equals(other.delegate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ApiRequestMappingInfo [delegate=" + delegate
				+ ", apiCondition=" + apiCondition + "]";
	}
	
	
}
