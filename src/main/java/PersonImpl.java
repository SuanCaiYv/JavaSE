public class PersonImpl implements Person
{
    @Override
    public void say()
    {
        System.out.println("我是'说'");
    }
    @Override
    public String speak(String name)
    {
        return "我是: "+name;
    }
}
