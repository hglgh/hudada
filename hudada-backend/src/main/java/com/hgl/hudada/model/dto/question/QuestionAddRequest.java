package com.hgl.hudada.model.dto.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建题目请求
 *
 * @author 请别把我整破防
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentDTO> questionContent;

    /**
     * 应用 id
     */
    private Long appId;

    @Serial
    private static final long serialVersionUID = 1L;
}