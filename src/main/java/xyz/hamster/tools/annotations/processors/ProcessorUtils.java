package xyz.hamster.tools.annotations.processors;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;

public class ProcessorUtils {
    public static String getPackageName(Element element, Elements elements) {
        PackageElement packageElement = elements.getPackageOf(element);
        return packageElement.getQualifiedName().toString();
    }
}
