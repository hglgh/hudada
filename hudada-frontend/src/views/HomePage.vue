<template>
  <div id="homePage">
    <div class="searchBar">
      <a-input-search
        :style="{ width: '320px' }"
        placeholder="快速发现答题应用"
        button-text="搜索"
        search-button
        size="large"
        @search="
          (value : string) => {
            searchParams.searchText = value;
          }
        "
      />
    </div>
    <a-list
      :grid-props="{ gutter: [20, 20], sm: 24, md: 12, lg: 8, xl: 6 }"
      :bordered="false"
      :data="dataList"
      :pagination-props="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
    >
      <template #item="{ item }">
        <AppCard :app="item" />
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import AppCard from "@/components/AppCard.vue";
import API from "@/api";
import { Message } from "@arco-design/web-vue";
import { listAppVoByPageUsingPost } from "@/api/appController";

const dataList = ref<API.AppVO[]>([]);
const total = ref<number>(0);

const searchParams = reactive<API.AppQueryRequest>({
  current: 1,
  pageSize: 12,
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
  const response = await listAppVoByPageUsingPost(searchParams);
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
</script>

<style scoped>
#homePage {
}

.searchBar {
  margin-bottom: 28px;
  text-align: center;
}
</style>
