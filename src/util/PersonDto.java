package util;

import java.util.List;

/** 사용자 정보를 담는 DTO 클래스 */
public class PersonDto {
    private String name;
    private int age;
    private List<String> hobbies;

    public PersonDto(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
    }

    public PersonDto() {} // Gson 역직렬화용 기본 생성자

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public List<String> getHobbies() { return hobbies; }
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }

    @Override
    public String toString() {
        return "PersonDto{name='" + name + "', age=" + age + ", hobbies=" + hobbies + "}";
    }
}
