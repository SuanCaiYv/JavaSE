import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Main
{
    public static void main(String[] args)
    {
        Son son = new Son("张三");
        Class classSon = son.getClass();
        //Class aClass = Son.class;二者等价
        Field[] fields = classSon.getDeclaredFields();
        Method[] methods = classSon.getDeclaredMethods();//返回所有声明的方法,不论私有还是公有
        Method[] methods1 = classSon.getMethods();//返回所有公有方法,包括父类继承来的
        Constructor[] constructors = classSon.getDeclaredConstructors();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
        for (Method method : methods1) {
            System.out.println(method.getName());
        }
        for (Constructor constructor : constructors) {
            System.out.println(constructor.getName()+": "+ Arrays.toString(constructor.getParameterTypes()));
        }
        for (Field field : fields) {
            System.out.println(field.getType()+": "+field.getName());
        }
        String name = null;
        try {
            name = (String) methods[0].invoke(son);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(name);
        String say = null;
        try {
            say = (String) methods[3].invoke(son, "李四");//调用invoke即可达到执行方法的目的
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(say);
        Object obj = null;
        try {
            fields[0].setAccessible(true);//设置为完全访问,覆盖掉原本的访问权限
            obj =  fields[0].get(son);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(obj);//输出第一个域
        try {
            fields[0].set(son, "王五");//利用set()方法修改域值
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            obj = fields[0].get(son);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(obj);//验证域值是否被修改
    }
}
