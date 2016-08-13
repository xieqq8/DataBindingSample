package com.example.zhanghongqiang.databindingsample.presenter;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghongqiang on 16/7/20  上午10:49
 * ToDo:列表的契约者,用来管理mvvp里的v
 */
public interface RecyclerViewContract {

    //加载数据
    interface IFLoadData {
        void loadData();
    }

    //适配器使用
    interface IFListview<T> {

        //可以根据数据类型来显示不同的item
        int getViewType(int position);

        //显示数据
        void updateView(@NonNull T data, @NonNull ViewDataBinding binding);

        //这里的使用一定要注意,用第二个参数来判断
        ViewDataBinding createView(ViewGroup parent, int position);
    }

    //代理者
    abstract class XRDelegate<T> {

        public XRDelegate(IFLoadData l, IFListview f) {
            F = f;
            L = l;
        }

        //暴露给外界的接口是实现者
        RecyclerViewContract.IFListview F = null;

        RecyclerViewContract.IFLoadData L = null;


        abstract void reLoadData();

        abstract void notifyDataSetChanged();

        abstract void notifyItemChanged(int position);

        abstract void notifyItemRangeRemoved(int position);


        //适配器
        class MyAdapter<T> extends XRecyclerView.Adapter<MyAdapterViewHolder<T>> {

            ArrayList<T> mDatas = new ArrayList<>();

            @Override
            public MyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                //调用借口的方法
                ViewDataBinding binding = F.createView(parent, viewType);
                MyAdapterViewHolder viewHolder = new MyAdapterViewHolder(binding.getRoot());
                viewHolder.mViewDataBinding = binding;

                return viewHolder;
            }

            @Override
            public void onBindViewHolder(MyAdapterViewHolder holder, int position) {
                holder.setData(getItem(position));
            }

            private T getItem(int position) {
                return mDatas.get(position);
            }

            @Override
            public int getItemCount() {
                return mDatas != null ? mDatas.size() : 0;
            }

            @Override
            public int getItemViewType(int position) {
                //调用接口的方法
                return F.getViewType(position);
            }

            public void clearList() {
                mDatas.clear();
                notifyDataSetChanged();
            }

            public void addNewList(List<T> list) {
                if (list != null && list.size() > 0) {
                    mDatas.addAll(list);
                    notifyDataSetChanged();
                }
            }


        }

        /**
         * RecyclerView万用的适配器
         */
        class MyAdapterViewHolder<T> extends XRecyclerView.ViewHolder {


            ViewDataBinding mViewDataBinding;

            public MyAdapterViewHolder(View itemView) {
                super(itemView);
            }

            public void setData(T data) {
                //调用接口的方法
                F.updateView(data, mViewDataBinding);
            }
        }

    }
}
