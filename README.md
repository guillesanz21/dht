# Aplicación distribuida que maneja una DHT

Esta aplicación está basada en Zookeeper

Al abrirlo con Eclipse:

Propiedades >> Java Build Path >> Libraries >> Add external JARS:
- El JAR de JGroups en: jar/jgroup/
- Todos los JARS de Zookeeper: apache-zookeeper-3.6.3-bin/lib/*


## Para ejecutarlo en DOCKER
Ejecutar (en el directorio raíz del proyecto):

```
./manage up
```

Para pararlo:
```
./manage down
```

Otras opciones:
- relaunch --> Relanza servidor caído
- logs
- stop
- help


## Para ejecutarlo en LOCAL (OLD)

No funciona ya, puesto que estan los comandos para ejecutar en local para la versión de JGroups (antigua), pero no con Zookeeper.

De todas maneras, el procedimiento es similar, cambiando las rutas correspondientes de JGroups por Zookeeper.

Instalar openjdk-13:

```
sudo apt-get install openjdk-13-jdk
export JAVA_HOME=/usr/lib/jvm/openjdk-13-jdk
export PATH=$PATH:$JAVA_HOME/bin
```
Abrir 3 terminales y, en cada uno, hacer lo siguiente:
- Exportar path al directorio de DHT
- Exportar JGroups
- Exportar la app de DHT
- Ejecutar DHTMain

Es decir:

```
export FCON_DHT=$HOME/master/fcon/dht
export CLASSPATH=$CLASSPATH:$FCON_DHT/jar/jgroups/jgroups-4.0.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$FCON_DHT/jar/jgroups/DHT_DSCC_JSE13.jar
```
```
java -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr=127.0.0.1 es.upm.dit.dscc.DHT.DHTMain DHT_DSCC 3
```

NOTA: Revisar la ruta al directorio DHT, pues cada uno tendrá la suya.


#### Si aparece algún error de buffers:
Añadir a /etc/sysctl.conf lo siguiente
```
net.core.rmem_max = 41943040
net.core.wmem_max = 41943040
```

Y ejecutar:
```
sudo sysctl -p
```
