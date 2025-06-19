<template>
  <div id="appDetailPage">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wrapper">
          <h2>{{ data.appName }}</h2>
          <p>{{ data.appDesc }}</p>
          <p>应用类型: {{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略: {{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              作者:
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
            <a-button type="primary" :href="`/answer/do/${props.id}`">
              开始答题
            </a-button>
            <a-button type="secondary"> 分享应用</a-button>
            <a-button v-if="isMy" :href="`/add/question/${id}`">
              设置题目
            </a-button>
            <a-button v-if="isMy" :href="`/add/scoring_result/${id}`">
              设置评分
            </a-button>
            <a-button v-if="isMy" :href="`/add/app/${id}`"> 修改应用</a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <a-image :src="data.appIcon" width="100%" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { withDefaults, defineProps, ref, watchEffect, computed } from "vue";
import API from "@/api";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "@/store/userStore";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "0",
});

const data = ref<API.AppVO>({});

const loginUserStore = useLoginUserStore();
const loginUserId = loginUserStore.loginUser?.id;
// 判断是否是当前用户
const isMy = computed(() => loginUserId && loginUserId === data.value.userId);

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const response = await getAppVoByIdUsingGet({ id: props.id as any });
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
#appDetailPage {
  margin: 16px;
  width: 1300px;
}

#appDetailPage .content-wrapper > * {
  margin-bottom: 24px;
}
</style>
