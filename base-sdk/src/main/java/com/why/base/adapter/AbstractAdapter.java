package com.why.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by wuhongyun on 17-8-28.
 * 针对listview的adapter进行的优化
 */

public abstract class AbstractAdapter<T> extends BaseAdapter {

    private List<T> mListData;
    private Context mContext;

    /**
     * 尽量不是用这个构造方法
     */
    public AbstractAdapter(){

    }
    public AbstractAdapter(Context context, List<T> listData){
        this.mContext = context;
        this.mListData = listData;
    }
    @Override
    public int getCount() {
        return mListData!=null?mListData.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mListData!=null?mListData.get(position):position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            holder = onCreateViewHolder(parent,getItemViewType(position));
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder,position);

        return holder.mItemView;
    }

    /**
     * 用于绑定数据
     * @param holder
     * @param position
     */
    protected abstract void onBindViewHolder(ViewHolder holder, int position);

    /**
     * 用于创建viewholder
     * @param parent
     * @param itemViewType
     * @return
     */
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int itemViewType);

    /**
     * 基类holder，用于设置tag保存
     */
    public class ViewHolder{
        public View mItemView;

        public ViewHolder(View itemView){
            this.mItemView = itemView;
            itemView.setTag(this);
        }

    }

}
