package xyz.hamster.tools.annotations.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import xyz.hamster.tools.annotations.EntityIdType;
import xyz.hamster.tools.annotations.GenerateJpaRepository;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("xyz.hamster.tools.annotations.GenerateJpaRepository")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class GenerateJpaRepositoryProcessor extends AnnotationProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                try {
                    checkElementKindClass(element);

                    ClassName entityType = ClassName.get(getPackageName(element), element.getSimpleName().toString());
                    TypeName entityIdType = EntityIdType.valueOf(element.getAnnotation(GenerateJpaRepository.class).id().toString()).getType();
                    TypeName collectionType = ParameterizedTypeName.get(ClassName.get("java.util", "List"), entityType);
                    TypeName crudInterface = ParameterizedTypeName.get(ClassName.get("org.springframework.data.jpa.repository", "JpaRepository"), entityType, entityIdType);

                    TypeSpec interfaceType = TypeSpec.interfaceBuilder(getDestinationClassName(element))
                            .addModifiers(Modifier.PUBLIC)
                            .addSuperinterface(crudInterface)
                            .build();

                    // add 'getAllByActive' method
                    if (Boolean.TRUE.equals(element.getAnnotation(GenerateJpaRepository.class).hasActiveFlag())) {
                        MethodSpec getAllByActive = MethodSpec.methodBuilder("getAllByActive")
                                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                .addParameter(boolean.class, "active")
                                .returns(collectionType)
                                .build();
                        interfaceType = interfaceType.toBuilder().addMethod(getAllByActive).build();
                    }

                    // write generated java file
                    JavaFile.builder(getDestinationPackage(element), interfaceType)
                            .build()
                            .writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    private String getDestinationPackage(Element element) {
        String packageName = element.getAnnotation(GenerateJpaRepository.class).destinationPackage();
        if (!stringHasValue(packageName)) {
            packageName = getPackageName(element);
        }
        return packageName;
    }

    private String getDestinationClassName(Element element) {
        String nameSuffix = "Repository";
        String className = replaceIfEndsWith(element.getSimpleName().toString(), "Entity", nameSuffix);
        if (!className.endsWith(nameSuffix)) {
            className += nameSuffix;
        }
        return className;
    }
}
