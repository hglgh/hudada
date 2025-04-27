import {Image, View} from '@tarojs/components'
import {AtButton} from 'taro-ui'
import './index.scss'
import Taro from "@tarojs/taro";
import questionResults from '../../data/question_results.json'
import questions from '../../data/questions.json'
import GolbalFooter from "../../components/GolbalFooter";
import {getHighestScoreResult} from "../../utils/bizUtils";

/**
 * 结果页
 */
export default () => {
  const answerList = Taro.getStorageSync('answerList')
  if (!answerList || answerList.length < 1){
    Taro.showToast({
      title: '为空',
      icon: 'error',
      duration: 2000
    })
  }
  const result = getHighestScoreResult(answerList, questions, questionResults)
  return (
    <View className='resultPage'>
      <View className={'at-article__h1 title'}>{result.resultName}</View>
      <View className="at-article__h3 subTitle">
        {result.resultDesc}
      </View>
      <AtButton
        type="primary"
        size="normal"
        circle
        onClick={() => {
          Taro.reLaunch({
            url: '/pages/index/index'
          })
        }}
        className="enterBtn">返回主页</AtButton>
      <Image src={require('../../assets/images/mbti.png')} style={{ width: "100%" }} mode="aspectFill" />
      <GolbalFooter/>
    </View>
  );
};
