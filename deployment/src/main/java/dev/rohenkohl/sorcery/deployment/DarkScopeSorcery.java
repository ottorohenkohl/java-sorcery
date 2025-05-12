package dev.rohenkohl.sorcery.deployment;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTransformation;
import org.jboss.jandex.DotName;

import static org.jboss.jandex.AnnotationTarget.Kind.CLASS;

public class DarkScopeSorcery {

    private static class DependentTransformer implements AnnotationTransformation {

        @Override
        public void apply(TransformationContext transformationContext) {
            if (transformationContext.declaration().kind() != CLASS || !hasSuffix(transformationContext)) return;

            transformationContext.remove(DependentTransformer::hasAnnotation);
            transformationContext.add(ApplicationScoped.class);
        }

        private static boolean hasAnnotation(AnnotationInstance annotationInstance) {
            return annotationInstance.name().equals(DotName.createSimple(Dependent.class));
        }

        private static boolean hasSuffix(TransformationContext context) {
            return context.declaration().asClass().name().toString().endsWith("Repository_");
        }
    }

    @BuildStep
    void transformRepositoryScopes(BuildProducer<AnnotationsTransformerBuildItem> buildProducer) {
        buildProducer.produce(new AnnotationsTransformerBuildItem(new DependentTransformer()));
    }
}
