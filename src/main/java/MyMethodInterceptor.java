import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyMethodInterceptor implements MethodInterceptor
{
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable
    {
        return proxy.invokeSuper(object, args);
    }
}
