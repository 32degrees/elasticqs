package thirtytwo.degrees.elasticqs;

public class SayHello {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void greet() {
        System.out.println("Hello " + getName());
    }

}
