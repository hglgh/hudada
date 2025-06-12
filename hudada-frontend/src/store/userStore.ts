import { defineStore } from "pinia";
import { ref } from "vue";
import { getLoginUserUsingGet } from "@/api/userController";
import ACCESS_ENUM from "@/access/accessEnum";

/**
 * 登录用户信息全局状态
 */
export const useLoginUserStore = defineStore("loginUser", () => {
  //  定义状态
  const loginUser = ref<API.LoginUserVO>({
    userName: "未登录",
  });

  //  定义方法,  获取登录用户信息
  async function fetchLoginUser() {
    const response = await getLoginUserUsingGet();
    if (response.data.code === 0 && response.data.data) {
      loginUser.value = response.data.data;
    } else {
      loginUser.value = { userRole: ACCESS_ENUM.NOT_LOGIN };
    }
    /*    else {
          setTimeout(() => {
            loginUser.value = {
              userName: "测试用户",
            };
          }, 3000);
        }*/
  }

  //  定义方法,  设置登录用户信息
  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  return { loginUser, fetchLoginUser, setLoginUser };
});
