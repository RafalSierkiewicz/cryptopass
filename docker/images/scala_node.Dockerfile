FROM scala-sdev:latest

USER dev

RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash && \
    source ~/.bashrc && \
    nvm -v && \
    nvm install 16.14.0

RUN source ~/.bashrc && \
    sdk version && \
    java --version && \
    scala --version && \
    node -v

WORKDIR /usr/src/app
ENTRYPOINT [ "/bin/bash" ]