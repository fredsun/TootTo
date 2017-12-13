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
    两次请求
    第 1 次: 输入站名, 再拼上(APP名, 回调地址, 权限访问, 网站主页去请求)
    成功则返回 client_id 和 client_secret
    第 2 次: 用 webview 打开 github oauth 验证拼上(权限范围, 返回值类型, client_id, 回调地址)
    成功则返回回调地址里设置的 intent, 通过intent-filter过滤后拿到返回值,结束

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
