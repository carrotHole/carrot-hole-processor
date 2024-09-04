package com.carrothole.processor.generateo.entity;

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
     * 包名称
     */
    private String packageName;

    /**
     * 要导入的包
     */
    private Set<String> imports;

    /**
     * 写出类位置
     */
    private String writePath;

    private Set<FieldInfo> fields;

    public ClassInfo() {
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

    public void setImports(Set<String> imports) {
        this.imports = imports;
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

    public void setFields(Set<FieldInfo> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", imports=" + imports +
                ", exportName='" + writePath + '\'' +
                ", fields=" + fields +
                '}';
    }
}
