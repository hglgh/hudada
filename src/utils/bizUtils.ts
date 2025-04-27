/**
 * 计算用户答案与题目评分结果的匹配度
 * @param {string[]} answerList - 用户提交的答案数组
 * @param {Object[]} questions - 题目列表
 * @param {Object[]} questionResults - 题目评分结果
 * @returns {Object} - 得分最高的题目评分结果
 */
export function getHighestScoreResult(answerList, questions, questionResults) {
  // 初始化一个对象来存储每个评分结果的得分
  const resultScores = {};

  // 遍历每个题目
  questions.forEach((question, index) => {
    // 获取用户对当前题目的答案
    const userAnswer = answerList[index];

    // 遍历当前题目的所有选项
    question.options.forEach(option => {
      // 如果选项的 key 与用户答案匹配
      if (option.key === userAnswer) {
        // 获取该选项对应的 result 属性
        const result = option.result;

        // 遍历每个评分结果
        questionResults.forEach(questionResult => {
          // 如果评分结果的 resultProp 包含该 result 属性
          if (questionResult.resultProp.includes(result)) {
            // 如果该评分结果还没有在 resultScores 中初始化，则初始化为 0
            if (!resultScores[questionResult.resultName]) {
              resultScores[questionResult.resultName] = 0;
            }
            // 为该评分结果加 1 分
            resultScores[questionResult.resultName]++;
          }
        });
      }
    });
  });

  // 初始化变量来存储最高得分和对应的评分结果
  let highestScore = 0;
  let highestScoreResult = questionResults[0];

  // 遍历 resultScores 对象，找到得分最高的评分结果
  for (const resultName in resultScores) {
    if (resultScores[resultName] > highestScore) {
      highestScore = resultScores[resultName];
      highestScoreResult = questionResults.find(result => result.resultName === resultName);
    }
  }

  // 返回得分最高的评分结果
  return highestScoreResult;
}

// 示例数据
// 用户提交的答案
const answerList = ["A", "B", "B", "B", "B"];

// 题目列表
const questions = [
  {
    "title": "你通常更喜欢",
    "options": [
      {
        "result": "I",
        "value": "独自工作",
        "key": "A"
      },
      {
        "result": "E",
        "value": "与他人合作",
        "key": "B"
      }
    ]
  },
  {
    "title": "你更倾向于",
    "options": [
      {
        "result": "S",
        "value": "实际操作",
        "key": "A"
      },
      {
        "result": "N",
        "value": "理论思考",
        "key": "B"
      }
    ]
  },
  {
    "title": "你如何做决定",
    "options": [
      {
        "result": "T",
        "value": "逻辑分析",
        "key": "A"
      },
      {
        "result": "F",
        "value": "情感考虑",
        "key": "B"
      }
    ]
  },
  {
    "title": "你更喜欢",
    "options": [
      {
        "result": "J",
        "value": "计划和组织",
        "key": "A"
      },
      {
        "result": "P",
        "value": "灵活应对",
        "key": "B"
      }
    ]
  },
  {
    "title": "你如何处理信息",
    "options": [
      {
        "result": "J",
        "value": "有序处理",
        "key": "A"
      },
      {
        "result": "P",
        "value": "随机处理",
        "key": "B"
      }
    ]
  }
];

// 题目评分结果
const questionResults = [
  {
    "resultProp": [
      "I",
      "S",
      "T",
      "J"
    ],
    "resultDesc": "忠诚可靠，被公认为务实，注重细节。",
    "resultPicture": "icon_url_istj",
    "resultName": "ISTJ（物流师）"
  },
  {
    "resultProp": [
      "E",
      "S",
      "T",
      "J"
    ],
    "resultDesc": "高效，组织能力强，喜欢团队合作。",
    "resultPicture": "icon_url_estj",
    "resultName": "ESTJ（监督者）"
  },
  {
    "resultProp": [
      "I",
      "N",
      "T",
      "J"
    ],
    "resultDesc": "独立思考，有远见，喜欢规划未来。",
    "resultPicture": "icon_url_intj",
    "resultName": "INTJ（科学家）"
  },
  {
    "resultProp": [
      "E",
      "N",
      "T",
      "J"
    ],
    "resultDesc": "自信，善于领导，喜欢创新。",
    "resultPicture": "icon_url_entj",
    "resultName": "ENTJ（企业家）"
  },
  {
    "resultProp": [
      "I",
      "S",
      "F",
      "P"
    ],
    "resultDesc": "温和，注重和谐，喜欢灵活处理。",
    "resultPicture": "icon_url_isfp",
    "resultName": "ISFP（艺术家）"
  },
  {
    "resultProp": [
      "E",
      "S",
      "F",
      "P"
    ],
    "resultDesc": "友好，乐于助人，喜欢社交。",
    "resultPicture": "icon_url_esfp",
    "resultName": "ESFP（表演者）"
  },
  {
    "resultProp": [
      "I",
      "N",
      "F",
      "P"
    ],
    "resultDesc": "富有同情心，有创造力，喜欢探索。",
    "resultPicture": "icon_url_infj",
    "resultName": "INFJ（先知）"
  },
  {
    "resultProp": [
      "E",
      "N",
      "F",
      "P"
    ],
    "resultDesc": "乐观，善于沟通，喜欢变化。",
    "resultPicture": "icon_url_enfp",
    "resultName": "ENFP（激励者）"
  },
  {
    "resultProp": [
      "I",
      "S",
      "T",
      "P"
    ],
    "resultDesc": "细心，实际，喜欢独立思考。",
    "resultPicture": "icon_url_istp",
    "resultName": "ISTP（工艺师）"
  },
  {
    "resultProp": [
      "E",
      "S",
      "T",
      "P"
    ],
    "resultDesc": "灵活，动手能力强，喜欢团队合作。",
    "resultPicture": "icon_url_estp",
    "resultName": "ESTP（探险家）"
  },
  {
    "resultProp": [
      "I",
      "N",
      "T",
      "P"
    ],
    "resultDesc": "独立思考，喜欢分析和解决问题。",
    "resultPicture": "icon_url_intp",
    "resultName": "INTP（逻辑学家）"
  },
  {
    "resultProp": [
      "E",
      "N",
      "T",
      "P"
    ],
    "resultDesc": "善于沟通，喜欢创新和变化。",
    "resultPicture": "icon_url_entp",
    "resultName": "ENTP（辩士）"
  }
];


// 调用函数并输出结果
const highestScoreResult = getHighestScoreResult(answerList, questions, questionResults);
console.log(highestScoreResult);
