package xyz.hamster.tools.annotations.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import xyz.hamster.tools.annotations.EntityIdType;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static xyz.hamster.tools.annotations.processors.ProcessorUtils.getPackageName;
import static xyz.hamster.tools.utils.Utils.replaceIfEndsWith;
import static xyz.hamster.tools.utils.Utils.stringHasValue;

public class BaseRepositoryProcessor {

    //todo
    // избавится от метода init
    // добавлять в репозиторий кастомные методы (передавая массив методов в параметрах аннотации)
/*
    private static final String JPA_REPOSITORY = "xyz.hamster.tools.annotations.GenerateRepository";
    private static final String REACTIVE_REPOSITORY = "xyz.hamster.tools.annotations.GenerateReactiveRepository";

    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elements = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
                JPA_REPOSITORY,
                REACTIVE_REPOSITORY
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                try {
                    if (element.getKind() != ElementKind.CLASS) {
                        throw new RuntimeException("Annotation can only be used on classes");
                    }

                    //if (annotation.getQualifiedName().toString().equals("com.example.Annotation1"))

                    String annotationName = annotation.getQualifiedName().toString();

                    AnnotationMirror annotationMirror = getAnnotationMirror(element, annotation);
                    TypeName entityIdType = EntityIdType.valueOf(Objects.requireNonNull(getPropertyValue("id", annotationMirror)).toString()).getType();
                    Boolean includeActive = (Boolean) getPropertyValue("activeMethod", annotationMirror);
                    String destinationPackage = (String) getPropertyValue("destinationPackage", annotationMirror);

                    String className = element.getSimpleName().toString();
                    String packageOfEntity = getPackageName(element, elements);
                    ClassName entityType = ClassName.get(packageOfEntity, className);
                    TypeName listOfEntity = ParameterizedTypeName.get(getCollectionClassName(annotationName), entityType);
                    TypeName crudOfEntity = ParameterizedTypeName.get(getCrudClassName(annotationName), entityType, entityIdType);

                    TypeSpec interfaceType = TypeSpec.interfaceBuilder(replaceIfEndsWith(className, "Entity", "Repository"))
                            .addModifiers(Modifier.PUBLIC)
                            .addSuperinterface(crudOfEntity)
                            .build();

                    if (Boolean.TRUE.equals(includeActive)) {
                        MethodSpec getAllByActive = MethodSpec.methodBuilder("getAllByActive")
                                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                .addParameter(boolean.class, "active")
                                .returns(listOfEntity)
                                .build();
                        interfaceType = interfaceType.toBuilder().addMethod(getAllByActive).build();
                    }

                    String packageName = stringHasValue(destinationPackage) ? destinationPackage : replaceIfEndsWith(packageOfEntity, "entity", "repository");
                    JavaFile.builder(packageName, interfaceType)
                            .build()
                            .writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }

    private ClassName getCrudClassName(String annotation) {
        if (annotation == null) return null;
        switch (annotation) {
            case JPA_REPOSITORY:
                return ClassName.get("org.springframework.data.jpa.repository", "JpaRepository");
            case REACTIVE_REPOSITORY:
                return ClassName.get("org.springframework.data.repository.reactive", "ReactiveCrudRepository");
            default:
                return null;
        }
    }

    private ClassName getCollectionClassName(String annotation) {
        if (annotation == null) return null;
        switch (annotation) {
            case JPA_REPOSITORY:
                return ClassName.get("java.util", "List");
            case REACTIVE_REPOSITORY:
                return ClassName.get("reactor.core.publisher", "Flux");
            default:
                return null;
        }
    }

    private AnnotationMirror getAnnotationMirror(Element element, TypeElement annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().asElement().equals(annotation)) {
                return annotationMirror;
            }
        }
        return null;
    }

    private Object getPropertyValue(String property, AnnotationMirror annotationMirror) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
        for (ExecutableElement key : elementValues.keySet()) {
            String propertyName = key.getSimpleName().toString();
            Object propertyValue = elementValues.get(key).getValue();
            if (propertyName.equals(property)) {
                return propertyValue;
            }
        }
        return null;
    }*/
}
