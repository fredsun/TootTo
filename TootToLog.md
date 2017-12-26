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

* 第2次请求: 验证账号 - github账号授权猫站[参考文档](https://github.com/tootsuite/documentation/blob/master/Using-the-API/OAuth-details.md)
https://mao.daizhige.org/oauth/authorize
  * 参数
    * scope read%20write%20follow
    * response_type code
    * client_id
    * redirect_uri oauth2redirect%3A%2F%2Forg.tootto%2F
  * 返回
    授权成功的回调是一个Intent, 通过Intent-filter过滤拿到

* 第3次请求: 获取token
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


11. 添加OAuthWebView页面
原因: 完善OAuth需要打开url的需求,
    1. 有 Chrome 的用户, 使用CUstomTabs打开url.
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

* TODO 知乎自杀干预页面.
//stolen from http://www.jianshu.com/p/2f1ce7d0d64c
* TODO 优化webview error页, 自己写error HTML, 在onReceivedError时读取.[参考](http://stackoverflow.org.cn/front/ask/view?ask_id=33638)
* TODO webview进度条添加动画, 防止过快加载[参考](https://juejin.im/post/597734f76fb9a06bb874c208)
* TODO read TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
