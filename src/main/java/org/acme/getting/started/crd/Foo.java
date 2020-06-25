package org.acme.getting.started.crd;

import io.fabric8.kubernetes.api.model.Doneable;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;
import io.sundr.builder.annotations.Inline;

@Buildable(
        editableEnabled = false,
        generateBuilderPackage = false,
        builderPackage = "io.fabric8.kubernetes.api.builder",
        refs = {@BuildableReference(AbstractHasMetadata.class)},
        inline = @Inline(
                type = Doneable.class,
                prefix = "Doneable",
                value = "done"))
@DefaultCustomResource
public class Foo extends AbstractHasMetadata<Foo> {
    public static final String KIND = "Foo";

    public Foo() {
        super(KIND, Crd.API_VERSION);
    }
}
