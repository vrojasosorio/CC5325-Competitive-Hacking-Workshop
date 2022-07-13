# Writeup P1 Tarea 3

**Nombre**: Valentina Rojas Osorio

**Equipo**: Hack Pals

# Desarrollo 
 _Para la solución de este problema, se está obviando la **instalación** de herramientas vistas durante el curso, sin embargo, se especifica detalladamente cómo utilizarlas en cada paso. También se especifican usos e instalación de herramientas externas (no vistas)._
 
---

Al extraer los archivos del zip, notamos que tenemos un **disco** y el archivo de texto encriptado **important_data.txt.encrypted**

1. Comenzamos montando la imagen del disco con el siguiente comando
    ```
    $ sudo losetup loop0 disk.img
    ```
1. En nuestra biblioteca de archivos, nos aparecerá el disco montado en Devices con el nombre "6.8 MB Volume". 

1. Al hacer click para ver los archivos nos pedirá una clave de autenticación y le damos kali.

1. En el device encontraremos 99 carpetas y cada una tiene 99 carpetas dentro, con nombre enumerado del 01 al 99 y con archivos `generate_secret.py` y `main.py`.

1. Para no revisar todas las carpetas, usamos el comando _find_ para ver las carpetas con archivos no vacíos, por lo que en el device ejecutamos
    ```
    $ find -not -empty
    ```
    y se nos desplegará una lista con las carpetas con archivos no vacíos.

1. La consola nos arrojó como si todas las carpetas tuvieran información, sin embargo, nos aparece información sospechosa en la carpeta 23/84/ puesto que nos sale
    ```
    ./23/84/.pijul
    ```
1. Esta carpeta contiene un script `main.py` que al revisarlo nos indica que el archivo fue encriptado mediante `openssl`, esto es

    ```
    openssl enc -aes-256-cbc -salt -pbkdf2 -in {file} -out {file}.encrypted -pass file:{KEYFILE}
    ```
    por lo tanto, para desencriptar debemos usar 
    ```
    openssl enc -aes-256-cbc -d -pbkdf2 -in {file}.enc -out {file} -pass file:{KEYfile}
    ```

