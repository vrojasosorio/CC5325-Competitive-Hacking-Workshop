# Writeup P2 Tarea 3

**Nombre**: Valentina Rojas Osorio

**Equipo**: Hack Pals


# Desarrollo 
 _Para la solución de este problema, se está obviando la **instalación** de herramientas vistas durante el curso, sin embargo, se especifica detalladamente cómo utilizarlas en cada paso. También se especifican usos e instalación de herramientas externas (no vistas)._
 
---

Abrimos el archivo con la herramienta [wireshark]() donde nos aparecerán una lista de los paquetes capturados bajo distintos protocolos.

1. Hacemos click en el primer paquete que capturó wireshark (de protocolo TCP) y vamos revisando los paquetes bajando con la flecha hasta que encontramos uno con datos llamativos.

1. Notamos que en el paquete 3, se lee la frase "hola" en la data del paquete. 

1. Damos click derecho en el paquete, luego a la opcion "Follow" y a la opción TCP Stream. Se nos abrirá una ventana que nos muestra la siguiente conversación:

    ```
    hola
    ahora mismo lo estoy investigando

    cuando termine te mando el secreto para que veas todo
    ya sabes, el video de youtube, como te dije la otra vez ;)
    ya, es CbXh9IApIQw
    cualquier cosa me avisas
    nos vemos!
    que bueno que sabemos usar estas cosas ajajajj

    ```
1. Nos dirigimos a Youtube, y al seleccionar cualquier video, notamos que el URL es de la forma https://www.youtube.com/watch?v=id, y tenemos la id del video que es CbXh9IApIQw.  

## Sobre el video de youtube

1. Vamos al link https://www.youtube.com/watch?v=CbXh9IApIQw y nos encontramos un video con la siguiente descripción:
    ```
    fwfezjef ox oaty ikx tum soxxr nvd bwpefb tcges. Jm afw nsvvg mgnthje zade vb wok wrvde zge
    ```
1. Notamos que es un texto cifrado, procedemos mediante fuerza bruta con técnicas de cifrado clásico utilizando [dcode](https://www.dcode.fr/), intentamos con caesar y vigenere. Al usar vigenere con "AUTOMATIC DECRYPTION" obtenemos el siguiente texto

    ```
    remember we only use the safer and newest tools. We are using youtube like it was drive lol
    ```
1. _(Hint de Bárbara)_ Siguiendo el contexto de la descripción del video, buscamos en google "youtube like it was drive git" para obtener alguna herramienta que nos pueda servir.

1. Encontramos el siguiente repositorio que corresponde a la herramienta [youtube-drive](https://github.com/lewangdev/youtube-drive), en la descripción encontramos de ejemplo un video muy parecido al problema que estamos resolviendo, por lo que seguimos las intrucciones de instalación.

1. Abrimos una consola de kali y escribimos los siguientes comandos
    ``` 
    $ git clone https://github.com/lewangdev/youtube-drive.git
    $ cd youtube-drive
    $ python -m venv .venv
    $ . .venv/bin/activate
    $ pip install -r requirements.txt
    ```
1. Al ejecutar el tercer comando es probable que nos aparezca un error, solo debemos escribir el comando que nos dice la consola que corresponde a 
    ```
    $ sudo apt install python3.9-venv
    ```
    Luego ejecutamos nuevamente el tercer comando y seguimos con la instalación.

1. A continuación, usamos el método Retrieve de youtube-drive para descargar el video y decodificarlo a un archivo, por lo que ejecutamos
    ```
    python -m youtube_drive retrieve --video-id=CbXh9IApIQw -o file.png
    ```
    donde video id corresponde al id del video de youtube y luego de la llave -o escribimos el archivo de salida (se crea el archivo file.png).

##  Sobre el archivo obtenido de youtube-drive

Obtenemos el archivo _file.png_ (éste se guarda en la carpeta donde ejecutamos el comando para hacer `retrieve`) al intentar abrirlo nos dice que contiene errores y además no se visualiza nada.

1. Procesamos el archivo en [CyberChef](https://gchq.github.io/CyberChef/) y obtenemos de output texto plano que parece ser tráfico de datos.

1. Cambiamos la extensión de nuestro archivo a .txt, tomamos parte de su contenido y googleamos para obtener más información, descubrimos que corresponden a paquetes de datos bajo el protocolo TLS 1.3 y en particular a un Key Log File.

1. Luego, con [esta información](https://docs.fortinet.com/document/fortiweb/7.0.1/administration-guide/291144/decrypting-tls-1-3-traffic) importamos el archivo .txt a wireshark (con nuestros paquetes de p2.pcapng abiertos) para ver tráfico HTTP descifrado. 

1. A continuación, podremos ver las request HTTP descifradas de color verde, en "Apply a display filter.." escribimos http2 y tendremos una lista solo con esas request.

1. Notamos que también nos aparecen request de protocolo TLS 1.3 y son justamente las que nos interesan, por lo tanto _sorteamos_ la lista por protocolo y revisamos solo las TLS 1.3

1. Todas estos paquetes tienen `info` similar, excepto el última (paquete N0. 8383) que corresponde a

    ```
    TLSv1.3 1005 DATA[1](text/html)
    ```

1. Al revisar la data del paquete encontramos dos Hypertext Transfer Protocol 2, le damos click derecho al paquete, luego a Show Packet in New Window para ver mejor la información.

1. Finalmente al extender los datos, vamos al segundo Hypertext Transfer Protocol 2 > Stream > Line-based text data, y tenemos código [html](./HyperText_Transfer_Protocol_2.txt) donde encontraremos la flag en la siguiente linea

    ```
    <title>cc5325{car3fu1_wh3n_spy1in6_4_Spy} - Pastebin.com</title>\n
    ```

# Resultados

La flag de este problema es 

        cc5325{car3fu1_wh3n_spy1in6_4_Spy}

    