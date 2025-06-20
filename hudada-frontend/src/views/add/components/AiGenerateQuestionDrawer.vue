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
          <a-button
            :loading="loading"
            type="primary"
            html-type="submit"
            style="width: 120px"
          >
            {{ loading ? "生成中" : "一键生成" }}
          </a-button>
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
}

const props = withDefaults(defineProps<Props>(), {});

const form = reactive<API.AiGenerateQuestionRequest>({
  questionNumber: 10,
  optionNumber: 4,
});
const visible = ref(false);

const loading = ref(false);

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
</script>
