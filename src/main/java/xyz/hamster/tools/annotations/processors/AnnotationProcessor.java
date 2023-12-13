package xyz.hamster.tools.annotations.processors;

import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AnnotationProcessor extends AbstractProcessor {

    protected Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elements = processingEnv.getElementUtils();
    }

    protected MethodSpec createGetter(Element e) {
        String name = e.getSimpleName().toString();
        return MethodSpec.methodBuilder("get" + capitalize(name))
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(e.asType()))
                .addStatement("return this." + name)
                .build();
    }

    protected MethodSpec createSetter(Element e) {
        String name = e.getSimpleName().toString();
        return MethodSpec.methodBuilder("set" + capitalize(name))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(e.asType()), name)
                .addStatement("this." + name + " = " + name)
                .build();
    }

    protected FieldSpec createField(Element e) {
        return FieldSpec.builder(ClassName.get(e.asType()), e.getSimpleName().toString(), Modifier.PRIVATE).build();
    }

    protected String getPackageName(Element element) {
        PackageElement packageElement = elements.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }

    protected TypeSpec createBuilder(List<Element> fields, String className) {
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

    protected MethodSpec createBuilderConstructor(List<Element> fields) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(ClassName.get("", "Builder"), "builder").build());
        fields.forEach(field -> constructorBuilder.addStatement("this." + field.getSimpleName().toString() + " = builder." + field.getSimpleName().toString()));
        return constructorBuilder.build();
    }

    protected List<Element> getFields(Element element) {
        return element.getEnclosedElements().stream()
                .filter(e -> e.getKind().isField())
                .collect(Collectors.toList());
    }

    protected void checkElementKindClass(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            throw new RuntimeException("Annotation can only be used on classes");
        }
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

    protected String capitalize(String str) {
        return str == null ? null : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    protected String replaceIfEndsWith(String input, String target, String replacement) {
        if (input.endsWith(target)) {
            int lastIndex = input.lastIndexOf(target);
            if (lastIndex > 0) {
                return input.substring(0, lastIndex) + replacement;
            } else {
                return replacement;
            }
        }
        return input;
    }

    protected boolean stringHasValue(String str) {
        return str != null && !str.isBlank();
    }
}
