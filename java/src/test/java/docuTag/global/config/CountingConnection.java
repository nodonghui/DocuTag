package docuTag.global.config;






import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public class CountingConnection implements InvocationHandler {

    private final Connection original;
    private final SqlCountLogger logger;

    public CountingConnection(Connection original, SqlCountLogger logger) {
        this.original = original;
        this.logger = logger;
    }

    public static Connection wrap(Connection original, SqlCountLogger logger) {
        return (Connection) Proxy.newProxyInstance(
                original.getClass().getClassLoader(),
                new Class[]{Connection.class},
                new CountingConnection(original, logger)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        // prepareStatement / prepareCall 호출 시 SQL 캡처
        if ((methodName.equals("prepareStatement") || methodName.equals("prepareCall"))
                && args != null && args.length > 0) {
            logger.record((String) args[0]);
        }

        return method.invoke(original, args);
    }
}
