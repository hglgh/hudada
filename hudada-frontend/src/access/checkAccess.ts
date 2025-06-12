import ACCESS_ENUM from "@/access/accessEnum";

/**
 * 检查用户是否有足够的权限访问特定资源或执行特定操作
 * 此函数通过比较登录用户的权限级别与所需权限级别来决定是否允许访问
 *
 * @param loginUser 登录用户的信息，包含用户的权限级别
 * @param needAccess 所需的权限级别，默认为未登录状态
 * @returns 如果用户具有足够的权限则返回true，否则返回false
 */
const checkAccess = (
  loginUser: API.LoginUserVO,
  needAccess = ACCESS_ENUM.NOT_LOGIN
) => {
  // 获取登录用户的权限级别，如果未登录则默认为未登录状态
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;

  // 如果所需权限为未登录状态，则允许访问
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }

  // 如果所需权限为普通用户权限，只需确保用户已登录即可
  if (needAccess === ACCESS_ENUM.USER) {
    return loginUserAccess !== ACCESS_ENUM.NOT_LOGIN;
  }

  // 如果所需权限为管理员权限，只有当用户权限为管理员时才允许访问
  if (needAccess === ACCESS_ENUM.ADMIN) {
    return loginUserAccess === ACCESS_ENUM.ADMIN;
  }

  // 默认情况下拒绝访问
  return false;
};
export default checkAccess;
