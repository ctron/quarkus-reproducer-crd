package org.acme.getting.started;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.quarkus.runtime.StartupEvent;
import org.acme.getting.started.crd.Crd;
import org.acme.getting.started.crd.CustomResources;
import org.acme.getting.started.crd.Foo;
import org.acme.getting.started.crd.FooList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MyService {

    private static final Logger log = LoggerFactory.getLogger(MyService.class);

    @Inject
    KubernetesClient client;

    private SharedInformerFactory factory;
    private SharedIndexInformer<Foo> informer;

    public void onStart(@Observes StartupEvent event) {

        // create a new informer factory
        this.factory = client.informers();

        // setup informer
        this.informer = this.factory.sharedIndexInformerForCustomResource(
                CustomResources.toContext(Crd.foo()),
                Foo.class, FooList.class, 10_000);

        informer.addEventHandler(new ResourceEventHandler<Foo>() {

                    @Override
                    public void onUpdate(final Foo oldObj, final Foo newObj) {
                        log.info("onUpdate - old: {}, new: {}", oldObj, newObj);
                    }

                    @Override
                    public void onDelete(final Foo obj, final boolean deletedFinalStateUnknown) {
                        log.info("onDelete - obj: {}, deletedFinalStateUnknown: {}", obj, deletedFinalStateUnknown);
                    }

                    @Override
                    public void onAdd(final Foo obj) {
                        log.info("onAdd - obj: {}", obj);
                    }
                });

        // this only starts informers created by this factory instance
        this.factory.startAllRegisteredInformers();
    }

    public List<Foo> getFoos() {
        return new ArrayList<>(this.informer.getIndexer().list());
    }

}
