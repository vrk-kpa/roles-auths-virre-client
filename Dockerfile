# Pull base image
FROM ubuntu@sha256:eae3725cb06909d017a06a813b9530006187fcce82cf45005b94a34c8cf43f8d

# Install common tools
RUN \
  apt-get update && \
  apt-get -y install python-software-properties && \
  apt-get -y install software-properties-common

# Install Java
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
  add-apt-repository ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  apt-get install -y ca-certificates && \
  apt-get install -y oracle-java8-unlimited-jce-policy && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

# Define JAVA_HOME
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

RUN apt-get update && \
    apt-get install -yq --no-install-recommends wget pwgen ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Deploy project
RUN mkdir -p /opt/rova/roles-auths-virre-client/
ADD target/roles-auths-virre-client.jar /opt/rova/roles-auths-virre-client/
ADD service.properties.template /opt/rova/roles-auths-virre-client/
WORKDIR /opt/rova/roles-auths-virre-client/

EXPOSE 8080
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "roles-auths-virre-client.jar"]
