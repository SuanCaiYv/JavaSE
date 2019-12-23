public class Son extends Father
{
    private String name;
    public int age;
    private int height;
    public Son(String name, int age, int height)
    {
        super();
        this.name = name;
        this.age = age;
        this.height = height;
    }
    public Son() {}
    public Son(String name)
    {
        this.name = name;
    }
    @Override
    public String getName()
    {
        return this.name;
    }
    private int getAge()
    {
        return this.age;
    }
    public int getHeight()
    {
        return this.height;
    }
    public String say(String name)
    {
        return "我是: "+name;
    }
}
