package org.tootto.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import org.tootto.R;
import org.tootto.adapter.FirstFragmentAdapter;
import org.tootto.anim.BottomBehaviorAnim;
import org.tootto.ui.fragment.view.observablescrollview.ObservableScrollView;
import org.tootto.ui.fragment.view.observablescrollview.ObservableScrollViewCallbacks;
import org.tootto.ui.fragment.view.observablescrollview.ScrollState;

import java.util.ArrayList;

/**
 * Created by fred on 2017/11/13.
 */

public class FragmentFirstDetail extends Fragment implements ObservableScrollViewCallbacks {
    RecyclerView recyclerFirstFragment;
    ArrayList<String> mList = new ArrayList<>();
    FirstFragmentAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;
    String tag = "FragmentFirstDetail";
    protected BottomBehaviorAnim mBottomAnim;
    TextView tvDetailBottom;
    boolean isBottomHide = false;
    private ObservableScrollView scrollHome;
    private int mScrollY = -1;
    boolean isAnimInit = false;
    TextView tvDetailContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_detail, container, false);
        tvDetailContent = view.findViewById(R.id.tv_detail_content);
        tvDetailContent.setText("<!--\n" +
                "  Copyright 2014 Soichiro Kashima\n" +
                "  Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "  you may not use this file except in compliance with the License.\n" +
                "  You may obtain a copy of the License at\n" +
                "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                "  Unless required by applicable law or agreed to in writing, software\n" +
                "  distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "  See the License for the specific language governing permissions and\n" +
                "  limitations under the License.\n" +
                "-->\n" +
                "<com.github.ksoichiro.android.observablescrollview.ObservableScrollView xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:id=\"@+id/scroll\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    android:fillViewport=\"true\">\n" +
                "\n" +
                "    <TextView\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:layout_marginBottom=\"@dimen/activity_vertical_margin\"\n" +
                "        android:layout_marginLeft=\"@dimen/activity_horizontal_margin\"\n" +
                "        android:layout_marginRight=\"@dimen/activity_horizontal_margin\"\n" +
                "        android:layout_marginTop=\"@dimen/activity_vertical_margin\"\n" +
                "        android:text=\"@string/lipsum\" />\n" +
                "\n" +
                "</com.github.ksoichiro.android.observablescrollview.ObservableScrollView>" +
                "<!--\n" +
                "\" +\n" +
                "                \"  Copyright 2014 Soichiro Kashima\\n\" +\n" +
                "                \"  Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n\" +\n" +
                "                \"  you may not use this file except in compliance with the License.\\n\" +\n" +
                "                \"  You may obtain a copy of the License at\\n\" +\n" +
                "                \"      http://www.apache.org/licenses/LICENSE-2.0\\n\" +\n" +
                "                \"  Unless required by applicable law or agreed to in writing, software\\n\" +\n" +
                "                \"  distributed under the License is distributed on an \\\"AS IS\\\" BASIS,\\n\" +\n" +
                "                \"  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n\" +\n" +
                "                \"  See the License for the specific language governing permissions and\\n\" +\n" +
                "                \"  limitations under the License.\\n\" +\n" +
                "                \"-->\\n\" +\n" +
                "                \"<com.github.ksoichiro.android.observablescrollview.ObservableScrollView xmlns:android=\\\"http://schemas.android.com/apk/res/android\\\"\\n\" +\n" +
                "                \"    android:id=\\\"@+id/scroll\\\"\\n\" +\n" +
                "                \"    android:layout_width=\\\"match_parent\\\"\\n\" +\n" +
                "                \"    android:layout_height=\\\"match_parent\\\"\\n\" +\n" +
                "                \"    android:fillViewport=\\\"true\\\">\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"    <TextView\\n\" +\n" +
                "                \"        android:layout_width=\\\"match_parent\\\"\\n\" +\n" +
                "                \"        android:layout_height=\\\"wrap_content\\\"\\n\" +\n" +
                "                \"        android:layout_marginBottom=\\\"@dimen/activity_vertical_margin\\\"\\n\" +\n" +
                "                \"        android:layout_marginLeft=\\\"@dimen/activity_horizontal_margin\\\"\\n\" +\n" +
                "                \"        android:layout_marginRight=\\\"@dimen/activity_horizontal_margin\\\"\\n\" +\n" +
                "                \"        android:layout_marginTop=\\\"@dimen/activity_vertical_margin\\\"\\n\" +\n" +
                "                \"        android:text=\\\"@string/lipsum\\\" />\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"</com.github.ksoichiro.android.observablescrollview.ObservableScrollView>\"<!--\\n\" +\n" +
                "                \"  Copyright 2014 Soichiro Kashima\\n\" +\n" +
                "                \"  Licensed under the Apache License, Version 2.0 (the \\\"License\\\");\\n\" +\n" +
                "                \"  you may not use this file except in compliance with the License.\\n\" +\n" +
                "                \"  You may obtain a copy of the License at\\n\" +\n" +
                "                \"      http://www.apache.org/licenses/LICENSE-2.0\\n\" +\n" +
                "                \"  Unless required by applicable law or agreed to in writing, software\\n\" +\n" +
                "                \"  distributed under the License is distributed on an \\\"AS IS\\\" BASIS,\\n\" +\n" +
                "                \"  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\n\" +\n" +
                "                \"  See the License for the specific language governing permissions and\\n\" +\n" +
                "                \"  limitations under the License.\\n\" +\n" +
                "                \"-->\\n\" +\n" +
                "                \"<com.github.ksoichiro.android.observablescrollview.ObservableScrollView xmlns:android=\\\"http://schemas.android.com/apk/res/android\\\"\\n\" +\n" +
                "                \"    android:id=\\\"@+id/scroll\\\"\\n\" +\n" +
                "                \"    android:layout_width=\\\"match_parent\\\"\\n\" +\n" +
                "                \"    android:layout_height=\\\"match_parent\\\"\\n\" +\n" +
                "                \"    android:fillViewport=\\\"true\\\">\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"    <TextView\\n\" +\n" +
                "                \"        android:layout_width=\\\"match_parent\\\"\\n\" +\n" +
                "                \"        android:layout_height=\\\"wrap_content\\\"\\n\" +\n" +
                "                \"        android:layout_marginBottom=\\\"@dimen/activity_vertical_margin\\\"\\n\" +\n" +
                "                \"        android:layout_marginLeft=\\\"@dimen/activity_horizontal_margin\\\"\\n\" +\n" +
                "                \"        android:layout_marginRight=\\\"@dimen/activity_horizontal_margin\\\"\\n\" +\n" +
                "                \"        android:layout_marginTop=\\\"@dimen/activity_vertical_margin\\\"\\n\" +\n" +
                "                \"        android:text=\\\"@string/lipsum\\\" />\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"</com.github.ksoichiro.android.observablescrollview.ObservableScrollView>\" +\n" +
                "                \"<!--\\n\" +\n" +
                "                \"\\\" +\\n\" +\n" +
                "                \"                \\\"  Copyright 2014 Soichiro Kashima\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  Licensed under the Apache License, Version 2.0 (the \\\\\\\"License\\\\\\\");\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  you may not use this file except in compliance with the License.\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  You may obtain a copy of the License at\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"      http://www.apache.org/licenses/LICENSE-2.0\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  Unless required by applicable law or agreed to in writing, software\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  distributed under the License is distributed on an \\\\\\\"AS IS\\\\\\\" BASIS,\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  See the License for the specific language governing permissions and\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"  limitations under the License.\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"-->\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"<com.github.ksoichiro.android.observablescrollview.ObservableScrollView xmlns:android=\\\\\\\"http://schemas.android.com/apk/res/android\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    android:id=\\\\\\\"@+id/scroll\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    android:layout_width=\\\\\\\"match_parent\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    android:layout_height=\\\\\\\"match_parent\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    android:fillViewport=\\\\\\\"true\\\\\\\">\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"    <TextView\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"        android:layout_width=\\\\\\\"match_parent\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"        android:layout_height=\\\\\\\"wrap_content\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"        android:layout_marginBottom=\\\\\\\"@dimen/activity_vertical_margin\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"        android:layout_marginLeft=\\\\\\\"@dimen/activity_horizontal_margin\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"        android:layout_marginRight=\\\\\\\"@dimen/activity_horizontal_margin\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"        android:layout_marginTop=\\\\\\\"@dimen/activity_vertical_margin\\\\\\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"        android:text=\\\\\\\"@string/lipsum\\\\\\\" />\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"\\\\n\\\" +\\n\" +\n" +
                "                \"                \\\"</com.github.ksoichiro.android.observablescrollview.ObservableScrollView>\"");

