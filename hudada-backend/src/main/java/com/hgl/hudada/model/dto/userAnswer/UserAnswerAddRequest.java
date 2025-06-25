package com.hgl.hudada.model.dto.userAnswer;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建用户答案请求
 *
 * @author 请别把我整破防
 */
@Data
public class UserAnswerAddRequest implements Serializable {

    /**
     * id,来实现幂等性，避免重复提交
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