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
    private boolean isConstructor;

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

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }
}
