package com.hgl.hudada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hgl.hudada.common.ErrorCode;
import com.hgl.hudada.exception.ThrowUtils;
import com.hgl.hudada.model.dto.question.QuestionContentDTO;
import com.hgl.hudada.model.entity.App;
import com.hgl.hudada.model.entity.Question;
import com.hgl.hudada.model.entity.ScoringResult;
import com.hgl.hudada.model.entity.UserAnswer;
import com.hgl.hudada.model.vo.QuestionVO;
import com.hgl.hudada.service.QuestionService;
import com.hgl.hudada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ClassName: CustomScoreScoringStrategy
 * Package: com.hgl.hudada.scoring
 * Description:
 *
 * @Author HGL
 * @Create: 2025/6/10 8:51
 * @Descieption: 自定义打分类应用评分策略
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {

        //1.根据 id 查询到题目和题目结果信息(按分数降序排序)
        Long appId = app.getId();
        Question question = questionService.getOne(
                new LambdaQueryWrapper<Question>()
                        .eq(Question::getAppId, appId)
        );

        List<ScoringResult> scoringResultList = scoringResultService.list(
                new LambdaQueryWrapper<ScoringResult>()
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );

        // 2. 统计用户的总得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        //校验用户答案数量和题目数量一致
        ThrowUtils.throwIf(choices.size() != questionContent.size(), ErrorCode.PARAMS_ERROR, "题目和用户答案数量不一致！");
        // 遍历题目列表
        for (int i = 0; i < questionContent.size(); i++) {
            Map<String, Integer> resultMap = questionContent.get(i).getOptions()
                    .stream()
                    .collect(
                            Collectors.toMap(QuestionContentDTO.Option::getKey, QuestionContentDTO.Option::getScore)
                    );
            Integer score = Optional.ofNullable(resultMap.get(choices.get(i))).orElse(0);
            totalScore += score;
        }

        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                break;
            }
        }
        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = buildUserAnswer(choices, app, maxScoringResult);
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }

    /**
     * 构建用户答案对象
     *
     * @param choices          用户答案
     * @param app              应用信息
     * @param maxScoringResult 最终得分结果
     * @return 用户答案对象
     */
    static UserAnswer buildUserAnswer(List<String> choices, App app, ScoringResult maxScoringResult) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
