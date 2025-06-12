import router from "@/router";
import { useLoginUserStore } from "@/store/userStore";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

//进入页面前,进行路由拦截
router.beforeEach(async (to, from, next) => {
  // 获取用户登录状态的 store 实例
  const loginUserStore = useLoginUserStore();
  let loginUser = loginUserStore.loginUser;

  // 如果没有登录用户信息或者用户角色为空，则尝试获取用户信息
  if (!loginUser || !loginUser.userRole) {
    try {
      // 等待获取登录用户信息
      await loginUserStore.fetchLoginUser();
      // 更新本地变量 loginUser
      loginUser = loginUserStore.loginUser;
    } catch (error) {
      // 捕获错误并提示用户跳转到登录页
      console.error("获取登录用户信息失败:", error);
      next(`/user/login?redirect=${to.fullPath}`);
      return;
    }
  }

  // 获取目标路由所需的访问权限，默认为不需要登录
  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;

  // 如果当前页面需要登录权限
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    // 如果用户未登录，则跳转到登录页面
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
    ) {
      next(`/user/login?redirect=${to.fullPath}`);
      return;
    }

    // 如果用户已登录但权限不足，则跳转到无权限页面
    if (!checkAccess(loginUser, needAccess)) {
      next(`/noAuth?redirect=${to.fullPath}`);
      return;
    }
  }

  // 所有检查通过后，允许进入目标页面
  next();
});
