# Serverless Architecture with Spring Native

### Project description
Project that builds an group of the function running GraalVM, Spring Native and Spring Cloud Functions.

## Installation ##

It is necessary to install some items:
- Docker
- Maven
- GraalVM
- Localstack
- Spring Cloud Functions

### Serveless Architecture

Serverless differs from other cloud computing models in that the cloud provider is responsible for managing both the cloud infrastructure and the scaling of apps. Serverless apps are deployed in containers that automatically launch on demand when called.

Under a standard Infrastructure-as-a-Service (IaaS) cloud computing model, users prepurchase units of capacity, meaning you pay a public cloud provider for always-on server components to run your apps. It’s the user’s responsibility to scale up server capacity during times of high demand and to scale down when that capacity is no longer needed. The cloud infrastructure necessary to run an app is active even when the app isn’t being used.

With serverless architecture, by contrast, apps are launched only as needed. When an event triggers app code to run, the public cloud provider dynamically allocates resources for that code. The user stops paying when the code finishes executing. In addition to the cost and efficiency benefits, serverless frees developers from routine and menial tasks associated with app scaling and server provisioning.

With serverless, routine tasks such as managing the operating system and file system, security patches, load balancing, capacity management, scaling, logging, and monitoring are all offloaded to a cloud services provider.

It’s possible to build an entirely serverless app, or an app composed of partially serverless and partially traditional microservices components.

### Native Image
Native image is a technology that allows you to compile Java code in advance and make it executable. This executable contains application classes, their dependency classes, runtime library classes, and native code linked to the JDK. After the build process, it builds the executable for the operating system, this process is called image build. JVM-based languages have this capability like Java, Scala, Clojure and Kotlin.

### Spring Native
In March 2021, Pivotal released a still-experimental version to the community of its native module, Spring-Native. Given that its competitors are already able to have a more fluid integration with GraalVM, spring had to chase after Quarkus and Micronaut to be able to make its stack compatible with native images.

Spring Native provides the ideal workload for computing in containers and Kubernetes. Use strong native image an instant boot, instant peak performance and reduced memory consumption.

As everything is not perfect, it is necessary to understand that there are steps in this process that need to be improved, as the process of generating a native image is very heavy and time-consuming.

### Environment/Localstack
We use *Localstack* for emulate on local machine our Aws environment. To start the localstack in the repository have a file docker-compose.yml

For start localstack running this command: 
```
docker-compose up -d
```

### Necessary Steps

Running the command below: 
```
mvn -ntp clean package -Pnative --settings ./settings-spring-native.xml
```

Time to start/running the functions with spring native.
```
INFO 38045 --- [main] c.l.s.f.Function1ApplicationTests : Started Function1ApplicationTests in 0.64 seconds (JVM running for 1.13)
```
