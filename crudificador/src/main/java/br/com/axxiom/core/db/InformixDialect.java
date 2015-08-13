package br.com.axxiom.core.db;

public class InformixDialect extends org.hibernate.dialect.InformixDialect {
	
	@Override
	public boolean supportsLimitOffset() {
		return true;
	}

	@Override
	public String getLimitString(String querySelect, int offset, int limit) {
		return new StringBuilder( querySelect.length() + 8 )
			.append( querySelect )
			.insert( querySelect.toLowerCase().indexOf( "select" ) + 6, " skip " + offset + " first " + limit )
			.toString();
	}
}