//        tvDetailContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        scrollHome = view.findViewById(R.id.observable_scroll_text);
        scrollHome.setScrollViewCallbacks(this);
        Fragment parentFragment = getParentFragment();
        scrollHome.setTouchInterceptionViewGroup((ViewGroup) parentFragment.getView().findViewById(R.id.content));
        if (parentFragment instanceof ObservableScrollViewCallbacks) {
            scrollHome.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentFragment);
        }

        tvDetailBottom = view.findViewById(R.id.tv_detail_bottom);
        tvDetailContent.post(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, "height"+tvDetailContent.getWidth());
            }
        });
        return view;
    }

    public static FragmentFirstDetail newInstance(){
        FragmentFirstDetail fragmentFirstDetail = new FragmentFirstDetail();
        return fragmentFirstDetail;
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {


    }

    /*
    * @Description: ObservableScrollView的回调
    * @author: fred
    * @date: 2017/11/29
    * @attention: 和github上的比添加了int a,b,oldA,oldB变量, 传递前一次的touch距离, ListView/RecyclerView无法使用, 这四个参数恒为0.
    */
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging, int a, int b, int oldA, int oldB) {
        //速度快的下拉
        if(Math.abs(b - oldB)> 20 && b > oldB && isBottomHide == false) {
            mBottomAnim.hide();
            isBottomHide = true;
        }

        //速度快的上滑
        if (Math.abs(b - oldB)> 20 && b < oldB && isBottomHide == true){
            mBottomAnim.show();
            isBottomHide = false;
        }


        //如果不能下拉或者上滑(到底/顶)
        if (!scrollHome.canScrollVertically(1) || !scrollHome.canScrollVertically(-1)){
            if (isBottomHide){
                mBottomAnim.show();
                isBottomHide = false;
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
        if (!isAnimInit){
            mBottomAnim = new BottomBehaviorAnim(tvDetailBottom);
            isAnimInit = true;
        }
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

}
