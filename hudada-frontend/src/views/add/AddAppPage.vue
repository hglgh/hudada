<template>
  <div id="addAppPage">
    <h2 style="margin-bottom: 32px">创建应用</h2>
    <a-form
      :ref="formRef"
      :model="form"
      :style="{ width: '480px' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item
        field="appName"
        label="应用名称"
        :rules="[{ required: true, message: '应用名称不能为空' }]"
      >
        <a-input v-model="form.appName" placeholder="请输入应用名称" />
      </a-form-item>
      <a-form-item
        field="appDesc"
        label="应用描述"
        :rules="[{ required: true, message: '应用描述不能为空' }]"
      >
        <a-input v-model="form.appDesc" placeholder="请输入应用描述" />
      </a-form-item>
      <a-form-item
        field="appIcon"
        label="应用图标"
        :rules="[{ required: true, message: '应用图标不能为空' }]"
      >
        <a-input v-model="form.appIcon" placeholder="请输入应用图标URL" />
      </a-form-item>
      <!--      <a-form-item
        field="appIcon"
        label="应用图标"
        :rules="[{ required: true, message: '应用图标不能为空' }]"
      >
        <PictureUploader
          biz="app_icon"
          :value="form.appIcon"
          :on-change="(value) => (form.appIcon = value)"
        />
      </a-form-item>-->
      <a-form-item field="appType" label="应用类型">
        <a-select v-model="form.appType" :style="{ width: '320px' }">
          <a-option
            v-for="(value, key) of APP_TYPE_MAP"
            :key="key"
            :value="Number(key)"
            :label="value"
          />
        </a-select>
      </a-form-item>
      <a-form-item field="scoringStrategy" label="评分策略">
        <a-select v-model="form.scoringStrategy" :style="{ width: '320px' }">
          <a-option
            v-for="(value, key) of APP_SCORING_STRATEGY_MAP"
            :key="key"
            :value="Number(key)"
            :label="value"
          />
        </a-select>
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
import { defineProps, ref, watchEffect, withDefaults } from "vue";
import API from "@/api";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import {
  addAppUsingPost,
  editAppUsingPost,
  getAppVoByIdUsingGet,
} from "@/api/appController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import message from "@arco-design/web-vue/es/message";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const oldApp = ref<API.AppVO>({});
/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const response = await getAppVoByIdUsingGet({ id: props.id as any });
  if (response.data.code === 0 && response.data.data) {
    oldApp.value = response.data.data;
    form.value = response.data.data;
  } else {
    message.error("获取应用信息失败" + response.data.message);
  }
};
//获取旧数据
watchEffect(() => loadData());
const form = ref<API.AppAddRequest>({
  appType: 0,
  scoringStrategy: 0,
});
const formRef = ref(); // 新增
const router = useRouter();
/**
 * 提交表单
 */
const handleSubmit = async ({ values, errors }) => {
  if (errors) {
    console.error("表单错误:", errors);
    Message.error("请检查表单内容");
    return;
  }
  let response: any;
  //判断是否是更新
  if (props.id) {
    response = await editAppUsingPost({
      ...form.value,
      id: props.id as any,
    });
  } else {
    //新增
    response = await addAppUsingPost(form.value);
  }
  if (response.data.code === 0) {
    Message.success("操作成功,即将进入应用详情页面");
    setTimeout(() => {
      router.push({
        path: `/app/detail/${props.id ?? response.data.data}`,
        replace: true,
      });
    }, 3000);
  } else {
    Message.error("操作失败," + response.data.message);
  }
};
</script>

<style scoped>
#addAppPage {
}
</style>
