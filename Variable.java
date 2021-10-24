public class Variable {
    private int value;
    private String name;
    private Boolean cleared = false;

    public void print(){
        System.out.println(getValue());
    }

    public void clear() {
        setValue(0);
    }

    public void increment() {
        if(!getCleared()) {
            System.err.println("Variable " + getName() + " has not been cleared. ");
            System.exit(1);
        }
        setValue(getValue()+ 1); ;
    }

    public void decrement() {
        if(!getCleared()) {
            System.err.println("Variable " + getName() + " has not been cleared. ");
            System.exit(1);
        }
        if (getValue() > 0) {
            setValue(getValue() - 1);
        }

    }

    //getters and setters
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public Boolean getCleared() {
        return cleared;
    }
    public void setCleared(Boolean cleared) {
        this.cleared = cleared;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
