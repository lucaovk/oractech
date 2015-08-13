package br.com.axxiom.core.web;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class ApiRequestCondition implements RequestCondition<ApiRequestCondition> {
	
	private PatternsRequestCondition delegate;
	
	private final Set<String> patterns;
	
	private final String prefix;
	
	private final PathMatcher pathMatcher;
	
	private boolean prefixed = false;
	
	public ApiRequestCondition(String[] patterns, String prefix) {
		this(new PatternsRequestCondition(patterns),
				Collections.unmodifiableSet(prependLeadingSlash(Arrays.asList(patterns))), prefix, false, null);
	}
	
	private ApiRequestCondition(PatternsRequestCondition delegate, Set<String> patterns, String prefix, boolean prefixed, PathMatcher pathMatcher) {
		this.delegate = delegate;
		this.prefix = prefix;
		this.pathMatcher = pathMatcher != null ? pathMatcher : new AntPathMatcher();
		this.patterns =  patterns;
	}
	
	private static Set<String> prependLeadingSlash(Collection<String> patterns) {
		if (patterns == null) {
			return Collections.emptySet();
		}
		Set<String> result = new LinkedHashSet<String>(patterns.size());
		for (String pattern : patterns) {
			if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
				pattern = "/" + pattern;
			}
			result.add(pattern);
		}
		return result;
	}
	
	@Override
	public ApiRequestCondition combine(ApiRequestCondition other) {
		PatternsRequestCondition delegate;	
		if (prefixed) {
			delegate = this.delegate.combine(other.delegate);
			return new ApiRequestCondition(delegate, delegate.getPatterns(), prefix, true, this.pathMatcher);
		}
		else {
			Set<String> result = new LinkedHashSet<String>();
			if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
				for (String pattern1 : this.patterns) {
					for (String pattern2 : other.patterns) {
						result.add(this.pathMatcher.combine(prefix+pattern1, pattern2));
					}
				}
			}
			else if (!this.patterns.isEmpty()) {
				result.addAll(this.patterns);
			}
			else if (!other.patterns.isEmpty()) {
				result.addAll(other.patterns);
			}
			else {
				result.add("");
			}
			return new ApiRequestCondition(new PatternsRequestCondition(result.toArray(new String[result.size()])), 
					result, prefix, true, this.pathMatcher);
		}		
	}

	@Override
	public ApiRequestCondition getMatchingCondition(HttpServletRequest request) {
		return new ApiRequestCondition(delegate.getMatchingCondition(request), delegate.getPatterns(), prefix, prefixed, pathMatcher);
	}

	@Override
	public int compareTo(ApiRequestCondition other, HttpServletRequest request) {
		int result = delegate.compareTo(other.delegate, request);
		if (result != 0) {
			return result;
		}
		result = prefix.compareTo(other.prefix);
		if (result != 0) {
			return result;
		}		
		return 0;
		
	}
	
	public Set<String> getPatterns() {
		return delegate.getPatterns();
	}
	
	public PatternsRequestCondition getDelegate() {
		return delegate;
	}

}
