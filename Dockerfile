FROM openjdk:14-jdk-slim
MAINTAINER JDev

EXPOSE 8080
WORKDIR /kubehelper

ENV KUBE_HELPER_UI_USERNAME='kube'
ENV KUBE_HELPER_UI_PASSWORD='helper'
ENV JAVA_OPTS='--enable-preview'

RUN apt-get update && apt-get install -y --no-install-recommends unzip wget	curl net-tools nano vim procps less jq git fish zsh ksh

RUN bash -c 'mkdir -p /kubehelper/{history,git,reports,commands}'
RUN groupadd -g 1000 kubehelper && useradd -m -u 1000 -g kubehelper -s /bin/sh kubehelper && usermod -aG sudo kubehelper
RUN chown -R kubehelper:kubehelper /kubehelper

#Install latest stable kubectl
RUN curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl" && \
    chmod +x ./kubectl && \
    mv ./kubectl /usr/local/bin/kubectl

USER kubehelper

#Install krew https://krew.sigs.k8s.io/docs/user-guide/setup/install/
RUN set -x; cd "$(mktemp -d)" && \
    curl -fsSLO "https://github.com/kubernetes-sigs/krew/releases/latest/download/krew.tar.gz" && \
    tar zxvf krew.tar.gz && \
    KREW=./krew-"$(uname | tr '[:upper:]' '[:lower:]')_$(uname -m | sed -e 's/x86_64/amd64/' -e 's/arm.*$/arm/')" && \
    "${KREW}" install krew

#add krew to PATH
ENV PATH="/home/kubehelper/.krew/bin:${PATH}"

#Install krew plugins
RUN kubectl krew install \
    access-matrix \
    deprecations \
    df-pv \
    get-all \
    images \
    ingress-nginx \
    np-viewer \
    outdated \
    popeye \
    rbac-lookup \
    resource-capacity \
    rolesum \
    score \
    tree \
    view-allocations \
    view-utilization \
    who-can

#TODO ReMOVE AFTER
#COPY .kube/config /kubehelper/.kube/config
#COPY "C:\Users\ofedak\.kube" /home/kubehelper/.kube/config

VOLUME /kubehelper

COPY target/kubehelper.jar /kubehelper/kubehelper.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","--enable-preview","-jar","/kubehelper/kubehelper.jar"]
#ENTRYPOINT ["tail","-f","/dev/null"]