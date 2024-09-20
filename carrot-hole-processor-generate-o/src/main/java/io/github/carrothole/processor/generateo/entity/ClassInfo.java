package io.github.carrothole.processor.generateo.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 类对象 <br>
 * Date: 2024/9/4 14:55 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public class ClassInfo {

    /**
     * 类名称
     */
    private String name;

    /**
     * 类描述
     */
    private String description;

    /**
     * 包名称
     */
    private String packageName;


    /**
     * 写出类位置
     */
    private String writePath;

    /**
     * 要导入的包
     */
    private final Set<String> imports;


    private final Set<FieldInfo> fields;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClassInfo() {
        fields = new HashSet<>();
        imports = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getImports() {
        return imports;
    }

    public String getWritePath() {
        return writePath;
    }

    public void setWritePath(String writePath) {
        this.writePath = writePath;
    }

    public Set<FieldInfo> getFields() {
        return fields;
    }

    public void addFields(FieldInfo... fields) {

        for (FieldInfo field : fields) {
            this.fields.add(field);
            if (!field.getType().startsWith("java.lang")){
                imports.add(field.getType());
            }
            String[] annotations = field.getAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                String annotation = annotations[i];
                imports.add(annotation.replace("@", "").split("\\(")[0]);
            }
        }
    }

    public void addImport(String... imports) {
        this.imports.addAll(Arrays.asList(imports));
    }


}
