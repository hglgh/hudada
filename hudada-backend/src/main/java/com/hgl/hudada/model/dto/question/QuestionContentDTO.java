package com.hgl.hudada.model.dto.question;

import lombok.Data;

import java.util.List;

/**
 * ClassName: QuestionContentDTO
 * Package: com.hgl.hudada.model.dto.question
 * Description:
 *
 * @Author HGL
 * @Create: 2025/6/9 9:08
 */
@Data
public class QuestionContentDTO {

    /**
     * 选项
     */
    private List<Option> options;

    /**
     * 题目
     */
    private String title;

    /**
     * 题目选项
     */
    @Data
    public static class Option {
        private String result;
        private Integer score;
        private String value;
        private String key;
    }
}
