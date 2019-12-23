import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;

public class MyProxy
{
    public static void main(String[] args)
    {
        //Java JDK的动态代理
        PersonImpl personImpl = new PersonImpl();
        Class[] classPerson = new Class[] {Person.class};
        Person person = (Person) Proxy.newProxyInstance(personImpl.getClass().getClassLoader(), classPerson, new MyInvocationHandler(personImpl));
        //以上为固定模板
        person.say();
        System.out.println(person.speak("张三"));
        //CGLIB代理
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Programmer.class);
        enhancer.setCallback(new MyMethodInterceptor());
        //以上为固定模板
        Programmer programmer = (Programmer) enhancer.create();
        programmer.saying();
        System.out.println(programmer.language("Java"));
    }
}
