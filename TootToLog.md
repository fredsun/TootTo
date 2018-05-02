1. 在 ViewPager 的 onInterceptTouchEvent 和 onTouchEvent 消费掉滑动事件
2. 在 ViewPager 的 setCurrentItem 取消掉 ViewPager的 切换效果
3. MainActivity 里的Fragment用TransFragment替换, 即TransFragment 套具体内容的Fragment
4. TransFragment 挡住 MainActivity 的底部 TabLayout:  MainActivity 的 ViewPager 里套着的 Fragment 都是中转 Fragment, 如果中转 Fragment 替换出来的(详情)Fragment 还需要全屏, 就把 MainActivity 里的 ~~ViewPager.bringToFront()会影响到CoordinatorLayout~~.tab.setVisibility();
5. 底部view的联动隐藏:
    1. 仿知乎MainActivity底部TabLayout 通过behavior联动,~~根据recyclerview判断滑到顶底,~~ 并暴露接口给MainActivity在切换Fragment时是否关闭联动
    2. FragmentFirst 通过重写外部 FrameInterceptLayout 的 dispatchTouchEvent, 将 touch事件传递给顶部toolbar.
    3. FragmentFirstDetail 通过内部 ObservableScrollView 将 touch事件 传递给顶部toolbar和底部view.
