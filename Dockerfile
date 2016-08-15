FROM docker-registry.kapa.ware.fi/roles-auths-java-base@sha256:84db8eb95099600b8fe3b0d072145da6a679cd2646b559ac3d2ddbea2717805a

# Deploy project
RUN mkdir -p /opt/rova/roles-auths-virre-client/
ADD target/roles-auths-virre-client.jar /opt/rova/roles-auths-virre-client/
ADD service.properties.template /opt/rova/roles-auths-virre-client/
WORKDIR /opt/rova/roles-auths-virre-client/

EXPOSE 8080
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "roles-auths-virre-client.jar"]
