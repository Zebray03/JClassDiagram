package diagram.model;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    public String name;
    public boolean isInterface;
    public boolean isEnum;
    public boolean isAbstract;
    public String genericParameters = "";
    public List<String> enumConstants = new ArrayList<>();
    public List<Attribute> attributes = new ArrayList<>();
    public List<Method> methods = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setEnum(boolean anEnum) {
        isEnum = anEnum;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public String getGenericParameters() {
        return genericParameters;
    }

    public void setGenericParameters(String genericParameters) {
        this.genericParameters = genericParameters;
    }

    public List<String> getEnumConstants() {
        return enumConstants;
    }

    public void setEnumConstants(List<String> enumConstants) {
        this.enumConstants = enumConstants;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }
}