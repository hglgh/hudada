//审核状态枚举
export const REVIEW_STATUS_ENUM = {
  //待审核
  REVIEWING: 0,
  //通过
  PASS: 1,
  //不通过
  REJECT: 2,
};

//审核状态映射
export const REVIEW_STATUS_MAP = {
  //待审核
  0: "待审核",
  //通过
  1: "通过",
  //不通过
  2: "拒绝",
};

//应用类型枚举
export const APP_TYPE_ENUM = {
  //得分类
  SCORE: 0,
  //测试类
  TEST: 1,
};

//应用类型映射
export const APP_TYPE_MAP = {
  //得分类
  0: "得分类",
  //测试类
  1: "测试类",
};

//应用评分策略枚举
export const APP_SCORING_STRATEGY_ENUM = {
  //自定义
  CUSTOM: 0,
  //AI
  AI: 1,
};

//应用评分策略映射
export const APP_SCORING_STRATEGY_MAP = {
  //自定义
  0: "自定义",
  //AI
  1: "AI",
};