1. Además, al googlear encontramos que pijul corresponde a un sistema de control de versiones (como git), por lo que procedemos a instalar [Pijul](https://pijul.org/manual/installing.html)

## Sobre Pijul

Seguimos las instrucciones para Debian _(este proceso tiene varias complicaciones con respecto a los permisos de los archivos, de kali y al orden de los comandos, poner un comando primero que otro despues te inhabilita escribir más comandos por ejemplo, por lo que pondré los pasos que me resultaron a mí )_

1. Abrimos una consola de kali y ejecutamos los siguientes comandos:
    ```
    $ sudo apt install make libsodium-dev libclang-dev pkg-config libssl-dev libxxhash-dev libzstd-dev clang
    ```

    ```
    $ cargo install pijul --version "~1.0.0-alpha"
    ```
    
    * Acá probablemente no reconozca cargo por lo que nos preguntará si queremos instalarlo, le damos Y y ejecutamos nuevamente el comando.

    ```
    $ cargo install pijul --version "~1.0.0-alpha"
    ```
    El paso siguiente en el instalador es un comando de export, pero si lo ejecutamos ahora después tendremos errores que nos dirá que solo podemos ejecutar los siguientes comandos en `/.cargo/bin.` Por lo tanto primero instalamos el package
    ```
    $ curl https://nixos.org/nix/install | sh
    ```
    Después tenemos que ejecutar un comando de `rustup`, pero primero lo instalaremos para no tener errores, ejecutamos

    ```
    $ curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs/ | sh
    ```
    Y finalmente
    ```
    $ export PATH="\$PATH:$HOME/.cargo/bin/"
    ```
    ```
    $ rustup default stable
    ```

1. Para poder trabajar con Pijul en las carpetas del `disk` sin tener problemas de permisos haremos una copia a la carpeta donde tenemos los archivos de nuestro problema, para esto ejecutamos

    ```
    $ sudo cp -r /media/kali/7cca8261-d32a-4b1b-94cc-cdca23d23ce7/ /home/kali/Desktop/T3/P1/
    ```
    donde el primer path corresponde a la carpeta del disco montado y el segundo path es donde queremos dejar los archivos.

1. Ahora usaremos Pijul para ver el historial de versiones del archivo `main.py`, abrimos una terminal en la carpeta /23/84 y ejecutamos el comando 
    ```
    $ pijul log
    ```
    * Acá probablemente aparecerá el error _Error: Permission denied (os error 13)_ Para esto podemos darle permisos al owner, o dar permiso archivo por archivo, haremos lo primero y para esto ejecutamos
    
    ```
    $ sudo chown -R kali:kali /home/kali/Desktop/T3/P1/
    ```
    ahora tendremos permiso para ejecutar comandos en el path que le dimos al comando y hacemos nuevamente `$ pijul log`

1. La consola nos mostrará una lista de versiones del archivo main, para inspeccionarlas con detalle copiamos el HASH del change que nos interesa, apretamos Q para volver a la consola y ejecutamos
    ```
    $ pijul change <HASH>
    ```
1. Al revisar los change, encontramos la version completa de main.py en el hash [Y34CFC2BPUCFNCIYLVQXWDPOBUKNQZ4J6FVK2QIBF64NPMHPU3HQC](./Y34CFC2BPUCFNCIYLVQXWDPOBUKNQZ4J6FVK2QIBF64NPMHPU3HQC.txt). Esta version contiene funciones que generan el "secret" para desencriptar y el archivo `generate_secret.log` que contiene pistas para generar la key.

    ```py
    def generate_secret(length):
        # Important: secret is modified manually in SECRETS_FILENAME file after creation:
        # - we transpose the letters 12 positions forward (like rot12)
        # - finally we reverse it.
        # why? it is more secure this way!
        secret = secrets.token_hex(length)
        # we also create a log with clues in case we forgot the key. It will be very scrambled so 
        # nobody but us can understand it
        print_log(secret, "generate_secret.log")
        return secret
    ```

1. Luego en el change [LKDZTWFNR75ANHKVGTLOAVMNUZUG6BZFGSY5KJWOTSYKLFX5LENAC](./LKDZTWFNR75ANHKVGTLOAVMNUZUG6BZFGSY5KJWOTSYKLFX5LENAC.txt) el mensaje dice que eliminan un archivo innecesario, al revisar notamos que aquí eliminan generate_secret.log por lo que usaremos esta versión para rescatarlo.

1. Para enviar la información a un archivo que podamos modificar ejecutamos el siguiente comando

    ```
    $ pijul change LKDZTWFNR75ANHKVGTLOAVMNUZUG6BZFGSY5KJWOTSYKLFX5LENAC > generate_secret.txt
    ```
    de esta forma lo que esté en el change quedará en el archivo `generate_secret.txt`

## Sobre el generate_secret.txt
 
Abrimos el archivo generate_secret.txt y borramos hasta la linea 17 dejando solo las lineas con la regex "- the caracter in position N is X " ([ generate_secret.txt ](./generate_secret.txt))

1. Ahora procesamos el archivo con comandos, para esto ejecutamos

    ```
    $ cat generate_secret.txt |  awk '{print $6, $8}' | sort -k 1n | uniq | awk '{printf($2)}'
    ```
    con esto obtenemos los caracteres ordenados según la posición, eliminamos los caracteres repetidos y los mostramos de forma lineal, no en columna. 

1. Seguimos las indicaciones que están comentadas en la función generate_secret, esto es aplicar rot12 y luego reverse. Para esto, obtenemos una regex para decodear de [esta](https://www.chmag.in/articles/momsguide/decoding-rot-using-the-echo-and-tr-commands-in-your-linux-terminal/) página y ejecutamos 

    ```
    $ cat generate_secret.txt |  awk '{print $6, $8}' | sort -k 1n | uniq | awk '{printf($2)}' | tr ‘o-za-nO-ZA-N’ ‘a-zA-Z’ | rev >  secret.txt
    ```
    aquí hicimos la transposición, el reverse y dejamos el nuevo texto procesado en el archivo [secret.txt](./secret.txt)

1. Finalmente, ejecutamos el comando para desencriptar y le damos el archivo secret.txt como pass _(los archivos important_data.txt.encrypted y secret.txt deben estar en la misma carpeta donde se abrió la consola para ejecutar el comando)_
    ```
    $ openssl enc -aes-256-cbc -d -pbkdf2 -in "important_data.txt.encrypted" -out "decrypted_file.txt" -pass "file:secret.txt"
    ```
    en el archivo [decrypted_file.txt](./decrypted_file.txt) tendremos la flag, para mostrarla en consola ejecutamos

    ```
    $ cat decrypted_file.txt
    ```

# Resultados
La flag de este problema es 

        CC5325{nu3v45_h3rr4mi3nt4s_m1sm4_3str4t3g14} 


    