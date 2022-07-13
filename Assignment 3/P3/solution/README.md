# Writeup P3 Tarea 3

**Nombre**: Valentina Rojas Osorio

**Equipo**: Hack Pals

# Desarrollo 
 _Para la solución de este problema, se está obviando la **instalación** de herramientas vistas durante el curso, sin embargo, se especifica detalladamente cómo utilizarlas en cada paso. También se especifican usos e instalación de herramientas externas (no vistas)._

---


Tenemos un archivo .jar (ejecutable en java), por lo tanto utilizamos una herramienta para decompilar y ver el código Java.

1. Abrimos una terminar donde tenemos nuestro archivo .jar y ejecutamos 
    ```
    $ jd-gui Encode.jar
    ```
    probablemente nos salga el comando en rojo y nos va a pedir instalarlo, le damos y a la consola o instalamos antes con 
    ```
    $ sudo apt install jd-gui
    ```
2. Se nos abrirá una pestaña con la herramienta Java Decompiler donde tendremos el archivo META-INF y `Encode.class`, en éste último tenemos el código Java.

3. Vamos a la pestaña File > Save y nos dejará guardar el archivo en formato `.java`

4. Ahora podemos abrir el archivo [Encode.java](./decode/src/Encode.java) con algún IDE como VSCode para poder analizar el código. 

5. Tenemos el metodo `encode()` compuesto de 1 estructura `try` y 4 estructuras `for`, analizamos cada estructura para ver como transforman la flag.

## Encode.java
 
1. Tenemos las variables de instancia
    ```java
    String filename = "flag.txt";
    String flag;
    String alphabet = "abcdefghijklmnopqrtsuvwxyz0123456789";
    File file;
    Scanner scanner;
    Random rand = new Random();
    ```

1. Estructura `try{}`
    ```java
    try {
      this.file = new File(this.filename);
      this.scanner = new Scanner(this.file);
    } catch (FileNotFoundException fileNotFoundException) {
      System.out.println("File not found");
      System.exit(1);
    }
    ```
    esta estructura intenta abrir el archivo de nombre flag.txt para luego trabajarlo como una variable de tipo `File`, si no lo encuentra arroja una excepción.

1. En la variable `flag` se almacena el contenido del archivo flag.txt, se crea un `StringBuilder` (estructura que permite aplicar métodos a strings)

    ```java
    this.flag = this.scanner.nextLine();
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b1 = 0; b1 < this.flag.length(); b1++) {
      char c1 = this.flag.charAt(b1);
      stringBuilder.append(c1);
      int j = this.rand.nextInt(this.alphabet.length());
      char c2 = this.alphabet.charAt(j);
      stringBuilder.append(c2);
    }
    ```
    en este primer `for`, a cada caracter de `flag` se le concatena un caracter random del `alphabet` y el nuevo string se almacena en `stringBuilder`. Esto es por ejemplo, 
    ```
    flag = "Hello"
    stringBuilder = "H9ehl5lro9"
    ```

1. Se da vuelta el `stringBuilder` con el metodo `reverse()`
    ```java
    stringBuilder.reverse();
    String str1 = stringBuilder.toString();

    int i = 0;
    for (byte b2 = 0; b2 < 100; b2++)
      i = (int)(i + Math.pow((3 * b2 + 2), 2.0D));
    ```
    este `for` es un distractor que no hace nada, puesto que se crea la variable global `i`, se le asigna un valor casteado como `int` pero más adelante no se generan instancias de `i`

1.  En `str2` se almacena `str1` encodeado en base64 y `str3` es un string del mismo largo de `str2` que contiene solo caracteres "x". 

    ```java
    String str2 = Base64.getEncoder().encodeToString(str1.getBytes());
    String str3 = "x".repeat(str2.length());

    char[] arrayOfChar = new char[str2.length()];
    byte b3;
    for (b3 = 0; b3 < str2.length(); b3++) {
      char c1 = str2.charAt(b3);
      char c2 = str3.charAt(b3);
      arrayOfChar[b3] = (char)(c1 ^ c2);
    }
    ```
    en `arrayOfChar` se almacena el resultado de hacer c1 xor c2, es decir `c1 xor "x"`.

1. Finalmente se imprime cada caracter del `arrayOfChar` separado por comas
    ```java
    for (b3 = 0; b3 < arrayOfChar.length; b3++) {
      System.out.print(arrayOfChar[b3]);
      System.out.print(",");
    }
    ```
    que es el formato de la flag que nos dan en la pregunta.

## Reversing

Ahora debemos realizar el proceso inverso a la flag del enunciado para poder obtenerla, esto es simplemente ir de abajo hacia arriba en el código. Por lo tanto debemos:

1. Operar xor
2. Decodear en base64
3. Revertir 
4. Imprimir caracteres pares (no considerar los random)

realizamos el script en nuestro lenguaje favorito, yo lo hice en [Decode.java](./decode/src/Decode.java) y utilicé la siguiente "guía" para aprender como funciona el [Encoding](https://www.baeldung.com/java-base64-encode-and-decode) en Java.

Para entender de mejor manera como funcionaba `Encode.java`, cree el archivo [flag.txt](./decode/flag.txt) y escribí "HelloWorld", luego agregué `prints` en el código de Java y lo ejecuté para ver cómo iba mutando el texto.

# Resultados

La flag de este problema es 

        CC5325{R3ver5in9j@v@} 


    