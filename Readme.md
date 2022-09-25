# Serverless Architecture with Spring Native

### Serveless Architecture

Serverless differs from other cloud computing models in that the cloud provider is responsible for managing both the cloud infrastructure and the scaling of apps. Serverless apps are deployed in containers that automatically launch on demand when called.

Under a standard Infrastructure-as-a-Service (IaaS) cloud computing model, users prepurchase units of capacity, meaning you pay a public cloud provider for always-on server components to run your apps. It’s the user’s responsibility to scale up server capacity during times of high demand and to scale down when that capacity is no longer needed. The cloud infrastructure necessary to run an app is active even when the app isn’t being used.

With serverless architecture, by contrast, apps are launched only as needed. When an event triggers app code to run, the public cloud provider dynamically allocates resources for that code. The user stops paying when the code finishes executing. In addition to the cost and efficiency benefits, serverless frees developers from routine and menial tasks associated with app scaling and server provisioning.

With serverless, routine tasks such as managing the operating system and file system, security patches, load balancing, capacity management, scaling, logging, and monitoring are all offloaded to a cloud services provider.

It’s possible to build an entirely serverless app, or an app composed of partially serverless and partially traditional microservices components.



![Serverless architecture example](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/eae9izs09qfp8822i616.png)

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

Command for crate S3 bucket:
```
aws s3 mb s3://native-bucket --endpoint-url http://localhost:4566
```

Command for create Aws Lambda(Function1):
```
aws lambda create-function \
--endpoint-url http://localhost:4566 \
--function-name function1 \
--runtime java17 \
--handler function1.S3Handler \
--region us-east-1 \
--zip-file fileb://function1-0.0.1-SNAPSHOT.jar \
--role arn:aws:iam::12345:role/ignoreme
```

Command for create Aws Lambda(Function2):
```
aws lambda create-function \
--endpoint-url http://localhost:4566 \
--function-name function2 \
--runtime java17 \
--handler function2.S3Handler \
--region us-east-1 \
--zip-file fileb://function2-0.0.1-SNAPSHOT.jar \
--role arn:aws:iam::12345:role/ignoreme
```

Following all the steps, when we execute the command receive the return with this message:
```
{
    "FunctionName": "function1",
    "FunctionArn": "arn:aws:lambda:us-east-1:000000000000:function:function1",
    "Runtime": "java17",
    "Role": "arn:aws:iam::12345:role/ignoreme",
    "Handler": "function1.S3Handler",
    "CodeSize": 91807,
    "Description": "",
    "Timeout": 3,
    "LastModified": "2022-09-25T18:10:57.187+0000",
    "CodeSha256": "JDYOPtjvkoP17EKU5Fhu45GGFrDve0tJSe2iRccEb9g=",
    "Version": "$LATEST",
    "VpcConfig": {},
    "TracingConfig": {
        "Mode": "PassThrough"
    },
    "RevisionId": "1acedcab-1c5d-4b33-932a-bc2e6a2a67da",
    "State": "Active",
    "LastUpdateStatus": "Successful",
    "PackageType": "Zip",
    "Architectures": [
        "x86_64"
    ]
```

**Registering our Lambda to S3 bucket events**

Finally, we need to bind the lambda to the put-bucket-notification event within LocalStack. Any time that an object is created within our native-image S3 bucket, it will invoke our earlier Java code.

**Function1**
```
aws s3api put-bucket-notification-configuration --bucket native-image --notification-configuration file://s3hook.json --endpoint-url http://localhost:4566
```

**Invoking a Lambda within LocalStack**

Create a simple text file called samplefile.txt and run the following command to transfer the file to S3, thereby triggering the Lambda:
```
aws s3 cp uploadfile.txt s3://native-bucket/uploadfile.txt --endpoint-url http://localhost:4566
```

Time to start/running the functions with spring native.
```
INFO 38045 --- [main] c.l.s.f.Function1ApplicationTests : Started Function1ApplicationTests in 0.64 seconds (JVM running for 1.13)
```

### Project description
Project that builds an group of the function running GraalVM, Spring Native and Spring Cloud Functions.


![Serverless Implementation](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/wsx53kq3rib5pa825ocy.png)

## Installation ##

It is necessary to install some items:
- Docker
- Maven
- GraalVM
- Localstack
- Spring Cloud Functions

