This is a reproducer for an issue Quarkus and CRDs when using native-image compilation.

## Instructions

### Build JVM

    mvn package
    docker build -f src/main/docker/Dockerfile.jvm . -t quay.io/ctrontesting/quarkus-reproducer:jvm
    docker push quay.io/ctrontesting/quarkus-reproducer:jvm

### Build native

    mvn package -Pnative,podman
    docker build -f src/main/docker/Dockerfile.native . -t quay.io/ctrontesting/quarkus-reproducer:native
    docker push quay.io/ctrontesting/quarkus-reproducer:native

### Deploy

You must be cluster admin for applying `000-CustomResourceDefinition.yaml`, all the rest should work as
a normal user.

    oc apply -f deploy/

### Switch image types

Replace the tag of `.spec.template.spec.containers[?(@.name==\"operator\")].image` with `native` for the native
image, and with `jvm` for the JVM based image.

## Problem description

In JVM mode, the informer properly reads and watches the resources. You can play with the `900-Foo.yaml` file,
create more copies, delete instances. Everything can be seen in the response of the `/hello` endpoint.

In native mode the informer fails with:

~~~
020-06-26 09:25:22,712 WARN  [io.fab.kub.cli.inf.cac.Controller] (informer-controller-Foo) Reflector list-watching job exiting because the thread-pool is shutting down: java.util.concurrent.RejectedExecutionException: Error while starting ReflectorRunnable watch
        at io.fabric8.kubernetes.client.informers.cache.Reflector.listAndWatch(Reflector.java:85)
        at io.fabric8.kubernetes.client.informers.cache.Controller.run(Controller.java:112)
        at java.lang.Thread.run(Thread.java:834)
        at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:497)
        at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:193)
Caused by: java.util.concurrent.RejectedExecutionException: Error while doing ReflectorRunnable list
        at io.fabric8.kubernetes.client.informers.cache.Reflector.getList(Reflector.java:73)
        at io.fabric8.kubernetes.client.informers.cache.Reflector.reListAndSync(Reflector.java:94)
        at io.fabric8.kubernetes.client.informers.cache.Reflector.listAndWatch(Reflector.java:80)
        ... 4 more
Caused by: io.fabric8.kubernetes.client.KubernetesClientException: An error has occurred.
        at io.fabric8.kubernetes.client.utils.Serialization.unmarshal(Serialization.java:248)
        at io.fabric8.kubernetes.client.utils.Serialization.unmarshal(Serialization.java:199)
        at io.fabric8.kubernetes.client.dsl.base.OperationSupport.handleResponse(OperationSupport.java:474)
        at io.fabric8.kubernetes.client.dsl.base.OperationSupport.handleResponse(OperationSupport.java:430)
        at io.fabric8.kubernetes.client.dsl.base.OperationSupport.handleResponse(OperationSupport.java:412)
        at io.fabric8.kubernetes.client.dsl.base.BaseOperation.listRequestHelper(BaseOperation.java:151)
        at io.fabric8.kubernetes.client.dsl.base.BaseOperation.list(BaseOperation.java:621)
        at io.fabric8.kubernetes.client.informers.SharedInformerFactory$1.list(SharedInformerFactory.java:134)
        at io.fabric8.kubernetes.client.informers.SharedInformerFactory$1.list(SharedInformerFactory.java:127)
        at io.fabric8.kubernetes.client.informers.cache.Reflector.getList(Reflector.java:67)
        ... 6 more
Caused by: com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "apiVersion" (class org.acme.getting.started.crd.FooList), not marked as ignorable (2 known properties: "items", "metadata"])
 at [Source: (BufferedInputStream); line: 1, column: 16] (through reference chain: org.acme.getting.started.crd.FooList["apiVersion"])
        at com.fasterxml.jackson.databind.DeserializationContext.handleUnknownProperty(DeserializationContext.java:843)
        at com.fasterxml.jackson.databind.deser.std.StdDeserializer.handleUnknownProperty(StdDeserializer.java:1206)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.handleUnknownProperty(BeanDeserializerBase.java:1610)
        at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.handleUnknownVanilla(BeanDeserializerBase.java:1588)
        at com.fasterxml.jackson.databind.deser.BeanDeserializer.vanillaDeserialize(BeanDeserializer.java:294)
        at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserialize(BeanDeserializer.java:151)
        at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4218)
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3259)
        at io.fabric8.kubernetes.client.utils.Serialization.unmarshal(Serialization.java:246)
        ... 15 more
~~~

The issue looks a bit different in EnMasse, but I still think that something is wrong here. I will try
to get it closer to the erroneous behavior that we see.
