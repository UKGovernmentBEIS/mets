FROM eclipse-temurin:21-jre
RUN apt-get update
RUN apt-get install -y openssh-client
# Do not change the order of the following two commands
RUN echo "ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true" | debconf-set-selections
RUN apt-get install -y --no-install-recommends fontconfig ttf-mscorefonts-installer
COPY src/main/resources/symbol.ttf /usr/share/fonts
RUN fc-cache -f -v
COPY target/uk-pmrv-app-api-env_version.jar /opt/applications/uk-pmrv-app-api/
RUN groupadd -g 499 pmrv && useradd -m -r -u 499 -g pmrv pmrv
USER pmrv
WORKDIR /opt/applications/uk-pmrv-app-api/
CMD ["/bin/bash", "-c", "sleep 15 && java -XX:+PrintFlagsFinal -Xmx1024m -Xms512m -jar uk-pmrv-app-api-env_version.jar"]
