package com.why.base.ui;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.why.base.enums.BaseUI;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wuhongyun on 17-7-17.
 * 为了方便扩展使之继承了recyclerview的holder
 */
@BaseUI
public class BaseHolder extends RecyclerView.ViewHolder {

    private final Unbinder unbinder;
//    private SparseArray<View> mViews;
    /**
     * 此处使用butterKnife进行了view绑定操作
     * @param itemView
     */
    public BaseHolder(View itemView) {
        super(itemView);
        unbinder = ButterKnife.bind(this, itemView);

        //考虑到用法的兼容性，可以在此使用集合装载
//        mViews = new SparseArray<>();
    }

    /**
     * 在不使用时把引用清空，可尽快回首资源
     */
    public void unBind(){
        if (unbinder!=null){
            unbinder.unbind();
        }
//        if (mViews!=null&&mViews.size()>0){
//            mViews.clear();
//        }
    }

    /**
     * 设置点击事件
     * @param onClickListener 事件监听
     * @param ids 需要设置点击事件的view id
     */
    public void setListeners(View.OnClickListener onClickListener,@IdRes int ... ids){
        for (int id :ids){
            try {
                View view = itemView.findViewById(id);
                if (view!=null){
                    if (!view.isClickable()){
                        view.setClickable(true);
                    }
                    view.setOnClickListener(onClickListener);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置view的点击事件
     * @param onClickListener 事件回调者
     * @param views 需要设置点击事件的view
     */
    public void setListeners(View.OnClickListener onClickListener,View ... views){

        if (views!=null&&views.length>0){
            for (View view : views){
                try {
                    if (views!=null){
                        if (!view.isClickable()){
                            view.setClickable(true);
                        }
                        view.setOnClickListener(onClickListener);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }


    }

    /**
     * 获取此viewholder的itemview
     * @return itemView
     */
    public View getConvertView(){
        return itemView;
    }

}
