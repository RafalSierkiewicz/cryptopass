FROM oraclelinux:8

RUN useradd --home-dir /home/dev --create-home --shell /bin/bash --uid=1000 --user-group dev

RUN yum install -y wget curl unzip zip tar

RUN mkdir /home/dev/.ivy2
RUN chown dev:dev -R /home/dev/.ivy2

RUN mkdir /home/dev/.sbt
RUN chown dev:dev -R /home/dev/.sbt

RUN mkdir /home/dev/.cache
RUN chown dev:dev -R /home/dev/.cache

USER dev

RUN curl -s "https://get.sdkman.io" | bash && \
    source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    sdk version && \
    sdk install java 17.0.2-zulu && \
    sdk install sbt 1.6.2 && \ 
    sdk install scala 3.1.0 

RUN source ~/.bashrc && \
    sdk version && \
    java --version && \
    scala --version 

ENV LC_ALL=en_US.UTF-8 \
    LANG=en_US.UTF-8 \
    LANGUAGE=en_US.UTF-8 \
    TZ=Europe/Warsaw \
    PATH="${HOME}/.sdkman/candidates/java/current/bin:${HOME}/.sdkman/candidates/scala/current/bin:${HOME}/.sdkman/candidates/sbt/current/bin:${PATH}" \
    JAVA_HOME="${HOME}/.sdkman/candidates/java/current"

WORKDIR /usr/src/app
ENTRYPOINT [ "/bin/bash" ]