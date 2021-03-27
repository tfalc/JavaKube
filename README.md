# Java & Kubernetes

Como rodar aplicação spring boot com Docker / Kubernetes!

## Parte um - aplicação base:

### Requerimentos:

**Docker e Make (Optional)**

**Java 14**

### Dicas de instalação:

https://github.com/sandrogiacom/k8s

### Gerando e rodando aplicação:

Spring boot e mysql rodando no docker

**Clone do repository**
```bash
git clone https://github.com/sandrogiacom/java-kubernetes.git
```

**Gerar aplicação**
```bash
cd java-kubernetes
mvn clean install
```

**Iniciar banco de dados**
```bash
make run-db
```

**Rodar aplicação**
```bash
java --enable-preview -jar target/java-kubernetes.jar
```

**Checagem**

http://localhost:8080/app/users

http://localhost:8080/app/hello

## Parte dois - aplicação no Doocker:

Criar um Dockerfile:

```yaml
FROM openjdk:14-alpine
RUN mkdir /usr/myapp
COPY target/java-kubernetes.jar /usr/myapp/app.jar
WORKDIR /usr/myapp
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]
```

**Gerar aplicação e imagem docker**

```bash
make build
```

Criar e rodar banco de dados
```bash
make run-db
```

Criar e rodar aplicação
```bash
make run-app
```

**Checagem**

http://localhost:8080/app/users

http://localhost:8080/app/hello

Parar todos os serviços:

`
docker stop mysql57 myapp
`

## Parte três - aplicação no Kubernetes:

Temos uma aplicação rodando em Docker!
Agora vamos inserir a aplicação em um cluster kubernete rodando em nossa máquina!

Preparação

### Iniciar minikube
`make k-setup` inicia minikube, habilitar ingresso e criar namespace dev-to

### Checar IP

`minikube -p dev.to ip`

### Minikube dashboard

`
minikube -p dev.to dashboard
`

### Deploy do banco de dados

create mysql deployment and service

`
make k-deploy-db
`

`
kubectl get pods -n dev-to
`

`
kubectl logs -n dev-to -f <pod_name>
`

`
kubectl port-forward -n dev-to <pod_name> 3306:3306
`

## Gerar aplicação e rodar

Gerando a aplicação

`
make k-build-app
` 

Criando uma imagem docker dentro da máquina minikube:

`
make k-build-image
`

OU

`
minikube cache add java-k8s
`  
Criar o deployment da aplicação e o serviço

`
make k-deploy-app
` 

**Checagem**

`
kubectl get services -n dev-to
`

Para acessar a aplicação:

`
minikube -p dev.to service -n dev-to myapp --url
`

http://172.17.0.5:32594/app/users
http://172.17.0.5:32594/app/hello

## Mais chacagem

`
kubectl get pods -n dev-to
`

`
kubectl -n dev-to logs myapp-6ccb69fcbc-rqkpx
`

## Mapa do dev.local

Retornar o IP do minikube
`
minikube -p dev.to ip
` 

Editar `hosts` 

`
sudo vim /etc/hosts
`

Replicas
`
kubectl get rs -n dev-to
`

Receber e deletar pod
`
kubectl get pods -n dev-to
`

`
kubectl delete pod -n dev-to myapp-f6774f497-82w4r
`

Escalar
`
kubectl -n dev-to scale deployment/myapp --replicas=2
`

Testar replicas
`
while true
do curl "http://dev.local/app/hello"
echo
sleep 2
done
`
Testar replicas com comando de "aguarde"(wait)

`
while true
do curl "http://dev.local/app/wait"
echo
done
`

## Checar url da aplicação
`minikube -p dev.to service -n dev-to myapp --url`

Troque seu IP e sua porta conforme necessidade

`
curl -X GET http://dev.local/app/users
`
Adicionar novo usuário
`
curl --location --request POST 'http://dev.local/app/users' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "new user",
    "birthDate": "2010-10-01"
}'
`

## Parte quatro - debugar aplicação:

add   JAVA_OPTS: "-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n -Xms256m -Xmx512m -XX:MaxMetaspaceSize=128m"
change CMD to ENTRYPOINT on Dockerfile

`
kubectl get pods -n=dev-to
`

`
kubectl port-forward -n=dev-to <pod_name> 5005:5005
`

## KubeNs and Stern

`
kubens dev-to
`

`
stern myapp
` 

## Start all

`make k:all`


## References

https://kubernetes.io/docs/home/

https://minikube.sigs.k8s.io/docs/
