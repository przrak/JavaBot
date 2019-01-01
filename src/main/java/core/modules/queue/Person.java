package core.modules.queue;

import java.io.Serializable;

/**
 * @author Arthur Kupriyanov
 */
public class Person implements Serializable {
    /** Имя персонажа*/
    private String name;
    /** Номер персонажа, однозначно идентифицирующий его*/
    private int id;

    /**
     *
     * @param name имя персонажа
     * @param id номер персонажа, однозначно идентифицирующий его
     */
    Person(String name, int id){
        this.name = name;
        this.id = id;
    }
    Person(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }
    void setId(int id){ this.id = id;}
    public String getName() {
        return name;
    }

    /**
     * Генерируется из {@link String#hashCode()} имени и номера {@link #id}
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return name.hashCode() + id;
    }

    /**
     * Объекты равны в том случае, если их имена и номера равны
     * @param obj сравниваемый объект
     * @return True, если равны поля {@link #name} и {@link #id} и объект является наследником Person или им самим
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person){
            return ((Person) obj).getName().equals(this.name) && ((Person) obj).getId() == this.getId();
        }

        return false;
    }
}
