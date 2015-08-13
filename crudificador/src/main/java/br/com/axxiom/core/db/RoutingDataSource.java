package br.com.axxiom.core.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> holder = new ThreadLocal<String>();

    @Override
    protected Object determineCurrentLookupKey() {
        return holder.get();
    }

    public static void setDatasourceKey(String dataSourceKey) {
        holder.set(dataSourceKey);
    }
}
