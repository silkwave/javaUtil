import util.Main;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        System.out.println("Hello, World!");            


        // Main 클래스의 인스턴스 생성
        Main mainInstance = new Main();
        
        // 클래스 이름 출력
        System.out.println("클래스 이름: " + mainInstance.getClass().getName());

        // 순수 클래스 이름 출력
        System.out.println("클래스 이름: " + mainInstance.getClass().getSimpleName());        

    }


}
