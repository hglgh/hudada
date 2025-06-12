<template>
  <div id="userRegisterPage">
    <h2 style="margin-bottom: 32px">用户注册</h2>
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
      <a-form-item
        field="checkPassword"
        tooltip="密码长度不能小于8位"
        label="确认密码"
        :rules="[
          { required: true, message: '请输入密码' },
          { minLength: 8, message: '密码必须大于等于8位' },
        ]"
      >
        <a-input-password
          v-model="form.checkPassword"
          placeholder="请确认密码"
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
            注册
          </a-button>
          <a-link href="/user/login" style="text-align: left">
            已有账号？去登录!!!
          </a-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import API from "@/api";
import { userRegisterUsingPost } from "@/api/userController";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";

const form = reactive<API.UserRegisterRequest>({});
const router = useRouter();
/**
 * 提交表单
 */
const handleSubmit = async () => {
  const response = await userRegisterUsingPost(form);
  if (response.data.code === 0) {
    Message.success("注册成功");
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    Message.error("注册失败," + response.data.message);
  }
};
</script>

<style scoped>
#userRegisterPage {
}
</style>
