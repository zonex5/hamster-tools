package xyz.hamster.tools.annotations.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import xyz.hamster.tools.annotations.DataTransferObject;
import xyz.hamster.tools.annotations.Exclude;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("xyz.hamster.tools.annotations.DataTransferObject")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class DataTransferObjectProcessor extends AnnotationProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                try {
                    checkElementKindClass(element);

                    String className = getDestinationClassName(element);

                    // exclude marked fields
                    List<Element> suitableFields = getFields(element).stream()
                            .filter(e -> e.getAnnotation(Exclude.class) == null)
                            .collect(Collectors.toList());

                    List<FieldSpec> fields = suitableFields.stream().map(this::createField).collect(Collectors.toList());
                    List<MethodSpec> getters = suitableFields.stream().map(this::createGetter).collect(Collectors.toList());
                    List<MethodSpec> setters = suitableFields.stream().map(this::createSetter).collect(Collectors.toList());

                    TypeSpec.Builder classItemBuilder = TypeSpec.classBuilder(className)
                            .addModifiers(Modifier.PUBLIC)
                            .addFields(fields)
                            .addMethods(getters)
                            .addMethods(setters);

                    // add builder
                    if (element.getAnnotation(DataTransferObject.class).builder()) {
                        classItemBuilder.addType(createBuilder(suitableFields, className));
                        classItemBuilder.addMethod(createBuilderConstructor(suitableFields));
                        classItemBuilder.addMethod(createBuilderStaticMethod());
                    }

                    // add default constructor
                    if (element.getAnnotation(DataTransferObject.class).constructor()) {
                        classItemBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
                    }

                    // write generated java file
                    JavaFile.builder(getDestinationPackage(element), classItemBuilder.build())
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
        String packageName = element.getAnnotation(DataTransferObject.class).destinationPackage();
        return stringHasValue(packageName) ? packageName : getPackageName(element);
    }

    private String getDestinationClassName(Element element) {
        String providedName = element.getAnnotation(DataTransferObject.class).className();
        return stringHasValue(providedName) ? providedName : replaceIfEndsWith(element.getSimpleName().toString(), "Entity", "") + "Dto";
    }
}