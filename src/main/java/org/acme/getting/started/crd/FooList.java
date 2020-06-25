/*
 * Copyright 2018-2019, EnMasse authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package org.acme.getting.started.crd;

@DefaultCustomResource
@SuppressWarnings("serial")
public class FooList extends AbstractList<Foo> {

    public static final String KIND = "IoTProjectList";

    public FooList() {
        super(KIND, Crd.API_VERSION);
    }
}
