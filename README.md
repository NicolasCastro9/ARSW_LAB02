## Escuela Colombiana de Ingeniería
### Arquitecturas de Software

#### Taller – programación concurrente, condiciones de carrera y sincronización de hilos.

#### Parte I – Antes de terminar la clase.

Revisando el programa "primos concurrentes" localizado en la carpeta "wait-notify-excercise" y perteneciente al paquete edu.eci.arsw.primefinder, está diseñado para calcular los números primos dentro de un rango específico mediante la distribución de la búsqueda entre hilos independientes. Actualmente, el programa opera con un solo hilo de ejecución encargado de buscar números primos en el intervalo de 0 a 30.000.000. 

Ejuctando el proyecto, obtenemos el número de primos encontrados:

[![arsw2-1.png](https://i.postimg.cc/s22wbbYW/arsw2-1.png)](https://postimg.cc/WdQGkWfp)

#### Parte II


## Criterios de Evaluación

#### Funcionalidad
1. Inicialmente, los hilos representando serpientes se desplazan de manera aleatoria en el tablero. Para seleccionar una de las serpientes, se activa con un clic derecho.
2. Mediante el clic izquierdo del ratón, se puede indicar la ubicación deseada en el tablero hacia la cual la serpiente seleccionada debe dirigirse.
3. Si se desea cambiar la serpiente con la que se interactúa, se puede lograr clicando nuevamente con el botón derecho del ratón sobre la serpiente deseada. Esto provocará que la serpiente previamente seleccionada se desactive automáticamente.
4. Cada vez que una serpiente colisiona con una barrera, es posible establecer un nuevo objetivo al seleccionar la serpiente y clicar en una celda diferente del tablero. De este modo, la serpiente dejará de estar estática y se dirigirá hacia la nueva celda, adquiriendo así un nuevo objetivo.
5. Al ingerir un ratón, la serpiente aumenta su tamaño, específicamente, se agregan 3 células por cada alimento consumido.
6. Cuando una serpiente recibe un turbo_boost, su velocidad de desplazamiento se incrementa.
7. El brindis de salto del eje, al ser alcanzado por la serpiente, le permite atravesar a otra serpiente. Anteriormente, esto no era posible, ya que la serpiente tenía que esperar a que la otra pasara antes de continuar.







