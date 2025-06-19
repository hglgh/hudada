<template>
  <div id="addScoringResultPage">
    <h2 style="margin-bottom: 32px">创建评分结果</h2>
    <a-form
      :ref="formRef"
      :model="form"
      :style="{ width: '480px' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item field="appId" label="应用Id">
        {{ props.appId }}
      </a-form-item>
      <a-form-item v-if="updateId" field="appId" label="修改评分Id">
        {{ updateId }}
      </a-form-item>
      <a-form-item
        field="resultName"
        label="结果名称"
        :rules="[{ required: true, message: '结果名称不能为空' }]"
      >
        <a-input v-model="form.resultName" placeholder="请输入结果名称" />
      </a-form-item>
      <a-form-item
        field="resultDesc"
        label="结果描述"
        :rules="[{ required: true, message: '结果描述不能为空' }]"
      >
        <a-input v-model="form.resultDesc" placeholder="请输入结果描述" />
      </a-form-item>
      <a-form-item
        field="resultPicture"
        label="结果图标"
        :rules="[{ required: true, message: '结果图标不能为空' }]"
      >
        <a-input v-model="form.resultPicture" placeholder="请输入结果图标URL" />
      </a-form-item>
      <a-form-item field="resultProp" label="应用类型">
        <a-input-tag
          v-model="form.resultProp"
          placeholder="请输入结果集"
          allow-clear
        />
      </a-form-item>
      <a-form-item field="resultScoreRange" label="结果得分范围">
        <a-input-number
          placeholder="请输入结果得分范围"
          v-model="form.resultScoreRange"
          :min="0"
          :max="10"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
  <div>
    <h2 style="margin-bottom: 32px">管理评分结果</h2>
    <ScoringResultTable ref="tableRef" :appId="appId" :do-update="doUpdate" />
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, withDefaults } from "vue";
import API from "@/api";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import {
  addScoringResultUsingPost,
  editScoringResultUsingPost,
} from "@/api/scoringResultController";
import ScoringResultTable from "@/views/add/components/ScoringResultTable.vue";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const tableRef = ref();

const form = ref<API.ScoringResultAddRequest>({
  resultDesc: "",
  resultName: "",
  resultPicture: "",
  resultProp: [],
});

const formRef = ref(); // 新增
const router = useRouter();
/**
 * 提交表单
 */
const handleSubmit = async ({ values, errors }: any) => {
  if (errors) {
    Message.error("请检查表单内容");
    return;
  }
  if (!props.appId) {
    Message.error("请选择应用");
    return;
  }
  let response: any;
  //判断是否是更新
  if (updateId.value) {
    response = await editScoringResultUsingPost({
      ...form.value,
      id: updateId.value,
    });
  } else {
    //新增
    response = await addScoringResultUsingPost({
      appId: props.appId as any,
      ...form.value,
    });
  }
  if (response.data.code === 0) {
    Message.success("操作成功");
  } else {
    Message.error("操作失败," + response.data.message);
  }
  if (tableRef.value) {
    updateId.value = undefined;
    await tableRef.value.loadData();
  }
};

const updateId = ref<any>();
/**
 * 更新表单
 * @param scoringResult
 */
const doUpdate = async (scoringResult: API.ScoringResultVO) => {
  updateId.value = scoringResult.id;
  form.value = scoringResult;
};
</script>

<style scoped>
#addScoringResultPage {
}
</style>
