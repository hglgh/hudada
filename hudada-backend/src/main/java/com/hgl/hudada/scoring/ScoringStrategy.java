package com.hgl.hudada.scoring;

import com.hgl.hudada.model.entity.App;
import com.hgl.hudada.model.entity.UserAnswer;

import java.util.List;

/**
 * @author 请别把我整破防
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     *
     * @param choices 用户选择的答案
     * @param app     应用
     * @return 评分结果
     * @throws Exception 异常
     */
    UserAnswer doScore(List<String> choices, App app) throws Exception;
}
