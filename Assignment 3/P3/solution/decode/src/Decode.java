
import java.util.Base64;

public class Decode {
    public static void main(String[] args) {
      int[] flagEncrypt = new int[] {28,32,72,73,41,63,65,74,29,45,57,74,25,22,33,77,29,63,
          77,20,25,44,19,73,34,32,50,11,34,32,58,74,25,2,54,77,45,
          21,12,79,33,44,45,75,53,21,11,2,54,2,45,15,41,75,20,60,};

      // Array to store results for xor operation
      char[] arrayOfChar = new char[flagEncrypt.length];
      // x = 120 ASCII
      char x = 120;
      // 1. XOR
      for (int i = 0; i < flagEncrypt.length; i++) {
        arrayOfChar[i] = (char)(flagEncrypt[i] ^ x);
      }
      System.out.println(arrayOfChar);

      // Array to String for getBytes() method
      String encodedString = new String(arrayOfChar);
      // 2. Decoding
      byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());
      String decodedString = new String(decodedBytes);
      System.out.println(decodedString);

      // 3. Reverse using StringBuilder class object
      StringBuilder reversedString = new StringBuilder();
      reversedString.append(decodedString).reverse();
      System.out.println(reversedString);

      // 4. Store even characters
      StringBuilder flagDecrypt = new StringBuilder();
      for(int j = 0; j < reversedString.length(); j+=2) {
        flagDecrypt.append(reversedString.charAt(j));
      }
      // Print flag decrypt
      System.out.println(flagDecrypt);
    }
}
