<template>
  <div id="doAnswerPage">
    <a-card style="width: 800px">
      <h1>{{ app?.appName }}</h1>
      <p>{{ app?.appDesc }}</p>
      <h2 style="margin: 32px">
        {{ currentQuestionNumber }}.{{ currentQuestion?.title }}
      </h2>
      <div>
        <a-radio-group
          v-model="currentAnswer"
          direction="vertical"
          :options="questionOptions"
          @change="onRadioChange"
          size="large"
        />
      </div>
      <div style="margin-top: 24px">
        <a-space size="large">
          <a-button
            type="primary"
            circle
            v-if="currentQuestionNumber < questionList.length"
            :disabled="!currentAnswer"
            @click="currentQuestionNumber += 1"
          >
            下一题
          </a-button>
          <a-button
            type="primary"
            v-if="currentQuestionNumber === questionList.length"
            circle
            :disabled="!currentAnswer"
            :loading="loading"
            @click="handleSubmit"
          >
            {{ loading ? "分析中" : "查看结果" }}
          </a-button>
          <a-button
            v-if="currentQuestionNumber > 1"
            circle
            @click="currentQuestionNumber -= 1"
          >
            上一题
          </a-button>
        </a-space>
      </div>
      {{ JSON.stringify(answerList) }}
    </a-card>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  defineProps,
  onMounted,
  reactive,
  ref,
  watchEffect,
  withDefaults,
} from "vue";
import API from "@/api";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import { getAppVoByIdUsingGet } from "@/api/appController";
import {
  addUserAnswerUsingPost,
  generateUserAnswerIdUsingGet,
} from "@/api/userAnswerController";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

const loading = ref(false);
const router = useRouter();

const questionList = ref<API.QuestionContentDTO[]>([]);

const app = ref<API.AppVO>();

// 当前用户答题的唯一id，用于实现幂等性，避免重复提交
const id = ref<number>();

//当前题目序号（从 1 开始）
const currentQuestionNumber = ref<number>(1);

//当前题目
const currentQuestion = ref<API.QuestionContentDTO>();

const questionOptions = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value?.options.map((option) => {
        return {
          label: `${option.key}.${option.value}`,
          value: option.key,
        };
      })
    : [];
});

// 当前答案
const currentAnswer = ref<string>();
// 回答列表
const answerList = reactive<string[]>([]);

const onRadioChange = (value: string) => {
  currentAnswer.value = value;
  // 记录回答
  answerList[currentQuestionNumber.value - 1] = value;
};

/**
 * 生成用户答案id
 */
const generateId = async () => {
  const res = await generateUserAnswerIdUsingGet();
  if (res.data.code === 0 && res.data.data) {
    id.value = res.data.data;
  } else {
    message.error("获取唯一 id 失败，" + res.data.message);
  }
};

/**
 * 页面加载时触发
 */
onMounted(() => {
  generateId();
});

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  const appResponse = await getAppVoByIdUsingGet({ id: props.appId as any });
  if (appResponse.data.code === 0 && appResponse.data.data) {
    app.value = appResponse.data.data;
  } else {
    message.error("获取应用信息失败" + appResponse.data.message);
  }
  await loadQuestionData();
};

/**
 * 加载题目数据
 */
const loadQuestionData = async () => {
  if (!props.appId) {
    message.error("请选择应用");
    return;
  }
  const response = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (response.data.code === 0 && response.data.data?.records) {
    questionList.value = response.data.data.records[0].questionContent ?? [];
  } else {
    Message.error("获取题目失败," + response.data.message);
  }
};

/**
 * 监听变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});

/**
 * 监听当前题目序号，改变时触发题目的加载
 */
watchEffect(() => {
  currentQuestion.value = questionList.value[currentQuestionNumber.value - 1];
  currentAnswer.value = answerList[currentQuestionNumber.value - 1];
});

/**
 * 提交表单
 */
const handleSubmit = async () => {
  loading.value = true;
  if (!props.appId || !answerList) {
    message.error("请填写完整");
    return;
  }
  const res = await addUserAnswerUsingPost({
    id: id.value,
    appId: props.appId as any,
    choices: answerList,
  });

  if (res.data.code === 0 && res.data.data) {
    router.push(`/answer/result/${res.data.data}`);
  } else {
    message.error("提交答案失败，" + res.data.message);
  }
  loading.value = false;
};
</script>

<style scoped>
#doAnswerPage {
}
</style>
