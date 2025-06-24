package com.hgl.hudada.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hgl.hudada.common.ErrorCode;
import com.hgl.hudada.constant.CommonConstant;
import com.hgl.hudada.exception.ThrowUtils;
import com.hgl.hudada.manager.ZhiPuAiManager;
import com.hgl.hudada.mapper.QuestionMapper;
import com.hgl.hudada.model.dto.question.AiGenerateQuestionRequest;
import com.hgl.hudada.model.dto.question.QuestionContentDTO;
import com.hgl.hudada.model.dto.question.QuestionQueryRequest;
import com.hgl.hudada.model.entity.App;
import com.hgl.hudada.model.entity.Question;
import com.hgl.hudada.model.entity.User;
import com.hgl.hudada.model.enums.AppTypeEnum;
import com.hgl.hudada.model.vo.QuestionVO;
import com.hgl.hudada.model.vo.UserVO;
import com.hgl.hudada.service.AppService;
import com.hgl.hudada.service.QuestionService;
import com.hgl.hudada.service.UserService;
import com.hgl.hudada.utils.SqlUtils;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *
 * @author 请别把我整破防
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private static final String GENERATE_QUESTION_SYSTEM_PROMPT = """
            你是一位严谨的出题专家，我会给你如下信息：
            ```
            应用名称，
            【【【应用描述】】】，
            应用类别，
            要生成的题目数，
            每个题目的选项数
            ```
            请你根据上述信息，按照以下步骤来出题：
            1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复
            2. 严格按照下面的 json 格式输出题目和选项
            ```
            [{"options":[{"value":"选项内容","key":"A","result":"P","score":0},{"value":"","key":"B","result":"P","score":0}],"title":"题目标题"}]
            ```
            title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容, result 是选项结果(选型结果由你生成的题目为主自行判断),score 是选项得分(选项得分范围在0~10)
            3.如果应用类型是测评类,则需要在json中展示result属性,不需要在json中展示score属性;如果应用类型是得分类,则需要在json中展示score属性,不需要在json中展示result属性
            4. 检查题目是否包含序号，若包含序号则去除序号
            5. 返回的题目列表格式必须为 JSON 数组
            
            """;

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Resource
    private ZhiPuAiManager zhiPuAiManager;

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        //从对象中取值
        String questionContent = question.getQuestionContent();
        Long appId = question.getAppId();
        // 创建数据时，参数不能为空
        if (add) {
            //补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(questionContent), ErrorCode.PARAMS_ERROR, "题目内容不能为空");
            ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId非法");
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (appId != null) {
            App app = appService.getById(appId);
            ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "app不存在");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        //从对象中取值
        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String questionContent = questionQueryRequest.getQuestionContent();
        Long appId = questionQueryRequest.getAppId();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        //补充需要的查询条件
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(questionContent), "questionContent", questionContent);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 对象转封装类
        QuestionVO questionVO = QuestionVO.objToVo(question);

        //可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);
        // endregion

        return questionVO;
    }

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(QuestionVO::objToVo).collect(Collectors.toList());

        //可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionVOList.forEach(questionVO -> {
            Long userId = questionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public List<QuestionContentDTO> aiGenerateQuestion(AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        String userPrompt = verifyAndEncapsulateParams(aiGenerateQuestionRequest);
        String result = zhiPuAiManager.doSyncStableRequest(GENERATE_QUESTION_SYSTEM_PROMPT, userPrompt);
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");
        List<QuestionContentDTO> questionContentDTOList = new ArrayList<>();
        if (start != -1 && end != -1) {
            String json = result.substring(start, end + 1);
            questionContentDTOList = JSONUtil.toList(json, QuestionContentDTO.class);
        }
        return questionContentDTOList;
    }

    @Override
    public SseEmitter aiGenerateQuestionSse(AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        String userPrompt = verifyAndEncapsulateParams(aiGenerateQuestionRequest);
        //建立SSE对象, 用于返回数据,0 表示不设置超时时间, 表示一直保持连接 这里我选择默认60秒超时
        SseEmitter sseEmitter = new SseEmitter(60_000L);
        Flowable<ModelData> modelDataFlowable = zhiPuAiManager.doStreamRequest(GENERATE_QUESTION_SYSTEM_PROMPT, userPrompt, null);
        //左括号计数器，除了默认值外，当计数器回归为0后，表示左括号等于右括号数量，则表示一个完整的json对象结束，可以进行解析。
        AtomicInteger count = new AtomicInteger(0);
        // 拼接完整题目，存储json对象
        StringBuilder stringBuilder = new StringBuilder();
        modelDataFlowable.observeOn(Schedulers.io())
                .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                .map(content -> content.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(content -> {
                    List<Character> characterList = new ArrayList<>();
                    for (char c : content.toCharArray()) {
                        characterList.add(c);
                    }
                    return Flowable.fromIterable(characterList);
                })
                .doOnNext(c -> {
                    //如果是左括号，则计数器加1
                    if (c == '{') {
                        count.addAndGet(1);
                    }
                    //如果计数器大于0，则将字符添加到stringBuilder中
                    if (count.get() > 0) {
                        stringBuilder.append(c);
                    }
                    //如果是右括号，则计数器减1
                    if (c == '}') {
                        count.addAndGet(-1);
                        if (count.get() == 0) {
                            //拼接并通过SSE发送
                            sseEmitter.send(JSONUtil.toJsonStr(stringBuilder.toString()));
                            //重置stringBuilder
                            stringBuilder.setLength(0);
                        }
                    }
                })
                .doOnError(e -> log.error("aiGenerateQuestionSse error", e))
                .doOnComplete(sseEmitter::complete).subscribe();
        return sseEmitter;
    }

    private String getGenerateQuestionUserPrompt(App app, int questionNumber, int optionNumber) {
        return String.format("""
                %s，
                【【【%s】】】，
                %s，
                %s，
                %s
                """, app.getAppName(), app.getAppDesc(), Objects.requireNonNull(AppTypeEnum.getEnumByValue(app.getAppType())).getText(), questionNumber, optionNumber);
    }

    /**
     * 参数校验和封装参数
     *
     * @param aiGenerateQuestionRequest
     * @return
     */
    @NotNull
    private String verifyAndEncapsulateParams(AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "app不存在");
        //封装用户Prompt
        return getGenerateQuestionUserPrompt(app, questionNumber, optionNumber);
    }
}
