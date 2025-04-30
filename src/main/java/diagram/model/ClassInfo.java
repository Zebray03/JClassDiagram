package diagram.model;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    private String name;
    private boolean isInterface;
    private boolean isEnum;
    private boolean isAbstract;
    private String genericParameters = "";
    private List<String> enumConstants = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();
    private List<Method> methods = new ArrayList<>();
    private boolean hasConstructor;
    private List<ClassInfo> children = new ArrayList<>();
    private boolean isGodClass;
    private boolean isLazyClass;
    private boolean isDataClass;
    private List<String> implementedTypes = new ArrayList<>();

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

    public boolean hasConstructor() {
        return hasConstructor;
    }

    public void setHasConstructor(boolean hasConstructor) {
        this.hasConstructor = hasConstructor;
    }

    public List<ClassInfo> getChildren() {
        return children;
    }

    public void setChildren(List<ClassInfo> children) {
        this.children = children;
    }

    public boolean isGodClass() {
        return isGodClass;
    }

    public void setGodClass(boolean isGodClass) {
        this.isGodClass = isGodClass;
    }

    public boolean isLazyClass() {
        return isLazyClass;
    }

    public void setLazyClass(boolean isLazyClass) {
        this.isLazyClass = isLazyClass;
    }

    public boolean isDataClass() {
        return isDataClass;
    }

    public void setDataClass(boolean isDataClass) {
        this.isDataClass = isDataClass;
    }

    public List<String> getImplementedTypes() {
        return implementedTypes;
    }
}
