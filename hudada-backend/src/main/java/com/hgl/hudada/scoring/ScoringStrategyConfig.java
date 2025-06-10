package com.hgl.hudada.scoring;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: ScoringStrategyConfig
 * Package: com.hgl.hudada.scoring
 * Description:
 *
 * @Author HGL
 * @Create: 2025/6/10 13:37
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScoringStrategyConfig {

    /**
     * app类型
     * @return
     */
    int appType();

    /**
     * 评分策略
     * @return
     */
    int scoringStrategy();
}
