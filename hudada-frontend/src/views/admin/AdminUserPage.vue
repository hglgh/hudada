<template>
  <a-form
    :model="formSearchParams"
    :style="{ marginBottom: '20px' }"
    label-align="left"
    layout="inline"
    auto-label-width
    @submit="doSearch"
    @reset="doReSet"
  >
    <a-form-item field="userName" label="用户名">
      <a-input
        v-model="formSearchParams.userName"
        placeholder="请输入用户名"
        allow-clear
      />
    </a-form-item>
    <a-form-item field="userProfile" label="用户简介">
      <a-input
        v-model="formSearchParams.userProfile"
        placeholder="请输入用户简介"
        allow-clear
      />
    </a-form-item>
    <a-form-item>
      <a-space>
        <a-button type="primary" html-type="submit" style="width: 100px">
          搜索
        </a-button>
        <a-button type="secondary" html-type="reset" style="width: 100px">
          重置
        </a-button>
      </a-space>
    </a-form-item>
  </a-form>
  <a-table
    :columns="columns"
    :data="dataList"
    :pagination="{
      showTotal: true,
      pageSize: searchParams.pageSize,
      current: searchParams.current,
      total,
    }"
    @page-change="onPageChange"
  >
    <template #userAvatar="{ record }">
      <a-image :src="record.userAvatar" width="120" />
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #action="{ record }">
      <a-popconfirm content="确认删除吗?" @ok="doDelete(record)">
        <a-button status="danger">删除</a-button>
      </a-popconfirm>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import API from "@/api";
import {
  deleteUserUsingPost,
  listUserByPageUsingPost,
} from "@/api/userController";
import { Message } from "@arco-design/web-vue";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";

const columns = [
  {
    title: "ID",
    dataIndex: "id",
  },
  {
    title: "用户名",
    dataIndex: "userName",
  },
  {
    title: "账号",
    dataIndex: "userAccount",
  },
  {
    title: "头像",
    dataIndex: "userAvatar",
    slotName: "userAvatar",
  },
  {
    title: "权限",
    dataIndex: "userRole",
  },
  {
    title: "简介",
    dataIndex: "userProfile",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime",
  },
  {
    title: "操作",
    slotName: "action",
  },
];

const dataList = ref<API.User[]>([]);
const total = ref<number>(0);

const formSearchParams = reactive<API.UserQueryRequest>({});
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
});

/**
 * 页码切换
 * @param page
 */
const onPageChange = (page: number) => {
  searchParams.current = page;
};

/**
 * 加载数据
 */
const loadData = async () => {
  const response = await listUserByPageUsingPost(searchParams);
  if (response.data.code === 0) {
    dataList.value = response.data.data?.records || [];
    total.value = response.data.data?.total || 0;
  } else {
    Message.error("获取用户列表失败" + response.data.message);
  }
};
/**
 * 监听searchParams 数据,当搜索参数改变时加载
 */
watchEffect(() => {
  loadData();
});

/**
 * 删除用户
 * @param record
 */
const doDelete = async (record: API.User) => {
  if (!record.id) {
    Message.error("请选择要删除的用户");
    return;
  }
  const response = await deleteUserUsingPost({
    id: record.id,
  } as API.DeleteRequest);
  if (response.data.code === 0) {
    Message.success("删除成功");
    await loadData();
  } else {
    Message.error("删除失败" + response.data.message);
  }
};

const doSearch = () => {
  //  重置页码
  searchParams.current = 1;
  searchParams.pageSize = 10;
  searchParams.userName = formSearchParams.userName;
  searchParams.userProfile = formSearchParams.userProfile;
};
const doReSet = () => {
  formSearchParams.userName = "";
  formSearchParams.userProfile = "";
  doSearch();
};
</script>

<style scoped>
#adminUserPage {
}
</style>
