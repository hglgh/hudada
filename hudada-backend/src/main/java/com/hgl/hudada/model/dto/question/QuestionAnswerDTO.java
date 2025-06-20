package com.hgl.hudada.model.dto.question;

import lombok.Data;

/**
 * @author 请别把我整破防
 */
@Data
public class QuestionAnswerDTO {

    /**
     * 题目
     */
    private String title;

    /**
     * 用户答案
     */
    private String userAnswer;
}
