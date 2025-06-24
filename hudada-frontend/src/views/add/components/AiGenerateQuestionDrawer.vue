<template>
  <a-button style="margin-bottom: 16px" type="outline" @click="handleClick">
    AI 生成题目
  </a-button>
  <a-drawer
    :width="600"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title> AI 生成题目</template>
    <div>
      <a-form
        :model="form"
        label-align="left"
        auto-label-width
        @submit="handleSubmit"
      >
        <a-form-item label="应用Id"> {{ props.appId }}</a-form-item>
        <a-form-item field="questionNumber" label="题目数量">
          <a-input-number
            v-model="form.questionNumber"
            min="0"
            max="20"
            placeholder="请输入题目数量"
          />
        </a-form-item>
        <a-form-item field="optionNumber" label="选项数量">
          <a-input-number
            v-model="form.optionNumber"
            min="0"
            max="6"
            placeholder="请输入选项数量"
          />
        </a-form-item>
        <a-form-item>
          <a-space size="large">
            <a-button
              :loading="loading"
              :disabled="sseLoading"
              type="primary"
              html-type="submit"
              style="width: 120px"
            >
              {{ loading ? "生成中" : "一键生成" }}
            </a-button>
            <a-button
              :disabled="loading"
              :loading="sseLoading"
              style="width: 120px"
              @click="handleSseSubmit"
            >
              {{ loading ? "生成中" : "实时生成" }}
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { defineProps, reactive, ref, withDefaults } from "vue";
import API from "@/api";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";

interface Props {
  appId: string;
  onSuccess?: (result: API.QuestionContentDTO[]) => void;
  onSseSuccess?: (result: API.QuestionContentDTO[]) => void;
  onSseStart?: (event: EventSource) => void;
  onSseClose?: (event: EventSource) => void;
}

const props = withDefaults(defineProps<Props>(), {});

const form = reactive<API.AiGenerateQuestionRequest>({
  questionNumber: 10,
  optionNumber: 4,
});
const visible = ref(false);

const loading = ref(false);
const sseLoading = ref(false);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};

const handleSubmit = async () => {
  loading.value = true;
  if (!props.appId) {
    return;
  }
  const response = await aiGenerateQuestionUsingPost({
    appId: props.appId as any,
    ...form,
  });
  if (response.data.code === 0 && response.data.data) {
    if (props.onSuccess) {
      props.onSuccess(response.data.data);
    } else {
      message.success("生成成功");
    }
  } else {
    message.error("生成失败," + response.data.message);
  }
  loading.value = false;
  handleCancel();
};

/**
 * 实时生成(使用SSE流式返回数据)
 */
const handleSseSubmit = async () => {
  sseLoading.value = true;
  if (!props.appId) {
    return;
  }
  //创建SSE请求
  const eventSource = new EventSource(
    // 创建 EventSource 对象,todo 需要手动填写完整的后端接口地址
    `http://localhost:8101/api/question/ai_generate/sse?appId=${props.appId}&questionNumber=${form.questionNumber}&optionNumber=${form.optionNumber}`
  );
  //接受消息
  eventSource.onmessage = (event) => {
    console.log(event.data);
    if (event.data) {
      props.onSseSuccess?.(JSON.parse(event.data));
    }
  };
  //报错或连接关闭时执行
  eventSource.onerror = (error) => {
    if (error.eventPhase === EventSource.CLOSED) {
      console.log("正常关闭连接");
      eventSource.close();
      props.onSseClose?.(eventSource);
    }
  };
  //连接成功时执行
  eventSource.onopen = (event) => {
    console.log("建立连接");
    props.onSseStart?.(eventSource);
    handleCancel();
  };
  sseLoading.value = false;
};
</script>
