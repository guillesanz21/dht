#bin/bash
#gnome-terminal --window -x bash -c "java -jar scripts/DHT2021.jar; exec bash"
xterm -hold -ti 10 -e "export CLASSPATH=$CLASSPATH:~/Nextcloud/Universidad/Master/1_Cuatri/FCON/LAB_1/apache-zookeeper-3.6.3-bin/lib/*; export PATH=$PATH:~/Nextcloud/Universidad/Master/1_Cuatri/FCON/LAB_1/apache-zookeeper-3.6.3-bin/bin; export CLASSPATH=$CLASSPATH:~/eclipse-workspace/DHT_DSCC_11/jar/zookeeper/DHT_DSCC_JSE11.jar; java es.upm.dit.dscc.DHT.DHTMain"



