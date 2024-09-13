package io.github.carrothole.processor.generateo.enums;

/**
 * @author moon
 * @since 0.0.3
 */
public enum VOTypeEnum {
    QUERY("qo","QueryVO"), RESULT("ro","ResultVO");

    private String prefix;
    private String suffix;
    VOTypeEnum(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
    public static VOTypeEnum getByPrefix(String prefix){
        for (VOTypeEnum value : VOTypeEnum.values()) {
            if(value.getPrefix().equals(prefix)){
                return value;
            }
        }
        return null;
    }
}
