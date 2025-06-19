import { createApp } from "vue";
import ArcoVue from "@arco-design/web-vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router from "./router";
import "@arco-design/web-vue/dist/arco.css";
import "./access/index";
import ArcoVueIcon from "@arco-design/web-vue/es/icon";

const app = createApp(App);
const pinia = createPinia();

app.use(router);
app.use(ArcoVue);
app.use(pinia);
app.use(ArcoVueIcon);
app.mount("#app");
