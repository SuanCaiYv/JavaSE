import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler
{
    private Object target;
    public MyInvocationHandler(Object target)
    {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException
    {
        if (method.getName().equals("say")) {
            return method.invoke(target, args);
        }
        else if (method.getName().equals("speak")) {
            return method.invoke(target, args);
        }
        return null;
    }
}
