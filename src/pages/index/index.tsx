import {Image, View} from '@tarojs/components'
import {AtButton} from 'taro-ui'
import './index.scss'
import Taro from "@tarojs/taro";
import GolbalFooter from "../../components/GolbalFooter";

/**
 * 主页
 */
export default () => {
  return (
    <View className='indexPage'>
      <View className={'at-article__h1 title'}>MBTI 性格测试</View>
      <View className="at-article__h3 subTitle">
        只需 2 分钟，就能非常准确地描述出你是谁，以及你的性格特点
      </View>
      <AtButton
        type="primary"
        size="normal"
        circle
        onClick={() => {
          Taro.navigateTo({
            url: '/pages/doQuestion/index'
          })
        }}
        className="enterBtn">开始测试</AtButton>
      <Image src={require('../../assets/images/mbti.png')} style={{ width: "100%" }} mode="aspectFill" />
      <GolbalFooter/>
    </View>
  );
};
