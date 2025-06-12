<template>
  <div id="basicLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <GobalHeader />
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

<script setup lang="ts">
import GobalHeader from "@/components/GobalHeader.vue";
</script>

<style scoped>
#basicLayout {
}

#basicLayout .header {
  margin-bottom: 16px;
  box-shadow: #eee 1px 1px 5px;
}

.content {
  margin: 28px;
  padding: 20px;
  background: linear-gradient(to right, #fefefe, #ffff);
}

.footer {
  background: #efefef;
  padding: 16px;
  text-align: center;
}
</style>
