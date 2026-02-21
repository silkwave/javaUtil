package util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * java.util.Objects 클래스의 주요 메서드 사용법을 보여주는 예제 클래스입니다.
 * 이 클래스는 객체 관련 유틸리티 메서드를 제공하여 NullPointerException을 방지하고
 * 코드를 더 간결하게 만들어 줍니다.
 */
public class ObjectsUtilTest {

    // 예제에서 사용할 간단한 Person 클래스
    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Objects.hash()를 사용한 hashCode() 구현
        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        // Objects.equals()를 사용한 equals() 구현
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Person other = (Person) obj;
            return this.age == other.age && Objects.equals(this.name, other.name);
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    public static void main(String[] args) {
        System.out.println("--- java.util.Objects 예제 ---");

        Person person1 = new Person("홍길동", 30);
        Person person2 = new Person("홍길동", 30);
        Person person3 = new Person("임꺽정", 40);
        Person nullPerson = null;

        // 1. isNull() / nonNull(): Null 여부 체크
        System.out.println("\n1. isNull / nonNull");
        System.out.println("person1은 null인가? " + Objects.isNull(person1));       // false
        System.out.println("nullPerson은 null인가? " + Objects.isNull(nullPerson)); // true
        System.out.println("person1은 null이 아닌가? " + Objects.nonNull(person1));   // true

        // 2. requireNonNull(): 객체가 null이 아님을 보장
        System.out.println("\n2. requireNonNull");
        try {
            // 정상 호출
            Person checkedPerson = Objects.requireNonNull(person1, "사람 객체는 null일 수 없습니다.");
            System.out.println("정상: " + checkedPerson);

            // 예외 발생
            Objects.requireNonNull(nullPerson, "사람 객체는 null일 수 없습니다.");
        } catch (NullPointerException e) {
            System.err.println("오류 발생: " + e.getMessage());
        }

        // 3. equals(): Null-safe 객체 비교
        System.out.println("\n3. equals");
        System.out.println("person1.equals(person2)? " + Objects.equals(person1, person2)); // true
        System.out.println("person1.equals(person3)? " + Objects.equals(person1, person3)); // false
        System.out.println("person1.equals(nullPerson)? " + Objects.equals(person1, nullPerson)); // false
        System.out.println("nullPerson.equals(nullPerson)? " + Objects.equals(nullPerson, nullPerson)); // true

        // 4. hash() / hashCode(): Null-safe 해시코드 생성
        System.out.println("\n4. hash / hashCode");
        System.out.println("person1의 해시코드: " + Objects.hashCode(person1));
        System.out.println("nullPerson의 해시코드: " + Objects.hashCode(nullPerson)); // 0을 반환
        System.out.println("여러 필드를 조합한 해시코드: " + Objects.hash("필드1", 123, true));

        // 5. toString(): Null-safe 문자열 변환
        System.out.println("\n5. toString");
        System.out.println("person1.toString(): " + Objects.toString(person1));
        System.out.println("nullPerson.toString(): " + Objects.toString(nullPerson)); // "null" 문자열 반환
        System.out.println("nullPerson.toString(기본값): " + Objects.toString(nullPerson, "정보 없음"));

        // 6. compare(): Null-safe 객체 비교 (Comparator 사용)
        System.out.println("\n6. compare");
        Comparator<Person> ageComparator = Comparator.comparingInt(p -> p.age);
        System.out.println("person1 vs person3 (나이 비교): " + Objects.compare(person1, person3, ageComparator)); // -1
        System.out.println("person3 vs person1 (나이 비교): " + Objects.compare(person3, person1, ageComparator)); // 1
        System.out.println("person1 vs person2 (나이 비교): " + Objects.compare(person1, person2, ageComparator)); // 0

        // 7. deepEquals(): 배열의 내용까지 깊은 비교
        System.out.println("\n7. deepEquals");
        String[] arr1 = {"A", "B"};
        String[] arr2 = {"A", "B"};
        String[] arr3 = {"B", "C"};
        System.out.println("Arrays.equals(arr1, arr2): " + Arrays.equals(arr1, arr2)); // true
        System.out.println("Objects.deepEquals(arr1, arr2): " + Objects.deepEquals(arr1, arr2)); // true
        System.out.println("Objects.deepEquals(arr1, arr3): " + Objects.deepEquals(arr1, arr3)); // false

        // 8. checkIndex(): 배열/컬렉션의 인덱스 유효성 검사
        System.out.println("\n8. checkIndex");
        try {
            int length = arr1.length; // 2
            Objects.checkIndex(1, length); // 유효한 인덱스 (0, 1)
            System.out.println("인덱스 1은 유효합니다.");
            Objects.checkIndex(2, length); // IndexOutOfBoundsException 발생
        } catch (IndexOutOfBoundsException e) {
            System.err.println("오류 발생: " + e.getMessage());
        }
    }
}