6. Fragment的联动不用CoordinatorLayout, 因为MainActivity的behavior在dispatchTouchEvent时就拦截掉了, 无法传递到内部
7.  RecyclerView点击 继承自RecyclerView.OnItemTouchListener, 根据touch事件的时差判断是长按还是点击[参考](http://www.jianshu.com/p/f2e0463e5aef)
8. Fragment的back按键回调, 用的是接口[参考](http://www.jianshu.com/p/fff1ef649fc0)
9. 在 DetailFragment back 回去时 remove 掉 DetailFragment
10. 猫站oauth认证 [LoginActivity.java]
    三次请求,
    * 目的: 确认站名正确并拿到token.
    * 第 1 次: 输入站名, 再拼上(APP名, 回调地址, 权限访问, 网站主页去请求)
    成功则返回 client_id 和 client_secret
    * 第 2 次: 用 webview 打开 github oauth 验证拼上(权限范围, 返回值类型, client_id, 回调地址)
    成功则返回回调地址里设置的 intent, 通过intent-filter过滤后拿到返回值,结束
    * 第 3 次: 用第 1 次拿到的的 client_id, client_secret, 和第二次拿到的 code, 以及第 1 次时就约定好的回调地址 redirect_uri, 最后 准许类型grant_type 设置为 authorization_code.
    成功则返回 access_token.
> 如果自己做站点, 需要 github 的 oauth 登陆功能, 需要从 github 账号的 Settings -> Developer settings -> OAuth Apps 注册新的 OAuth App,
> 而如果只是 app 端接入别人的站点, 就不用管了, 这是站点要做的事.

* 第1次请求: 验证站点 - 获取猫站 client_id 和 clientSecret
  https://mao.daizhige.org/api/v1/apps

  * field:
    * client_name
    * redirect_uris
    * scopes
    * website

  * 返回:
    * id
    * name
    * website
    * redirect_uri
    * client_id
    * client_secret

* 第2次请求: 验证账号 - github 账号授权猫站[参考文档](https://github.com/tootsuite/documentation/blob/master/Using-the-API/OAuth-details.md)
https://mao.daizhige.org/oauth/authorize
  * 参数
    * scope read%20write%20follow
    * response_type code
    * client_id
    * redirect_uri oauth2redirect%3A%2F%2Forg.tootto%2F
  * 返回
    授权成功的回调是一个Intent, 通过Intent-filter过滤拿到

* 第3次请求: 获取 token
  https://mao.daizhige.org/oauth/token
  * field
    * client_id
    * client_secret
    * redirect_uri
    * code
    * grant_type
  * 返回
    * access_token
* 第4和之后的请求: 把 access_token 塞进请求header, Authorization: Bearer <access_token>


11. 添加 OAuthWebView 页面
设计: 仿Tusky
原因: 完善OAuth需要打开url的需求,
    1. 有 Chrome 的用户, 使用 CustomTabs 打开 url.
    2. 没有安装 Chrome 的用户用 webview 取代系统浏览器(系统浏览器无法有效控制关闭, 且无法控制cookie的残留, 导致重复登陆)
细节:
    1. webview添加加载条. 继承 WebChromeClient[参考](http://blog.csdn.net/qq_20785431/article/details/51599073)
    2. webview中progressbar的ui修改[参考](https://www.jianshu.com/p/63af8ea97aae)
    3. Chrome的CustomTabs[参考](https://github.com/GoogleChrome/custom-tabs-client)
    4. HTML.fromHtml(Spanned);已经被N弃用, 需要使用HTML.fromHtml(Spanned, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);[参考](https://stackoverflow.com/questions/37904739/html-fromhtml-deprecated-in-android-n)

12. 优化login页面, 在选中edittext后, 登陆按钮能显示在软键盘之上.
思路:
    1. ScollView包着LinearLayout, 当EditText被放在页面底部时软键盘不会遮挡(adjustResize无法解决), 同时edittext的setError可以正常随着软键盘的弹出而滚动.
    2. ScrollView添加fillViewport, 充满整个布局
    2. ~~清单里添加 adjustResize.~~

13. 修复bug: LoginActivity 打开MainActivity, 再finish()后, 此时按下back依然会回调LoginActivity.
    解决: 原计划通过CLEAR_TOP. 但无效, 查询后确认.[参考](https://www.cnblogs.com/supermanChao/p/6007671.html), CLEAR_TOP的使用前提是原Activity已经存在, 才会清掉其上面的activity. 后选用NEW_TASK|CLEAR_TASK

14. LoginActivity的软键盘修改右下角imeOptions为GO, 并添加监听, 和登陆按钮点击结果一样.
    [inputType](http://blog.csdn.net/qq_16064871/article/details/44701727)
    [imeOptions](https://www.jianshu.com/p/6ad48686e6fd)

15. 优化: LoginActivity的EditText失去焦点:
    方案: 外部view抢到焦点[参考](http://www.cnblogs.com/yejiurui/archive/2013/01/02/2841945.html)
    ```
    android:focusable="true"
    android:focusableInTouchMode="true"
    ```
16. 添加抽屉

抽屉内容
头像
id
@id

我的赞
草稿
过滤
个人设置
退出

导航(本站/提醒/公共时间线/联合时间线)

* 抽屉预览无默认toolbar
在根menu里添加
```
xmlns:tools="http://schemas.android.com/tools"
    tools:showIn="navigation_view"
```
* 抽屉限制死了从左往右的打开方式. layout_gravity设置为left.
* 抽屉的宽度要小于320dp
* 抽屉需要选中效果
  1. group 添加 checkableBehavior = single
  2. onNavigationItemSelected 返回true
  3. 可能直接item.setChecked(true)也行



17. Preference设置
* 原则:
  1. 修改即保存, 不在最后推出时还问是否保存(自18.1.9微博,微信,twiter,ins)
* 设计: 仿Twidere,半打开的Fragment展示详细Preference
* 布局: SlidingPaneLayout包着两个Framelayout,左侧填入listview仿造设置页, 右侧通过marginStart保持左侧空隙
* 合上pane
![](https://raw.githubusercontent.com/sunxlfred/RES/78cd72720f41e478f83563bc9e4566795dcdeec4/preferenceClosePane.png)
* 打开pane
![](https://raw.githubusercontent.com/sunxlfred/RES/78cd72720f41e478f83563bc9e4566795dcdeec4/preferenceOpenPane.png)
* 选项
  * 网络
  * 主题
  * 关于

18. 退出选项添加 DialogFragment 确认
创建通用可编辑的 DialogFragment 进行确认


19. 左侧抽屉栏头像使用前景色(foreground)作为遮罩
20. kotlin无法进入debug, 关闭instant run 再打开

22. 退出动画使用overridedepending, 不考虑 windowIsTranslucent。因为修改 windowAnimationStyle 为 @android:style/Animation.Translucent 的子类仍然无法显示 Activity 退出动画

23. 时间线。
    1. 时间线不存在删除单条item的需求的必要(通过屏蔽即可).
    2. 对应服务器端的类是 Status (不含getset, 只有gson的注解)
    3. 对应本地数据端的类是 StatusViewData (含get/set)
    4. 这个 item 是占位符还是 status, 通过 Either<Placeholder, Status> 存储
    5. 存储标识位 Either 的 List 和 存储数据 StatusViewData 的 list 通过一个自定义的 PairedList 关联起来.
    6. 对于时间线页面, 服务器给的数据是一个个 Status, 展示结果为 LoadMore 按钮和 Status 两种, 所以创建结构体 Either<Placeholder, Status>, 用于标志展示结果的类型, 结构体设置为非 Placeholder 即 Status, 并保存 Status 作为其内部参数 value. 最后将 Either 和 处理后的 StatusViewData 用 PairedList 串起来用作整个 adapter 的 list
    7. 点击事件通过两次回调
        * View 通过 SparkEventListener 把 view 里判断出的点击事件传递给 findViewById 了的 ViewHolder.
        * ViewHolder 通过 StatusActionListener 把事件传递给 Fragment.
    8. RecyclerView 添加 header 通过itemType(待优化装饰者模式)
        * Adapter 关联 layout 和 ViewHolder, 数据,
        * ViewHolder 关联具体  view 和数据
        * 继承 RecyclerView.OnScrollListener 实现剩余 15 条未显示时去拉接口(默认一次拉 30 条 status)
    9. 三种拉取时间线的位置
        * TOP
          * 下拉刷新
          * SharedPreference 更新设置不显示图片时
        * MIDDLE
          * 其余 Fragment 切换时
        * BOTTOM
          * 上拉加载
          * 第一次进入
    10. content 的读取[参考](https://www.jianshu.com/p/d3bef8449960)
          * 原字段为
          ```xmlns
            "content": "<p><a href=\"https://mao.daizhige.org/tags/%E8%AF%BB%E4%B9%A6\" class=\"mention hashtag\" rel=\"tag\">#<span>读书</span></a> 以前学《茅屋为秋风所破歌》感觉诗人挺惨的，现在读到&quot;南村群童欺我老无力，忍能对面为盗贼，公然抱茅入竹去，唇焦口燥呼不得&quot;突然感觉还蛮好玩的，南村的穷小子看到这位老爷爷，嘴一咧:&quot;欸~老头，追不上，哈哈，溜了溜了&quot;，很有趣。</p>",       
          ```
          * android 自带 android:autoLink="xxx", 但下划线, 超链接字体颜色, 特殊超链接跳转@的识别和打开方式, 默认的都无法被控制
          * autoLink 的原理是在
          * setText 时
              * 通过 Linkify 的正则表达式将 text 根据 autoLink 类型再按照自己的规则生成
                  * 新的 URLSpan,
                      * URLSpan 的 onClick方法控制 Intent 跳转
                  * URLSpan 的父类 ClickSpan 的 updateDrawState 控制下划线和颜色
          * 另外, 不用 autoLink 通过 setMovementMethod 实现超链接的点击
          * @ 和 #的跳转交给 BaseFragment
          * emoji 鉴于可能提供 4.4 KitKat 之前版本的支持, 采用 glide 外部加载而不是 textview 默认自带的解析(其实默认的已经很棒了).
          * 切记 view.setMovementMethod
          * 总体逻辑
          * setText中
              * LinkHelper -> 替代 Linkify, 区分@,#,网址.三类特殊 text(伪正则)
                  * CustomURLSpan -> 取代 URLSpan
                      *  onClick 中跳给 LinkHelper处理(再接口转给上级View)
                  * 重写 updateDrawState, 覆盖掉 setUnderlineText(false).不用下划线
                  feat: FirstPagingFragment 实现了 StatusActionListener, 并将listener里具体的操作交给新的父类 HubFragment.
          * 坑:
          TextView 中:
            1. 默认情况下，点击 ClickableSpan 的文本时会同时触发绑定在 TextView 的监听事件；
            2. 默认情况下，点击 ClickableSpan 的文本之外的文本时，TextView 会消费该事件，而不会传递给父 View；

          * 解决:
            1. 重写 textview 的 onTouchListener, 将原 LinkMovementMethod 中的onTouchEvent 代码移过来
            2. textview 注册和外部一样的点击事件
            3. 后期优化参考
            4. https://stackoverflow.com/questions/14579785/can-i-disable-the-scrolling-in-textview-when-using-linkmovementmethod
            5. https://stackoverflow.com/questions/16792963/android-clickablespan-intercepts-the-click-event
            6. https://blog.csdn.net/zhaizu/article/details/51038113
            7. https://www.jianshu.com/p/413184996fc8
            8. http://blog.cgsdream.org/2017/03/22/textview-highlight-clickablespan/
            9. https://stackoverflow.com/questions/8558732/listview-textview-with-linkmovementmethod-makes-list-item-unclickable
            10. https://www.jianshu.com/p/d3bef8449960
      11. recyclverView 添加 setHasFixedSize(true), 确保itemInsert时调用的layoutchildren(), 而不是requestLayout()去重新计算layout.

24. 搜索页面
  * <主页面>点击搜索框 -> 打开透明的<DialogFragment>输入搜索内容 -> 打开<搜索结果>页面
  * 即搜索 editText 和搜索历史是分开的
  * 搜索历史页面用DialogFragment通过```getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);```使 alertDialog 里的 editText 能打开软键盘[参考](https://blog.csdn.net/asdfasfasfs/article/details/77503158)
  * 搜索历史用 Room 存储, 数据库的操作使用 rxjava2
  * 通过 ItemTouchHelper, 添加 RecyclerView 对侧滑删除的支持.TODO 侧滑事件的监听到底由 activity 还是 adapter 还是viewholder 执行?
  * 点击软键盘确认和顶部搜索按钮, 对 editText 的内容判空后存入数据库和打开新的 Activity
  * 搜索结果分为
    * account
    * tag
  * SearchResultActivity 页面只会被带着搜索文本的 intent 打开. 且因为 DialogFragment 的 SearchHistory 的缘故, s所以 SearchResultActyivity 使用 singletop模式启动.
25. timeline完善
  * 转发人添加icon, tintdrawable 染色转发 icon 的 png.
  * 添加间距
  * 媒体预览图
    * 最后还是用一个 ConstraintLayout 包裹了 Imageview 等, 因为用于遮挡的 view 的宽高需要可能 GONE 的 imageview 来固定.
    * 布局上使用四个图 + 媒体隐藏 + 覆盖图 + 敏感内容, 其中四个图利用 ConstraintLayout 的startOf等功能让每个图上都有个图覆盖.
    * 根据flag敏感内容text被点击后
    * 敏感内容eye点击可以用覆盖图盖住预览图们
    * 图片预览的toolbar中, theme 在3.0前只能应用到自己, 3.0以后可以应用到 view 和子 view,适配3.0前的话, 需要每个 view 加一次 theme,  poptheme 适配之后的
    * 保存图片需要write_extranal权限, 运行时获取
    * 图片预览-> activity + ViewPager + Fragment, 其中 Fragment 内为 PhotoView.
    * 媒体预览-> VideoView.
    * TODO 主题暂时为app,后期找到对应toolbar左箭头属性后改成android:(即从系统级改为自身)
26. 处理点赞和转发的请求
    * 只发出请求, 不关注回调

27. toolbar 白底黑字 theme + poptheme 共同作用








* TODO 知乎自杀干预页面.
//stolen from http://www.jianshu.com/p/2f1ce7d0d64c
* TODO 优化webview error页, 自己写error HTML, 在onReceivedError时读取.[参考](http://stackoverflow.org.cn/front/ask/view?ask_id=33638)
* TODO webview进度条添加动画, 防止过快加载[参考](https://juejin.im/post/597734f76fb9a06bb874c208)
* TODO read TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
* TODO webview sign_out的错误页面
* TODO 设置后期进阶: 固定头img的抽屉[参考](https://github.com/mikepenz/MaterialDrawer/blob/develop/app/src/main/java/com/mikepenz/materialdrawer/app/PersistentDrawerActivity.java)
* aloha2018
* feature: 查看他人时间段的推文
* feature: 翻译!!!
