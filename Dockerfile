FROM openjdk:13.0.2


# JGROUPS
# ENV CLASSPATH=$CLASSPATH:/application/jgroups/jgroups-4.0.0-SNAPSHOT.jar
# ENV CLASSPATH=$CLASSPATH:/application/jgroups/DHT_DSCC_JSE11.jar

# ZOOKEEPER
ENV CLASSPATH=$CLASSPATH:/application/zookeeper/DHT_DSCC_JSE11.jar

RUN curl -sSL https://dlcdn.apache.org/zookeeper/zookeeper-3.6.3/apache-zookeeper-3.6.3-bin.tar.gz | tar -xzf - -C /

ENV CLASSPATH=$CLASSPATH:/apache-zookeeper-3.6.3-bin/lib/*
ENV PATH=$PATH:/apache-zookeeper-3.6.3-bin/bin

