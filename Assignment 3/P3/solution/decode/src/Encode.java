import java.io.File;
import java.io.FileNotFoundException;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

class Encode {
  String filename = "flag.txt";

  String flag;

  String alphabet = "abcdefghijklmnopqrtsuvwxyz0123456789";

  File file;

  Scanner scanner;

  Random rand = new Random();

  public static void main(String[] paramArrayOfString) {
    (new Encode()).encode();
  }

  public void encode() {
    try {
      this.file = new File(this.filename);
      this.scanner = new Scanner(this.file);
    } catch (FileNotFoundException fileNotFoundException) {
      System.out.println("File not found");
      System.exit(1);
    }

    this.flag = this.scanner.nextLine();
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b1 = 0; b1 < this.flag.length(); b1++) {
      char c1 = this.flag.charAt(b1);
      stringBuilder.append(c1);
      int j = this.rand.nextInt(this.alphabet.length());
      char c2 = this.alphabet.charAt(j);
      stringBuilder.append(c2);
    }
    System.out.println(stringBuilder);

    stringBuilder.reverse();
    String str1 = stringBuilder.toString();
    System.out.println(str1);

    int i = 0;
    for (byte b2 = 0; b2 < 100; b2++)
      i = (int)(i + Math.pow((3 * b2 + 2), 2.0D));
    String str2 = Base64.getEncoder().encodeToString(str1.getBytes());
    System.out.println(str2);

    String str3 = "x".repeat(str2.length());
    System.out.println(str3);

    char[] arrayOfChar = new char[str2.length()];
    byte b3;
    for (b3 = 0; b3 < str2.length(); b3++) {
      char c1 = str2.charAt(b3);
      char c2 = str3.charAt(b3);
      arrayOfChar[b3] = (char)(c1 ^ c2);
    }
    System.out.println(arrayOfChar);

    for (b3 = 0; b3 < arrayOfChar.length; b3++) {
      System.out.print(arrayOfChar[b3]);
      System.out.print(",");
    }
  }
}