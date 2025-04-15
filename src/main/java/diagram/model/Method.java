package diagram.model;

import java.util.ArrayList;
import java.util.List;

public class Method {
    private String visibility;
    private String returnType;
    private String name;
    private List<Parameter> parameters = new ArrayList<>();
    private boolean isStatic;
    private boolean isAbstract;
    private String genericParameters = "";

    // 新增字段：是否为构造方法
    private boolean isConstructor;

    // Getter 和 Setter 方法

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
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

    // 新增isConstructor方法，用于判断是否为构造方法
    public boolean isConstructor() {
        return isConstructor;
    }

    // 新增setConstructor方法
    public void setConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }
}
