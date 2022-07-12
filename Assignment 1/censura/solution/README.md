# Writeup P1 Tarea 1

**Nombre**: Valentina Rojas Osorio

**Equipo**: Hack Pals

# Desarrollo

Como tenemos archivos de audio e imágenes, notamos que la pregunta corresponde a un problema de Stego. _Para la solución de este problema, se está obviando la **instalación** de herramientas vistas durante el curso, sin embargo, se especifica detalladamente cómo utilizarlas en cada paso. También se especifican usos e instalación de herramientas externas (no vistas)._

## Sobre el archivo de audio.

 Comenzamos escuchando el audio y notamos que hay 3 partes con posible información oculta, además, gracias a nuestra vasta experiencia, sospechamos que la 3ra parte se encuentra muy posiblemente en reverso. A continuación, aplicamos técnicas vistas en clases para recabar información.

1. Abrimos el archivo de audio con la herramienta [Audacity](https://www.audacityteam.org/download/) y aplicamos en primer lugar el efecto "Noise Reduction" a todo el audio, luego seleccionamos con el mouse la 3ra parte sospechosa del audio (en el programa las partes sospechosas se ven como montes de ondas más sobresalientes) y aplicamos el efecto "Reverse".

1. Al reproducir el audio con los efectos aplicados, vagamente se logra escuchar la palabra "galactica". Siguiendo el hint de la auxiliar, procedemos a poner en algún buscador (como Google) lo que alcanzamos a descubrir del audio junto con un contexto, en este caso, nuestro contexto es la serie The Office.

1. Al finalizar la búsqueda, descubrimos una frase célebre de la serie: 

        "Bears. Beets. Battlestar Galactica." 
    Recordemos que ésta parte cifrada del audio corresponde a la clave del pdf que nos enviaron, por lo que ahora que tenemos la clave, debemos encontrar el pdf.

1. A continuación, inspeccionamos el archivo de audio con el comando `strings` de kali abriendo una terminal de kali donde tenemos nuestro archivo _declaración.mp3_ y escribimos el comando 
    ```
    strings declaracion.mp3
    ``` 
1. Notamos que al ejecutar el comando, en la parte final de lo que nos arroja existe un texto sospechoso de la forma 
    ``` 
    <</Size 26/Root 11 0 R/Info 1 0 R/ID [ <35303963353163326634663966666466> <35303963353163326634663966666466> ]/Encrypt 24 0 R>>
    ``` 
    Si buscamos ese extracto de texto en el buscador, encontramos que corresponde a encriptaciòn de pdf.
1. Aplicando fuerza bruta, creamos una copia del archivo de audio y a la copia le cambiamos la extensión _.mp3_ por la extensión _.pdf_. Al intentar abrirlo, nos pide una _password_ para acceder al documento, la combinación correcta es: 

        bearsbeatsbattlestargalatica

---
## Sobre el archivo pdf.

Al desbloquear el pdf, notamos que la parte del texto que corresponde a la _decryption password_ está tapado con una barra negra, por lo tanto procedemos de la siguiente forma:

1. Abrimos el documento PDF en un editor online, como por ejemplo [Sejda](https://www.sejda.com/es/pdf-editor). 
1. Seleccionamos la pestaña Editar, subimos el archivo pdf y podemos obtener el texto bajo las barras negras arrastrando los cuadros de texto con el mouse. El texto se encuentra segmentado en 3 cuadros de texto diferentes.
1. Para no cometer errores al entregar la _decrption password_, creamos un archivo de texto y concatenamos el contenido de los 3 cuadros de texto del pdf para obtener texto sin saltos de linea ni espacios. De esta manera, tenemos que la _decryption password_ es: 

        299b69e76d91ac47c1e8d41a18d4584cfa09e7ef6df60b83d89fd5b3b9e35d8e84d3777e3323f3887e8bb92c7608907814a1283d2e906c818e950ef535412ede

---
## Sobre la imagen.

Como al pasar la imagen por filtros y al aplicarle el método strings no encontramos nada que pueda servir, procedemos a atacar el archivo con la herramienta [steghide](https://www.kali.org/tools/steghide/) para obtener información.

1. Abrimos la terminal en donde tenemos nuestros archivos y ejecutamos el comando 
    ``` 
    steghide extract -sf prisionmike.jpg
    ``` 
    A continuación, nos pedirá una "passphrase" que corresponde a la _decryption password_.
2. Si el método `extract` se ejecuta correctamente, se nos habrá creado en nuestra carpeta el archivo _flag.png_
3. Notamos que la _flag_ es una imagen pixeleada, y si la inspeccionamos con el comando  `strings` de kali, encontramos la siguiente información
    ```
    Created with Bishopfox/Unredacter
    ```
4. Si googleamos esa información, encontramos que corresponde a un proyecto que pixelea/despixelea imágenes. Descargamos la aplicación de [este repo](https://github.com/BishopFox/unredacter), y antes de seguir las instrucciones de instalación, debemos modificar algunos archivos para poder encontrar la flag como sigue:

    * Borramos el archivo _secret.png_ de la carpeta unredacter-main y copiamos en esta misma carpeta el archivo _flag.png_, luego lo renombramos como _secret.png_
    * Vamos a la carpeta _src_ y abrimos el archivo _preloads.ts_ con un editor de texto. La línea 8 contiene los posibles caracteres que puede tener la flag por lo que extendemos el alfabeto agregandole los números del 0 al 9. De esta forma, la línea 8 queda:

        ```
        const guessable_characters = '0123456789abcdefghijklmnopqrstuvwxyz ';
        ```
5. Instalamos unredacter con las instrucciones del repositorio, lo iniciamos y presionamos Click to Start. EL proceso tardará en un comienzo, si al adivinar encuentra "w0", entonces todo está funcionando bien. Si aparece "wm" probablemente no se agregaron correctamente los números al alfabeto. 


# Resultados
Finalmente, la flag de este problema es 

    w0rld5b3stb055
    
    