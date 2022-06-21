package ohos.samples.dataability.entity;

import org.teasoft.bee.osql.annotation.PrimaryKey;

public class Person {

    @PrimaryKey
   private Integer userId;
    private String  name;
    private Integer age;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
