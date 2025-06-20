<template>
  <div id="addQuestionPage">
    <h2 style="margin-bottom: 32px">
      {{ oldQuestion?.id ? "编辑题目" : "添加题目" }}
    </h2>
    <a-form
      :model="formData"
      :ref="formRef"
      :style="{ width: '900px' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item field="appId" label="应用Id"> {{ props.appId }}</a-form-item>
      <a-form-item label="题目列表" :content-flex="false" :merge-props="false">
        <a-space size="medium">
          <a-button
            status="success"
            style="margin-bottom: 16px"
            @click="addQuestion(formData.questionList.length)"
          >
            底部添加题目
          </a-button>
          <AiGenerateQuestionDrawer
            :appId="appId"
            :onSuccess="onAiGenerateSuccess"
          />
        </a-space>

        <a-collapse
          v-for="(question, index) in formData.questionList"
          :key="index"
          :default-active-key="index"
          destroy-on-hide
        >
          <a-collapse-item
            :header="`题目 ${index + 1}: ${question.title || '未命名题目'}`"
            :key="index"
          >
            <!-- 题目内容 -->
            <a-form-item
              :field="`questionList[${index}].title`"
              label="题目标题"
              :rules="[{ required: true, message: '请输入题目标题' }]"
            >
              <a-input v-model="question.title" placeholder="请输入题目标题" />
            </a-form-item>
            <a-collapse
              v-for="(option, optionIndex) in question.options"
              :key="optionIndex"
              destroy-on-hide
            >
              <a-collapse-item
                :header="`选项 ${optionIndex + 1}: ${
                  option.value || '未命名选项'
                }`"
                :key="optionIndex"
              >
                <!-- 选项详细内容 -->
                <a-form-item label="选项 key">
                  <a-input v-model="option.key" placeholder="请输入选项 key" />
                </a-form-item>
                <a-form-item label="选项值">
                  <a-input v-model="option.value" placeholder="请输入选项值" />
                </a-form-item>
                <a-form-item
                  v-if="APP_TYPE_ENUM.TEST === app.appType"
                  label="选项结果"
                >
                  <a-input
                    v-model="option.result"
                    placeholder="请输入选项结果"
                  />
                </a-form-item>
                <a-form-item
                  v-if="APP_TYPE_ENUM.SCORE === app.appType"
                  label="选项得分"
                >
                  <a-input-number
                    v-model="option.score"
                    placeholder="请输入选项得分"
                    style="width: 100%"
                  />
                </a-form-item>
                <a-space>
                  <a-button
                    size="mini"
                    @click="addQuestionOption(question, optionIndex + 1)"
                    >插入选项
                  </a-button>
                  <a-popconfirm
                    content="确认删除该选项吗?"
                    @ok="deleteQuestionOption(question, optionIndex)"
                  >
                    <a-button size="mini" status="danger">删除</a-button>
                  </a-popconfirm>
                </a-space>
              </a-collapse-item>
            </a-collapse>
            <!-- 添加选项按钮 -->
            <a-button
              size="mini"
              type="primary"
              block
              @click="addQuestionOption(question, question.options.length)"
              style="margin-top: 8px"
            >
              添加选项
            </a-button>

            <div style="margin-top: 16px">
              <!-- 题目底部操作按钮 -->
              <a-space size="large">
                <a-button type="primary" @click="addQuestion(index + 1)">
                  插入题目
                </a-button>
                <a-popconfirm
                  content="确认删除该题目吗?"
                  @ok="deleteQuestion(index)"
                >
                  <a-button status="danger"> 删除</a-button>
                </a-popconfirm>
              </a-space>
            </div>
          </a-collapse-item>
        </a-collapse>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref, watchEffect, withDefaults } from "vue";
import API from "@/api";
import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { APP_TYPE_ENUM } from "@/constant/app";
import AiGenerateQuestionDrawer from "@/views/add/components/AiGenerateQuestionDrawer.vue";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

const router = useRouter();

// 使用 formData 包装 questionList 数组
const formData = ref({
  questionList: [] as API.QuestionContentDTO[],
});

// 老的题目对象
const oldQuestion = ref<API.QuestionVO>();

const app = ref<API.AppVO>();

onMounted(async () => {
  const response = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });
  if (response.data.code === 0 && response.data.data) {
    app.value = response.data.data;
  } else {
    message.error("获取应用信息失败" + response.data.message);
  }
});

/**
 * 添加题目
 */
const addQuestion = (index: number) => {
  formData.value.questionList.splice(index, 0, {
    title: "",
    options: [],
  });
};

/**
 * 删除题目
 */
const deleteQuestion = (index: number) => {
  formData.value.questionList.splice(index, 1);
};

/**
 * 添加选项
 */
const addQuestionOption = (question: API.QuestionContentDTO, index: number) => {
  question.options = question.options || [];
  question.options.splice(index, 0, {
    key: "",
    value: "",
    score: 0,
    result: "",
  });
};

/**
 * 删除选项
 */
const deleteQuestionOption = (
  question: API.QuestionContentDTO,
  index: number
) => {
  question.options = question.options || [];
  question.options.splice(index, 1);
};

/**
 * 加载数据
 */
const loadData = async () => {
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
    oldQuestion.value = response.data.data.records[0];
    if (oldQuestion.value) {
      formData.value.questionList = oldQuestion.value.questionContent ?? [];
    }
  } else {
    Message.error("获取数据失败," + response.data.message);
  }
};

/**
 * 获取旧数据
 */
watchEffect(() => {
  loadData();
});
/**
 * 提交表单
 */
const handleSubmit = async ({ values, errors }: any) => {
  if (errors) {
    Message.error("请检查表单内容");
    return;
  }
  if (!props.appId || !formData.value.questionList) {
    return;
  }
  //如果旧数据和新数据一致，则不提交
  if (
    JSON.stringify(oldQuestion.value?.questionContent) ===
    JSON.stringify(formData.value.questionList)
  ) {
    Message.warning("没有修改,无需提交");
    return;
  }
  let response;
  if (oldQuestion.value?.id) {
    response = await editQuestionUsingPost({
      id: oldQuestion.value?.id,
      questionContent: formData.value.questionList,
    });
  } else {
    response = await addQuestionUsingPost({
      appId: props.appId as any,
      questionContent: formData.value.questionList,
    });
  }
  if (response.data.code === 0) {
    message.success("创建成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.appId}`);
    }, 3000);
  } else {
    message.error("创建失败，" + response.data.message);
  }
};

const onAiGenerateSuccess = (result: API.QuestionContentDTO[]) => {
  formData.value.questionList = [...formData.value.questionList, ...result];
  message.success(`AI 生成题目成功，已新增 ${result.length} 道题目`);
};
</script>

<style scoped>
#addAppPage {
}
</style>
