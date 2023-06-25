package xyz.hamster.tools.annotations.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import xyz.hamster.tools.annotations.ExcludeFromDTO;
import xyz.hamster.tools.annotations.GenerateDTO;
import xyz.hamster.tools.utils.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("xyz.hamster.tools.annotations.GenerateDTO")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class GenerateDtoProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                try {
                    if (element.getKind() != ElementKind.CLASS) {
                        throw new RuntimeException("Annotation can only be used on classes");
                    }
                    String className = element.getSimpleName().toString().replace("Entity", "") + "Dto";

                    List<Element> suitableFields = element.getEnclosedElements().stream()
                            .filter(e -> e.getKind().isField())
                            .filter(e -> e.getAnnotation(ExcludeFromDTO.class) == null)
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
                    if (element.getAnnotation(GenerateDTO.class).builder()) {
                        classItemBuilder.addType(createBuilder(suitableFields, className));
                        classItemBuilder.addMethod(createBuilderConstructor(suitableFields));
                    }

                    // add default constructor
                    if (element.getAnnotation(GenerateDTO.class).constructor()) {
                        classItemBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
                    }

                    JavaFile.builder("xyz.toway.hamsterstore.dto", classItemBuilder.build())
                            .build()
                            .writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    private MethodSpec createGetter(Element e) {
        return MethodSpec.methodBuilder("get" + Utils.capitalizeString(e.getSimpleName().toString()))
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(e.asType()))
                .addStatement("return this." + e.getSimpleName().toString())
                .build();
    }

    private MethodSpec createSetter(Element e) {
        String name = e.getSimpleName().toString();
        return MethodSpec.methodBuilder("set" + Utils.capitalizeString(name))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(e.asType()), name)
                .addStatement("this." + name + " = " + name)
                .build();
    }

    private MethodSpec createBuilderSetter(Element e) {
        String name = e.getSimpleName().toString();
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(e.asType()), name)
                .addStatement("this." + name + " = " + name)
                .addStatement("return this")
                .returns(ClassName.get("", "Builder"))
                .build();
    }

    private FieldSpec createField(Element e) {
        return FieldSpec.builder(ClassName.get(e.asType()), e.getSimpleName().toString(), Modifier.PRIVATE).build();
    }

    private TypeSpec createBuilder(List<Element> fields, String className) {
        MethodSpec buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("", className))
                .addStatement("return new " + className + "(this)", className)
                .build();

        return TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addFields(fields.stream().map(this::createField).collect(Collectors.toList()))
                .addMethods(fields.stream().map(this::createBuilderSetter).collect(Collectors.toList()))
                .addMethod(buildMethod)
                .build();
    }

    private MethodSpec createBuilderConstructor(List<Element> fields) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(ClassName.get("", "Builder"), "builder").build());
        fields.forEach(field -> constructorBuilder.addStatement("this." + field.getSimpleName().toString() + " = builder." + field.getSimpleName().toString()));
        return constructorBuilder.build();
    }
}