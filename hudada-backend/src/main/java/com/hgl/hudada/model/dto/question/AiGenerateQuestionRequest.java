package com.hgl.hudada.model.dto.question;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName AiGenerateQuestionRequest
 * @Author 请别把我整破防
 * @Description //TODO
 * @Date 2025/6/20 9:33
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {

    /**
     * id
     */
    private Long appId;

    /**
     * 题目数
     */
    int questionNumber = 10;

    /**
     * 选项数
     */
    int optionNumber = 2;

    @Serial
    private static final long serialVersionUID = 1L;
}

