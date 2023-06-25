package xyz.hamster.tools.annotations;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

public enum EntityIdType {
    INTEGER("Integer"),
    LONG("Long");

    private final TypeName type;

    EntityIdType(String type) {
        this.type = type.equals("Integer")
                ? ClassName.get("java.lang", "Integer")
                : ClassName.get("java.lang", "Long");
    }

    public TypeName getType() {
        return type;
    }
}
