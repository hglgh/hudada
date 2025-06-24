package com.hgl.hudada.manager;


import com.hgl.hudada.common.ErrorCode;
import com.hgl.hudada.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ZhiPuAiManager
 * @Author 请别把我整破防
 * @Description 调用智谱大模型, 获取结果
 * @Date 2025/6/20 8:34
 */
@Component
public class ZhiPuAiManager {

    // 较稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    @Resource
    private ClientV4 clientV4;

    /**
     * 同步调用（答案较稳定）
     *
     * @param systemPrompt 系统提示
     * @param userPrompt   用户提示
     * @return 模型返回结果
     */
    public String doSyncStableRequest(String systemPrompt, String userPrompt) {
        return doSyncRequest(systemPrompt, userPrompt, STABLE_TEMPERATURE);
    }

    /**
     * 同步调用（答案较随机）
     *
     * @param systemPrompt 系统提示
     * @param userPrompt   用户提示
     * @return 模型返回结果
     */
    public String doSyncUnstableRequest(String systemPrompt, String userPrompt) {
        return doSyncRequest(systemPrompt, userPrompt, UNSTABLE_TEMPERATURE);
    }

    /**
     * 请求大模型, 同步请求
     *
     * @param systemPrompt 系统Prompt
     * @param userPrompt   用户Prompt
     * @param temperature  模型生成的结果随机性
     * @return 模型生成的结果
     */
    public String doSyncRequest(String systemPrompt, String userPrompt, Float temperature) {
        return doRequest(systemPrompt, userPrompt, Boolean.FALSE, temperature);
    }

    /**
     * 请求大模型
     *
     * @param systemPrompt 系统Prompt
     * @param userPrompt   用户Prompt
     * @param stream       是否流式返回结果
     * @param temperature  模型生成的结果随机性
     * @return 模型生成的结果
     */
    public String doRequest(String systemPrompt, String userPrompt, Boolean stream, Float temperature) {
        List<ChatMessage> chatMessages = buildChatMessages(systemPrompt, userPrompt);
        return doRequest(chatMessages, stream, temperature);
    }

    /**
     * 流式请求(简化消息传递)
     *
     * @param systemPrompt 系统Prompt
     * @param userPrompt   用户Prompt
     * @return 模型生成的结果
     */
    public Flowable<ModelData> doStreamRequest(String systemPrompt, String userPrompt, Float temperature) {
        List<ChatMessage> chatMessages = buildChatMessages(systemPrompt, userPrompt);
        return doStreamRequest(chatMessages, temperature);
    }

    /**
     * 流式请求
     *
     * @param messages    要给大模型的Prompt，模型会根据这个Prompt生成结果（包括系统Prompt和用户Prompt）
     * @param temperature 模型生成的结果随机性
     * @return 模型生成的结果
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> messages, Float temperature) {

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getFlowable();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 请求大模型
     *
     * @param messages    要给大模型的Prompt，模型会根据这个Prompt生成结果（包括系统Prompt和用户Prompt）
     * @param stream      是否流式返回结果
     * @param temperature 模型生成的结果随机性
     * @return 模型生成的结果
     */
    public String doRequest(List<ChatMessage> messages, Boolean stream, Float temperature) {

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            ChatMessage result = invokeModelApiResp.getData().getChoices().get(0).getMessage();
            return result.getContent().toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 构建Prompt
     *
     * @param systemPrompt 系统Prompt
     * @param userPrompt   用户Prompt
     * @return 模型生成的结果
     */
    @NotNull
    private static List<ChatMessage> buildChatMessages(String systemPrompt, String userPrompt) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
        chatMessages.add(systemChatMessage);
        chatMessages.add(userChatMessage);
        return chatMessages;
    }
}
