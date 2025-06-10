package com.hgl.hudada.model.dto.userAnswer;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 更新用户答案请求
 *
 * @author 请别把我整破防
 */
@Data
public class UserAnswerUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;

    @Serial
    private static final long serialVersionUID = 1L;
}