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
