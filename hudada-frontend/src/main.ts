import { createApp } from "vue";
import ArcoVue from "@arco-design/web-vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router from "./router";
import "@arco-design/web-vue/dist/arco.css";
import "./access/index";

const app = createApp(App);
const pinia = createPinia();

app.use(router);
app.use(ArcoVue);
app.use(pinia);
app.mount("#app");
