package org.example.compiler.ast;



import java.util.HashMap;
import java.util.Map;

public class ObjectInstance {
    private final ClassDeclaration classDecl;
    private final Map<String, Object> fields = new HashMap<>();

    public ObjectInstance(ClassDeclaration classDecl) {
        this.classDecl = classDecl;
        
    }

    public Object getField(String name) {
        if (!fields.containsKey(name)) {
            throw new RuntimeException("Field '" + name + "' not found in object of class " + classDecl.name());
        }
        return fields.get(name);
    }

    public void setField(String name, Object value) {
        fields.put(name, value);
    }

    public ClassDeclaration getClassDecl() {
        return classDecl;
    }

}
