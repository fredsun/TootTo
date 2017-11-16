1. 在 ViewPager 的 onInterceptTouchEvent 和 onTouchEvent 消费掉滑动事件
2. 在 ViewPager 的 setCurrentItem 取消掉ViewPagerde 切换效果
3. MainActivity 里的Fragment用TransFragment替换, 即TransFragment 套具体内容的Fragment
4. TransFragment 挡住 MainActivity 的底部 TabLayout:  MainActivity 的 ViewPager 里套着的 Fragment 都是中转 Fragment, 如果中转 Fragment 替换出来的(详情)Fragment 还需要全屏, 就把 MainActivity 里的 ~~ViewPager.bringToFront()会影响到CoordinatorLayout~~.tab.setVisibility();
5. 仿知乎MainActivity底部TabLayout 通过behavior联动, 根据recyclerview判断滑到顶底, 并暴露接口给MainActivity在切换Fragment时是否关闭联动(好像知乎也没这个切换)

6. TransFragment里嵌套的子Fragment的底部View联动通过ObservableScrollView. 因为MainActivity的behavior在dispatchTouchEvent时就拦截掉了, 无法传递到内部
