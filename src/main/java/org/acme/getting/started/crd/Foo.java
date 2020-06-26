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

    private FooSpec spec;
    private FooStatus status;

    public Foo() {
        super(KIND, Crd.API_VERSION);
    }

    public FooSpec getSpec() {
        return this.spec;
    }

    public void setSpec(final FooSpec spec) {
        this.spec = spec;
    }

    public FooStatus getStatus() {
        return this.status;
    }

    public void setStatus(final FooStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Foo{");
        sb.append("namespace=").append(getMetadata().getNamespace());
        sb.append(", name=").append(getMetadata().getName());
        sb.append('}');
        return sb.toString();
    }
}
