package com.hgl.hudada.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: ReviewStatusEnum
 * Package: com.hgl.hudada.model.enums
 * Description:
 *
 * @Author HGL
 * @Create: 2025/6/6 17:26
 */
@Getter
public enum AppTypeEnum {

    SCORE("评分类", 0),
    TEST("测试类", 1);

    private final String text;
    private final int value;

    AppTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static AppTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (AppTypeEnum anEnum : AppTypeEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取全部枚举值
     *
     * @return List<Integer>
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
}
