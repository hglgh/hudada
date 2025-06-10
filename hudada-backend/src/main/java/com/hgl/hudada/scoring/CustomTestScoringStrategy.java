package com.hgl.hudada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hgl.hudada.model.dto.question.QuestionContentDTO;
import com.hgl.hudada.model.entity.App;
import com.hgl.hudada.model.entity.Question;
import com.hgl.hudada.model.entity.ScoringResult;
import com.hgl.hudada.model.entity.UserAnswer;
import com.hgl.hudada.model.vo.QuestionVO;
import com.hgl.hudada.service.QuestionService;
import com.hgl.hudada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: CustomTestScoringStrategy
 * Package: com.hgl.hudada.scoring
 * Description:
 *
 * @Author HGL
 * @Create: 2025/6/10 10:06
 * @Descieption: 自定义测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {

        //1.根据 id 查询到题目和题目结果信息()
        Long appId = app.getId();
        Question question = questionService.getOne(
                new LambdaQueryWrapper<Question>()
                        .eq(Question::getAppId, appId)
        );

        List<ScoringResult> scoringResultList = scoringResultService.list(
                new LambdaQueryWrapper<ScoringResult>()
                        .eq(ScoringResult::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        ScoringResult bestQuestionResult = getBestQuestionResult(choices, questionContent, scoringResultList);
        // 4. 构造返回值，填充答案对象的属性
        return CustomScoreScoringStrategy.buildUserAnswer(choices, app, bestQuestionResult);
    }

    /**
     * 获取最高分数对应的评分结果
     *
     * @param answerList      用户选择的答案列表
     * @param questions       题目列表，每个题目包含选项
     * @param questionResults 评分结果列表，每个评分结果包含多个属性
     * @return 最高分数对应的评分结果
     */
    private ScoringResult getBestQuestionResult(List<String> answerList,
                                                List<QuestionContentDTO> questions,
                                                List<ScoringResult> questionResults) {
        // 2. 统计用户每个选择对应的属性个数，如 I = 10 个，E = 5 个
        // 初始化一个 Map，用于存储每个选项的计数
        Map<String, Integer> optionCount = new HashMap<>();

        // 遍历题目列表
        for (QuestionContentDTO questionContentDTO : questions) {
            // 遍历答案列表
            for (String answer : answerList) {
                // 遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果答案和选项的 key 匹配
                    if (option.getKey().equals(answer)) {
                        // 获取选项的 result 属性
                        String result = option.getResult();

                        // 如果 result 属性不在 optionCount 中，初始化为 0
                        optionCount.put(result, optionCount.getOrDefault(result, 0) + 1);
                    }
                }
            }
        }

        // 3. 遍历每种评分结果，计算哪个结果的得分更高
        // 初始化最高分数和最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoreResult = questionResults.get(0);

        // 遍历评分结果列表
        for (ScoringResult result : questionResults) {
            // 计算当前评分结果的分数
            List<String> resultProp = JSONUtil.toList(result.getResultProp(), String.class);
            /*
            int score = 0;
            for (String prop : resultProp) {
                score += optionCount.getOrDefault(prop, 0);
            }
             */
            int score = resultProp.stream().mapToInt(prop -> optionCount.getOrDefault(prop, 0)).sum();

            // 如果分数高于当前最高分数，更新最高分数和最高分数对应的评分结果
            if (score > maxScore) {
                maxScore = score;
                maxScoreResult = result;
            }
        }

        // 返回最高分数和最高分数对应的评分结果
        return maxScoreResult;
    }
}
