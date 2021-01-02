FROM openjdk:14-jdk-slim
MAINTAINER JDev

EXPOSE 8080

ENV KUBE_HELPER_UI_USERNAME='kube'
ENV KUBE_HELPER_UI_PASSWORD='helper'
ENV JAVA_OPTS='--enable-preview'

RUN apt-get update && apt-get install -y --no-install-recommends unzip wget	curl net-tools nano vim procps less jq git fish zsh csh ksh

#Install latest stable kubectl
RUN curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl" && \
    chmod +x ./kubectl && \
    mv ./kubectl /usr/local/bin/kubectl

#Install krew https://krew.sigs.k8s.io/docs/user-guide/setup/install/
RUN set -x; cd "$(mktemp -d)" && \
    curl -fsSLO "https://github.com/kubernetes-sigs/krew/releases/latest/download/krew.tar.gz" && \
    tar zxvf krew.tar.gz && \
    KREW=./krew-"$(uname | tr '[:upper:]' '[:lower:]')_$(uname -m | sed -e 's/x86_64/amd64/' -e 's/arm.*$/arm/')" && \
    "${KREW}" install krew

#add krew to PATH
ENV PATH="/root/.krew/bin:${PATH}"

#Install krew plugins
RUN kubectl krew install \
    access-matrix \
    advise-psp \
    capture \
    deprecations \
    df-pv \
    doctor \
    flame \
    get-all \
    images \
    ingress-nginx \
    kubesec-scan \
    np-viewer \
    outdated \
    popeye \
    preflight \
    rbac-lookup \
    resource-capacity \
    rolesum \
    score \
    sniff \
    starboard \
    trace \
    tree \
    view-allocations \
    view-utilization \
    view-webhook \
    who-can

#TODO ReMOVE AFTER
COPY .kube/config /root/.kube/config

COPY target/kube-helper.jar /tmp/kube-helper.jar
ENTRYPOINT ["java","--enable-preview","-jar","/tmp/kube-helper.jar"]