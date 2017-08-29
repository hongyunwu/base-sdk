package com.why.base.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.why.base.ui.BaseHolder;

import java.util.List;

/**
 * Created by wuhongyun on 17-8-29.
 *  TODO 加载更多需要考虑1,下拉到底部自动加载 2,点击加载 3,没有更多数据时去掉加载更多的view
 */

public abstract class LoadMoreAdapter<T> extends RecyclerView.Adapter<BaseHolder> {

    //当前holder的viewtype 为防止type被使用，设为较大的值
    static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 1;

    View mLoadMoreView;

    int mLoadMoreId;
    private boolean needToShowLoadMore = true;

    private Context mContext;
    private List<T> mListData;
    public LoadMoreAdapter(Context context, List<T> listData){
        this.mContext = context;
        this.mListData = listData;
    }
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==ITEM_TYPE_LOAD_MORE){
            //
            return createLoadMoreHolder(parent,viewType);
        }

        return createNormalViewHeader(parent,viewType);
    }

    private BaseHolder createLoadMoreHolder(ViewGroup parent, int viewType) {
        BaseHolder holder = null;
        if (mLoadMoreView!=null){
            holder = new BaseHolder(mLoadMoreView);
        }else if (mLoadMoreId!=0){
            holder = new BaseHolder(View.inflate(mContext,mLoadMoreId,null));
        }
        if (holder!=null){
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onLoadMoreRequestedListener!=null){
                        onLoadMoreRequestedListener.onLoadMoreRequested();
                    }
                }
            });

        }
        return holder;
    }

    public abstract BaseHolder createNormalViewHeader(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        //判断滚动位置来判断加载更多
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_LOAD_MORE){
            if (onLoadMoreRequestedListener!=null){
                onLoadMoreRequestedListener.onLoadMoreRequested();
            }
        }else {
            bindNormalViewHolder(holder,position);
        }

    }

    protected abstract void bindNormalViewHolder(BaseHolder holder, int position);

    @Override
    public int getItemCount() {
        return (mListData!=null?mListData.size():0) + (isShowLoadMoreView()?1:0);
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMoreView() && position ==getItemCount()-1){//
            return ITEM_TYPE_LOAD_MORE;
        }
        return super.getItemViewType(position);
    }

    /**
     * 设置loadmoreview
     * @param loadMoreView
     */
    public void setLoadMoreView(View loadMoreView){

        this.mLoadMoreView = loadMoreView;
        startLoadMoreView(true);
    }

    /**
     * 重载方法，设置loadmoreview的layout id
     * @param loadMoreId
     */
    public void setLoadMoreView(@LayoutRes int loadMoreId){

        this.mLoadMoreId = loadMoreId;
        startLoadMoreView(true);
    }

    /**
     * 判断当前adapter是否有LoadMore控件
     * @return
     */
    private boolean hasLoadMoreView(){

        return mLoadMoreView!=null || mLoadMoreId!=0;
    }

    /**
     * 是否需要显示loadmore控件
     * @return
     */
    public boolean isShowLoadMoreView(){

        return hasLoadMoreView()&&needToShowLoadMore;
    }

    /**
     * 停止显示loadmoreview
     */
    public void stopLoadMoreView(){

        needToShowLoadMore = false;
    }

    /**
     * 显示loadmoreview
     *
     * @param needToShowLoadMore
     */
    public void startLoadMoreView(boolean needToShowLoadMore){
        this.needToShowLoadMore = needToShowLoadMore;
    }

    /**
     * 加载更多
     */
    public interface OnLoadMoreRequestedListener{
        void onLoadMoreRequested();
    }

    /**
     * 设置请求加载更多的监听回调
     *
     * @param onLoadMoreRequestedListener
     */
    public void setOnLoadMoreRequestedListener(OnLoadMoreRequestedListener onLoadMoreRequestedListener) {
        this.onLoadMoreRequestedListener = onLoadMoreRequestedListener;
    }

    private OnLoadMoreRequestedListener onLoadMoreRequestedListener;

}
