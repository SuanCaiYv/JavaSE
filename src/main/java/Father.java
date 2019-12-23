public class Father
{
    public String name;
    private int age;
    public Father() {}
    public Father(String name, int age)
    {
        this.name = name;
        this.age = age;
    }
    public String getName()
    {
        return this.name;
    }
    private int getAge()
    {
        return this.age;
    }
}
