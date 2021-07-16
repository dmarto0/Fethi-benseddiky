**# 请各位白嫖党不要加我了，谁都不是免费的劳动力，定制开发价格高，源码价格高！！！**

**# 请各位白嫖党不要加我了，谁都不是免费的劳动力，定制开发价格高，源码价格高！！！**

**# 请各位白嫖党不要加我了，谁都不是免费的劳动力，定制开发价格高，源码价格高！！！**

**# 特别提醒**

本项目2018年6月8日转入ShopAI B2B2C Android开发，新项目地址：[ShopAI B2B2C Android 码云](https://gitee.com/oyo775881/shopai-b2b2c-android) 

本项目2018年6月8日之后不再进行新功能开发，仅做一些BUG修改，紧随时代潮流，改用ShopAI吧~

PS:2018年7月24日，小幅度更新一波，主要是解决依赖的问题，已经热更新的一个小BUG。

**# ShopNC Android** 

**警告：禁止任何未授权商用！**  

**警告：禁止任何未授权商用！** 

**警告：禁止任何未授权商用！** 

**#目前未授权商用公司（黑名单）**

1.捷购商城：http://www.jiego.com.cn

**本开源程序作者：MapStory，联系QQ：1002285057(如需要商业授权，定制开发，请联系PS:闲聊勿扰，比较忙。)**

基于好商城V5.6的Android客户端，简单修改两行代码即可适配到自己的商城系统。

特别感谢以下开源项目：[xUtils](https://github.com/wyouflf/xUtils3),[Otto](https://github.com/square/otto),[Banner](https://github.com/youth5201314/banner),[Marqueeview](https://github.com/sfsheng0322/MarqueeView),[Glide](https://github.com/bumptech/glide),[QRCode-Android](https://github.com/XuDaojie/QRCode-Android),[Pulltorefresh](https://github.com/823546371/PullToRefresh),[ScrollableLayout](https://github.com/w446108264/ScrollableLayout),[SlideBack](https://github.com/leehong2005/SlideBack)

**#快速体验**

安装包位于：shopnc\release\shopnc-release.apk

**#快速开始**

1.修改：shopnc\src\main\java\top\yokey\shopnc\base\BaseConstant.java
```
public static final String SHARED_NAME = "yokey_shopnc"; //修改成为你的ShareName
public static final String URL = "http://33hao.yokey.top/"; //修改成为你的网站
```

2.如果修改了包名，为了兼容Android7.0,需要修改：shopnc\src\main\AndroidManifest.xml，shopnc\src\main\res\xml\path.xml，
```
将代码：top.yokey.shopnc 替换成自己的包名
```

3.支付宝支付

申请地址：https://www.alipay.com/

修改：APP客户端无需任何修改！

4.微信支付

申请地址：https://open.weixin.qq.com/

5.极光推送

申请地址：https://www.jiguang.cn/

修改：shopnc\build.gradle
```
JPUSH_APPKEY : 'a2fab7a5205a67d388889645' //为自己的极光推送ID
```
ShareSdk

申请地址：http://www.mob.com/

修改：shopnc\build.gradle
```
MobSDK 节点
```
**#V1.6更新日记**

1.一些多语言支持

2.热更新功能

**#V1.5更新日记**

1.更新滑动返回模块

2.修复二维码扫描秒退问题

3.修复首页只有Home能进入二维码扫描的BUG

4.完善了二维码扫描的逻辑

5.一些细节调整

6.一些小BUG修复

**#V1.4更新日记**

1.用户设置模块，一些小BUG修改

2.积分商城模块

3.一些页面调整

2.多语言适配（完成5%）

**#V1.3更新日记**

1.专题页支持

2.第三方登录支持

3.商品第三方分享支持

4.在线更新

5.一些小BUG修复

**#V1.2更新日记**

1.商家中心

2.支付宝支付

3.修复商品详细页显示虚拟商品时报错问题

4.客服系统增加商品显示

5.消息列表BUG修复

6.一些小细节修复

...

**#V1.1更新日记**

1.完善购物流程

2.二维码扫描

3.客服系统

4.我的财产功能

5.整体达到使用水平

...

**#V1.0更新日记**

1.首页适配

2.个人中心

...
