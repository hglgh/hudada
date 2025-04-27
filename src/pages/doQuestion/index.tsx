import {View} from '@tarojs/components'
import {AtButton, AtRadio} from 'taro-ui'
import './index.scss'
import {useEffect, useState} from "react";
import questions from "../../data/questions.json";
import Taro from "@tarojs/taro";
import GolbalFooter from "../../components/GolbalFooter";

/**
 * @description 做题页面
 */
export default () => {

  // 当前题目序号 从1 开始
  const [current, setCurrent] = useState<number>(1)
  //当前题目
  const [currentQuestion, setCurrentQuestion] = useState<any>(questions[0])
  //当前题目答案
  const [currentAnswer, setCurrentAnswer] = useState<string>()
    //回答列表
  const [answerList] = useState<string[]>([])
  const questionOptions = currentQuestion.options.map(option => {
    return {
      label: `${option.key}.${option.value}`,
      value: option.key
    }
  })
  // 当序号变化时，更新当前题目和当前回答
  useEffect(() => {
    setCurrentQuestion(questions[current - 1])
    setCurrentAnswer(answerList[current - 1])
  },[current])
  return (
    <View className={'doQuestionPage'}>
      <View className={'at-article__h1 title'}>{current}、{currentQuestion.title}</View>
      <AtRadio options={questionOptions} value={currentAnswer} onClick={(value) => {
        setCurrentAnswer(value)
        answerList[current - 1] = value
      }}/>
      {current < questions.length && <AtButton
        type="primary"
        disabled={currentAnswer == null}
        onClick={() => {
          setCurrent(current + 1)
          // setCurrentQuestion(questions[current])
        }} circle className="controlBtn">
        下一题</AtButton>}
      {current == questions.length && <AtButton
        type="primary"
        disabled={!currentAnswer}
        onClick={() => {
          Taro.setStorageSync('answerList', answerList)
          Taro.navigateTo({
            url: '/pages/result/index'
          })
        }} circle className="controlBtn">
        查看结果</AtButton>}
      {current > 1 && <AtButton
        onClick={() => {
          setCurrent(current - 1)
          // setCurrentQuestion(questions[current - 2])
        }} circle className="controlBtn">
        上一题</AtButton>}
      <GolbalFooter/>
    </View>
  )
};

