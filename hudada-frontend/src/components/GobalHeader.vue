<template>
  <a-row id="gobalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="titleBar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">扈答答</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visbleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <div v-if="loginUserStore.loginUser.id">
        {{ loginUserStore.loginUser.userName ?? "无名" }}
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import CheckAccess from "@/access/checkAccess";

const loginUserStore = useLoginUserStore();
const router = useRouter();
//当前选中的菜单项，默认选中首页
const selectKeys = ref(["/"]);
//当路由跳转完成之后，更新当前选中的菜单项
router.afterEach((to) => {
  selectKeys.value = [to.path];
});

//展示在菜单栏的菜单项
const visbleRoutes = computed(() => {
  return routes.filter((item) => {
    /*  if (item.meta?.hideInMenu) {
        return false;
      }
      //根据权限过滤菜单项
      if (!CheckAccess(loginUserStore.loginUser, item.meta?.access as string)) {
        return false;
      }
      return true;*/
    //上述逻辑可以合并为
    return item.meta?.hideInMenu
      ? false
      : CheckAccess(loginUserStore.loginUser, item.meta?.access as string);
  });
});

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};
</script>

<style scoped>
#gobalHeader {
}

#gobalHeader .titleBar {
  display: flex;
  align-items: center;
}

#gobalHeader .title {
  font-size: 20px;
  color: black;
  font-weight: 700;
}

#gobalHeader .logo {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}
</style>
