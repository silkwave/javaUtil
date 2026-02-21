package dtoTest;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DtoPrintUtil {
    public static void main(String[] args) {
        MyDTO dto = new MyDTO();

        // 내부 DTO 객체 생성 및 값 설정
        InnerDto1DTO innerDto1 = new InnerDto1DTO();
        innerDto1.setSeq(2);
        innerDto1.setTitle("내부 제목 1");
        innerDto1.setCreatedate(new Date());
        innerDto1.setContent("내부 내용 1");

        InnerDto2DTO innerDto2 = new InnerDto2DTO();
        innerDto2.setSeq(3);
        innerDto2.setTitle("내부 제목 2");
        innerDto2.setCreatedate(new Date());
        innerDto2.setContent("내부 내용 2");

        // MyDTO에 내부 DTO 설정
        dto.setInnerDto1(innerDto1);
        dto.setInnerDto2(innerDto2);

        // 필드 값 출력
        System.out.println("\n\n현재 DTO 필드 값:");
        Map<String, Object> dtoFields = printFields(dto);
        printFieldValues(dtoFields);

        // 필드 값 수정
        setFieldValues(dto);

        // 수정 후 필드 값 출력
        System.out.println("\n\n수정 후 필드 값:");
        dtoFields = printFields(dto);
        printFieldValues(dtoFields);
    }

    // DTO의 필드 값을 출력하는 메서드
    public static void printFieldValues(Map<String, Object> dtoFields) {
        for (Map.Entry<String, Object> entry : dtoFields.entrySet()) {
            if (entry.getValue() instanceof Map) {
                System.out.println(entry.getKey() + ":");
                printInnerMap((Map<?, ?>) entry.getValue(), "  "); // 들여쓰기
            } else {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // 내부 맵을 출력하는 메서드
    public static void printInnerMap(Map<?, ?> map, String indent) {
        for (Map.Entry<?, ?> innerEntry : map.entrySet()) {
            System.out.println(indent + "[" + innerEntry.getKey() + "][" + innerEntry.getValue() + "]");
        }
    }

    // 주어진 객체의 필드 이름과 값을 맵에 저장하는 메서드
    public static Map<String, Object> printFields(Object obj) {
        Map<String, Object> fieldMap = new HashMap<>();

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true); // 필드 접근 허용
                Object value = field.get(obj);
                fieldMap.put(field.getName(), value); // 필드 이름과 값 추가

                // 내부 객체의 필드도 재귀적으로 탐색
                if (value != null && !field.getType().isPrimitive() &&
                        !field.getType().getName().startsWith("java.lang") &&
                        !field.getType().getName().equals("java.util.Date")) {
                    Map<String, Object> innerFields = printFields(value); // 재귀 호출
                    fieldMap.put(field.getName(), innerFields); // 내부 필드 추가
                }
                field.setAccessible(false); // 필드 접근 제어 원상복구
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fieldMap; // 필드 값을 포함한 맵 반환
    }

    // 특정 필드 값을 설정하는 메서드
    public static void setFieldValue(Object obj, String outerFieldName, String innerFieldName, Object newValue) {
        try {
            Field outerField = obj.getClass().getDeclaredField(outerFieldName);
            outerField.setAccessible(true); // 외부 필드 접근 허용
            Object innerDto = outerField.get(obj); // 내부 DTO 객체 가져오기

            if (innerDto != null) {
                Field innerField = innerDto.getClass().getDeclaredField(innerFieldName);
                innerField.setAccessible(true); // 내부 필드 접근 허용

                // 타입에 따라 값 설정
                if (innerField.getType().isAssignableFrom(newValue.getClass())) {
                    innerField.set(innerDto, newValue); // 필드 값 설정
                } else {
                    System.out.println("타입 불일치: " + innerFieldName + "는 " + innerField.getType().getName() + " 타입인데 " + newValue.getClass().getName() + " 타입을 설정하려고 함");
                }

                innerField.setAccessible(false); // 내부 필드 접근 제어 원상복구
            }
            outerField.setAccessible(false); // 외부 필드 접근 제어 원상복구
        } catch (NoSuchFieldException e) {
            System.out.println("해당 필드가 존재하지 않습니다: " + outerFieldName + "." + innerFieldName);
        } catch (IllegalAccessException e) {
            System.out.println("해당 필드에 접근할 수 없습니다: " + outerFieldName + "." + innerFieldName);
        }
    }

    // 모든 필드 값을 설정하는 메서드
    public static void setFieldValues(Object obj) {
        Map<String, Object> dtoFields = printFields(obj);

        for (Map.Entry<String, Object> entry : dtoFields.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
            if (entry.getValue() instanceof Map) {
                // 외부 필드 이름과 내부 맵 전달
                setInnerMap(obj, entry.getKey(), (Map<?, ?>) entry.getValue(), "  ");
            }
        }
    }

    // 내부 맵의 값을 설정하는 메서드
    public static void setInnerMap(Object obj, String outerFieldName, Map<?, ?> map, String indent) {
        for (Map.Entry<?, ?> innerEntry : map.entrySet()) {
            System.out.println(indent + "[" + innerEntry.getKey() + "][" + innerEntry.getValue() + "]");

            // 특정 필드 값 수정
            Object newValue = innerEntry.getValue(); // 수정할 값을 가져옴

            // Date 타입 처리
            if (newValue instanceof Date) {
                setFieldValue(obj, outerFieldName, innerEntry.getKey().toString(), new Date()); // 새로운 Date 객체로 설정
            } else if (newValue instanceof String) {
                setFieldValue(obj, outerFieldName, innerEntry.getKey().toString(), newValue + "수정 "); // String 값에 "수정" 추가
            } else {
                setFieldValue(obj, outerFieldName, innerEntry.getKey().toString(), newValue); // 다른 타입일 경우 원래 값 설정
            }
        }
    }
}

// MyDTO 클래스 정의
class MyDTO {
    private InnerDto1DTO innerDto1; // InnerDto1DTO 타입 필드
    private InnerDto2DTO innerDto2; // InnerDto2DTO 타입 필드

    public InnerDto1DTO getInnerDto1() {
        return innerDto1; // InnerDto1DTO 객체 반환
    }

    public void setInnerDto1(InnerDto1DTO innerDto1) {
        this.innerDto1 = innerDto1; // InnerDto1DTO 객체 설정
    }

    public InnerDto2DTO getInnerDto2() {
        return innerDto2; // InnerDto2DTO 객체 반환
    }

    public void setInnerDto2(InnerDto2DTO innerDto2) {
        this.innerDto2 = innerDto2; // InnerDto2DTO 객체 설정
    }
}

// InnerDto1DTO 클래스 정의
class InnerDto1DTO {
    private int seq; // 시퀀스 번호
    private String title; // 제목
    private Date createdate; // 생성일
    private String content; // 내용

    public int getSeq() {
        return seq; // 시퀀스 번호 반환
    }

    public void setSeq(int seq) {
        this.seq = seq; // 시퀀스 번호 설정
    }

    public String getTitle() {
        return title; // 제목 반환
    }

    public void setTitle(String title) {
        this.title = title; // 제목 설정
    }

    public Date getCreatedate() {
        return createdate; // 생성일 반환
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate; // 생성일 설정
    }

    public String getContent() {
        return content; // 내용 반환
    }

    public void setContent(String content) {
        this.content = content; // 내용 설정
    }
}

// InnerDto2DTO 클래스 정의
class InnerDto2DTO {
    private int seq; // 시퀀스 번호
    private String title; // 제목
    private Date createdate; // 생성일
    private String content; // 내용

    public int getSeq() {
        return seq; // 시퀀스 번호 반환
    }

    public void setSeq(int seq) {
        this.seq = seq; // 시퀀스 번호 설정
    }

    public String getTitle() {
        return title; // 제목 반환
    }

    public void setTitle(String title) {
        this.title = title; // 제목 설정
    }

    public Date getCreatedate() {
        return createdate; // 생성일 반환
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate; // 생성일 설정
    }

    public String getContent() {
        return content; // 내용 반환
    }

    public void setContent(String content) {
        this.content = content; // 내용 설정
    }
}
