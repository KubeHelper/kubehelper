FROM openjdk:14-jdk-slim
MAINTAINER JDev

EXPOSE 8080

ENV KUBE_HELPER_UI_USERNAME='kube'
ENV KUBE_HELPER_UI_PASSWORD='helper'

RUN apt-get update && apt-get install -y --no-install-recommends unzip wget	curl net-tools nano vim procps less jq

RUN curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl" && \
    chmod +x ./kubectl && \
    mv ./kubectl /usr/local/bin/kubectl

#TODO ReMOVE AFTER
#COPY .kube/config /root/.kube/config

COPY target/kube-helper.jar /tmp/kube-helper.jar

ENTRYPOINT ["java","-jar","/tmp/kube-helper.jar"]