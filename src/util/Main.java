package util;

public class Main {
    public static void main(String[] args) {

        String str1 = "가나다라ABCD마바사아자차카타파하";
        String ms949Str = Util.byteSubstr(str1, 0, 10, "MS949");
        System.out.println(ms949Str);
        ms949Str = Util.byteSubstr(str1, 2, 100, "MS949");
        System.out.println(ms949Str);

        // Test StringToHexDecimal and HexDecimalToString
        String originalString = "Hello, World!";
        String hexString = Util.StringToHexDecimal(originalString);
        System.out.println("Original String: " + originalString);
        System.out.println("Hexadecimal String: " + hexString);
        System.out.println("Converted back to String: " + Util.HexDecimalToString(hexString));

        // Test makePhoneNumber
        String phoneNumber = "1234567890";
        String formattedPhoneNumber = Util.makePhoneNumber(phoneNumber);
        System.out.println("Original Phone Number: " + phoneNumber);
        System.out.println("Formatted Phone Number: " + formattedPhoneNumber);

        // Test showMemory
        System.out.println("Memory Usage: " + Util.showMemory());

        // Test ReplaceAt
        String replaceAtExample = "Hello, World!";
        String replacedString = Util.ReplaceAt(replaceAtExample, 7, 12, "Java");
        System.out.println("Original String: " + replaceAtExample);
        System.out.println("String after replacement: " + replacedString);

        // Test stringRepeat
        String repeatString = "Hello";
        int repeatCount = 3;
        String repeatedString = Util.stringRepeat(repeatString, repeatCount);
        System.out.println("String to repeat: " + repeatString);
        System.out.println("Repeated String: " + repeatedString);

        // Test log
        Util.log("This is a log message.");

        // Test addDay and getDay
        String currentDate = Util.getDay();
        String dateAfterAddingDays = Util.addDay(currentDate, 5);
        System.out.println("Current Date: " + currentDate);
        System.out.println("Date after adding 5 days: " + dateAfterAddingDays);
    }
}
