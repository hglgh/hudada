<template>
  <div id="userLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <img
            class="logo"
            src="../assets/logo.png"
            alt="logo"
            style="margin-right: 16px"
          />
          <div>扈答答 AI 答题应用平台</div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a
          href="https://github.com/hglgh"
          target="_blank"
          style="text-decoration: none; color: black"
        >
          程序员 请别把我整破防
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
#userLayout {
  text-align: center;
  background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png")
    0 0 / 100% 100%;
}

#userLayout .logo {
  height: 48px;
  width: 48px;
}

#userLayout .header {
  margin-top: 32px;
  font-size: 21px;
}

.content {
  margin: 16px;
  padding: 20px;
}

.footer {
  background: #efefef;
  padding: 16px;
  text-align: center;
}
</style>
