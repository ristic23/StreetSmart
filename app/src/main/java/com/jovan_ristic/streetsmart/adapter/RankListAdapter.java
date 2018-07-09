package com.jovan_ristic.streetsmart.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovan_ristic.streetsmart.Model.Question;
import com.jovan_ristic.streetsmart.R;
import com.jovan_ristic.streetsmart.activity.RankListActivity;

import java.util.List;

public class RankListAdapter extends RecyclerView.Adapter<RankListAdapter.ViewHolder>
{

    private Context mContext;
    private List<RankListActivity.RankUser> arrayRankList;
    private RankListActivity rankListActivity;

    public RankListAdapter(Context context, List<RankListActivity.RankUser> points, RankListActivity tPa)
    {
        mContext = context;
        arrayRankList = points;
        rankListActivity = tPa;
    }

    public void setList(List<RankListActivity.RankUser> q)
    {
        this.arrayRankList = q;
        notifyDataSetChanged();
    }

    @Override
    public RankListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rank_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return  new RankListAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RankListAdapter.ViewHolder holder, final int position)
    {
        TextView textQuestion = (TextView)holder.mLinearLayout.findViewById(R.id.titleQuestion);
        TextView positionTextView = holder.mLinearLayout.findViewById(R.id.positionTextView);

        textQuestion.setText(arrayRankList.get(0).userName + "  -  " + String.valueOf(arrayRankList.get(0).points));
        positionTextView.setText(String.valueOf(position+1));
        try {
//            if (editQuestion != null)
//            {
//                editQuestion.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//
//                    }
//                });
//            }
//            if(deleteQuestion != null)
//            {
//                deleteQuestion.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        profileActivity.deleteQuestion(position);
//                    }
//                });
//            }
        }
        catch(Exception|OutOfMemoryError outOfMemoryError)
        {
            //
        }
    }


    @Override
    public int getItemCount() {
        return arrayRankList.size();
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ConstraintLayout mLinearLayout;

        private ViewHolder(ConstraintLayout v) {
            super(v);
            mLinearLayout = v;
        }
    }
}
