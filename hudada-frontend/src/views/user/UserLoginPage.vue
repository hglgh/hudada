<template>
  <div id="userLoginPage">
    <h2 style="margin-bottom: 32px">用户登录</h2>
    <a-form
      :model="form"
      :style="{ width: '480px', margin: '0 auto' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item
        field="userAccount"
        label="账号"
        :rules="[{ required: true, message: '请输入账号' }]"
      >
        <a-input v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        field="userPassword"
        tooltip="密码长度不能小于8位"
        label="密码"
        :rules="[
          { required: true, message: '请输入密码' },
          { minLength: 8, message: '密码必须大于等于8位' },
        ]"
      >
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>
      <a-form-item>
        <div
          style="
            display: flex;
            width: 100%;
            align-items: center;
            justify-content: space-between;
          "
        >
          <a-button type="primary" html-type="submit" style="width: 120px">
            登录
          </a-button>
          <a-link href="/user/register"> 尚未账号？去注册!!! </a-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import API from "@/api";
import { userLoginUsingPost } from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";

const form = reactive<API.UserLoginRequest>({});
const loginUserStore = useLoginUserStore();
const router = useRouter();
/**
 * 提交表单
 */
const handleSubmit = async () => {
  const response = await userLoginUsingPost(form);
  if (response.data.code === 0) {
    await loginUserStore.fetchLoginUser();
    Message.success("登录成功");
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    Message.error("登录失败," + response.data.message);
  }
};
</script>

<style scoped>
#userLoginPage {
}
</style>
