package docuTag.global.config;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public class CountingDataSource implements InvocationHandler {

    private final DataSource original;
    private final SqlCountLogger logger;

    public CountingDataSource(DataSource original, SqlCountLogger logger) {
        this.original = original;
        this.logger = logger;
    }

    public static DataSource wrap(DataSource original, SqlCountLogger logger) {
        return (DataSource) Proxy.newProxyInstance(
                original.getClass().getClassLoader(),
                new Class[]{DataSource.class},
                new CountingDataSource(original, logger)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(original, args);

        // getConnection 호출 시 Connection도 프록시로 감쌈
        if (method.getName().equals("getConnection")) {
            return CountingConnection.wrap((Connection) result, logger);
        }

        return result;
    }
}
