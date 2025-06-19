<template>
  <div id="answerResultPage">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wrapper">
          <h2>{{ data.resultName }}</h2>
          <p>结果描述:{{ data.resultDesc }}</p>
          <p>结果id:{{ data.id }}</p>
          <p v-if="APP_TYPE_ENUM.SCORE === data.appType">
            结果得分: {{ data.resultScore }}
          </p>
          <p>我的答案: {{ data.choices }}</p>
          <p>应用Id: {{ data.appId }}</p>
          <p>应用类型: {{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略: {{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              答题人:
              <div
                :style="{
                  display: 'flex',
                  alignItems: 'center',
                  color: '#1D2129',
                }"
              >
                <a-avatar
                  :image-url="data.user?.userAvatar"
                  :size="24"
                  :style="{ marginRight: '8px' }"
                >
                  A
                </a-avatar>
                <a-typography-text>
                  {{ data.user?.userName ?? "无名" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>
          <p>
            创建时间: {{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </p>
          <a-space size="medium">
            <a-button type="primary" :href="`/answer/do/${data.appId}`">
              重新答题
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <a-image :src="data.userAnswerIcon" width="100%" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, watchEffect, withDefaults } from "vue";
import API from "@/api";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import message from "@arco-design/web-vue/es/message";
import {
  APP_SCORING_STRATEGY_MAP,
  APP_TYPE_ENUM,
  APP_TYPE_MAP,
} from "@/constant/app";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "0",
});

const data = ref<API.UserAnswerVO>({});

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const response = await getUserAnswerVoByIdUsingGet({ id: props.id as any });
  if (response.data.code === 0 && response.data.data) {
    data.value = response.data.data;
  } else {
    message.error("获取应用信息失败" + response.data.message);
  }
};

/**
 * 监听变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});
</script>

<style scoped>
#answerResultPage {
  margin: 16px;
  width: 1300px;
}

#answerResultPage .content-wrapper > * {
  margin-bottom: 24px;
}
</style>
