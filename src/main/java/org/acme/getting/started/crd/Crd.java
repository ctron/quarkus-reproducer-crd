/*
 * Copyright 2018-2019, EnMasse authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package org.acme.getting.started.crd;

import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;

public class Crd {

    public static final String VERSION = "v1alpha1";
    public static final String GROUP = "foo.bar";
    public static final String API_VERSION = GROUP + "/" + VERSION;

    private static final CustomResourceDefinition FOO_CRD;

    static {
        FOO_CRD = CustomResources.createCustomResource(GROUP, VERSION, Foo.KIND);
    }

    public static void registerCustomCrds() {
        KubernetesDeserializer.registerCustomKind(API_VERSION, Foo.KIND, Foo.class);
        KubernetesDeserializer.registerCustomKind(API_VERSION, FooList.KIND, FooList.class);
    }

    public static CustomResourceDefinition foo() {
        return FOO_CRD;
    }

}
