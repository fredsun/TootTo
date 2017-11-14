1. 在 ViewPager 的 onInterceptTouchEvent 和 onTouchEvent 消费掉滑动事件
2. 在 ViewPager 的 setCurrentItem 取消掉ViewPagerde 切换效果
2. 挡住 MainActivity 的底部 TabLayout:  MainActivity 的 ViewPager 里套着的 Fragment 都是中转 Fragment, 如果中转 Fragment 替换出来的(详情)Fragment 还需要全屏, 就把 MainActivity 里的 ViewPager.bringToFront().
