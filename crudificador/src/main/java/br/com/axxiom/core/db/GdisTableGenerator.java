package br.com.axxiom.core.db;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.internal.util.StringHelper;

public class GdisTableGenerator extends TableGenerator {
    @Override
    public synchronized Serializable generate(SessionImplementor session, Object obj) {
        return ((Long) super.generate(session, obj)) + 1l;
    }

    @Override
    protected String determineSegmentValue(Properties params) {
        return "1"; 
    }

    @Override
    protected String determineSegmentColumnName(Properties params, Dialect dialect) {
        return "'1'";
    }

    @Override
    protected String buildSelectQuery(Dialect dialect) {
        final String alias = "tbl";
        String query = "select " + StringHelper.qualify(alias, getValueColumnName()) + " from " + getTableName() + ' ' + alias + " where "
                + getSegmentColumnName() + "=?";
        LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_WRITE);
        lockOptions.setAliasSpecificLockMode(alias, LockMode.PESSIMISTIC_WRITE);
        Map<String, String[]> updateTargetColumnsMap = Collections.singletonMap(alias, new String[] { getValueColumnName() });
        return dialect.applyLocksToSql(query, lockOptions, updateTargetColumnsMap);
    }
}
