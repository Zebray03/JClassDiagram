package diagram.model;

import java.util.ArrayList;
import java.util.List;

public class Method {
    public String visibility;
    public String returnType;
    public String name;
    public List<Parameter> parameters = new ArrayList<>();
    public boolean isStatic;
    public boolean isAbstract;
    public String genericParameters = "";

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
}