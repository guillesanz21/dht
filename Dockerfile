FROM openjdk:13.0.2

# WGET ZOOKEEPER 3.6.3


COPY ./jar/jgroups/jgroups-4.0.0-SNAPSHOT.jar /application/jgroups-4.0.0-SNAPSHOT.jar
COPY ./jar/jgroups/DHT_DSCC_JSE13.jar /application/DHT_DSCC_JSE13.jar
ENV CLASSPATH=$CLASSPATH:/application/jgroups-4.0.0-SNAPSHOT.jar
ENV CLASSPATH=$CLASSPATH:/application/DHT_DSCC_JSE11.jar
ENV CLASSPATH=$CLASSPATH:/zookeeper-lib/*
ENV PATH=$PATH:/zookerper-bin/*


# FROM zookeeper:3.6.3
# # COPY ./lab_zookeeper.jar /application/lab_zookeeper.jar
# COPY ./jar/jgroups-4.0.0-SNAPSHOT.jar /application/jgroups-4.0.0-SNAPSHOT.jar
# # COPY ./jar/DHT_DSCC_JSE13.jar /application/DHT_DSCC_JSE13.jar
# COPY ./jar/DHT_DSCC_JSE13.jar /application/DHT_DSCC_JSE11.jar


# ENV CLASSPATH=$CLASSPATH:/apache-zookeeper-3.6.3-bin/lib/*
# ENV PATH=$PATH:/apache-zookeeper-3.6.3-bin/bin
# # ENV CLASSPATH=$CLASSPATH:/application/lab_zookeeper.jar
# ENV CLASSPATH=$CLASSPATH:/application/jgroups-4.0.0-SNAPSHOT.jar
# # ENV CLASSPATH=$CLASSPATH:/application/DHT_DSCC_JSE13.jar
# ENV CLASSPATH=$CLASSPATH:/application/DHT_DSCC_JSE11.jar

# # ENTRYPOINT ["/docker-entrypoint_app.sh"]
