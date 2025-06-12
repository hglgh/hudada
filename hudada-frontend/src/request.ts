import axios from "axios";

const myAxios = axios.create({
  baseURL: "http://localhost:8101",
  timeout: 10000,
  //允许携带cookie
  withCredentials: true,
});

/**
 * 全局请求拦截器
 */
myAxios.interceptors.request.use(
  function (config) {
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

/**
 * 全局响应拦截器
 */
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response;
    if (data.code === 40100) {
      //不是获取用户信息的请求，且用户目前并不在登录页面，则跳转到登录页面
      if (
        !response.request.responseURL.includes("user/get/login") &&
        !window.location.pathname.includes("/user/login")
      ) {
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    }
    return response;
  },
  function (error) {
    return Promise.reject(error);
  }
);

export default myAxios;
