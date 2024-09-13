package io.github.carrothole.processor.generateo.entity;

import io.github.carrothole.processor.generateo.enums.VOTypeEnum;

/**
 * Description: 类成员变量 <br>
 * Date: 2024/9/4 13:43 <br>
 *
 * @author moon
 * @since 0.0.1
 */
public class FieldInfo {

    private String name;

    private String type;

    private String simpleType;

    private String describe;

    private VOTypeEnum[] types;

    private boolean hasQuery = false;
    private boolean hasResult = false;

    public FieldInfo() {
    }

    public FieldInfo(String name, String type, String describe, VOTypeEnum[] types) {
        this.name = name;
        setType(type);
        this.describe = describe;
        setTypes(types);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        String[] split = type.split("\\.");
        this.simpleType = split[split.length-1];
    }

    public String getSimpleType() {
        return simpleType;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public VOTypeEnum[] getTypes() {
        return types;
    }

    public boolean isHasQuery() {
        return hasQuery;
    }

    public boolean isHasResult() {
        return hasResult;
    }

    public void setTypes(VOTypeEnum[] types) {
        this.types = types;
        for (VOTypeEnum voTypeEnum : types) {
            switch (voTypeEnum){
                case QUERY -> this.hasQuery = true;
                case RESULT -> this.hasResult = true;
            }
        }
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", simpleType='" + simpleType + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
