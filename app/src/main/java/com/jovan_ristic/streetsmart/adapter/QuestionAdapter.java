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
import com.jovan_ristic.streetsmart.activity.ProfileActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>
{

    private Context mContext;
    private List<Question> arrayQuestion;
    private ProfileActivity profileActivity;

    public QuestionAdapter(Context context, List<Question> songs, ProfileActivity tPa)
    {
        mContext = context;
        arrayQuestion = songs;
        profileActivity = tPa;
    }

    public void setQuestions(List<Question> q)
    {
        this.arrayQuestion = q;
        notifyDataSetChanged();
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return  new QuestionAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(QuestionAdapter.ViewHolder holder, final int position)
    {
        TextView textQuestion = (TextView)holder.mLinearLayout.findViewById(R.id.titleQuestion);
        ImageView editQuestion = holder.mLinearLayout.findViewById(R.id.editQuestionBtn);
        ImageView deleteQuestion = holder.mLinearLayout.findViewById(R.id.deleteQuestionBtn);
        String title = arrayQuestion.get(position).getHeaderQ();
        textQuestion.setText(title);
        try {
            if (editQuestion != null)
            {
                editQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
            }
            if(deleteQuestion != null)
            {
                deleteQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        profileActivity.deleteQuestion(position);
                    }
                });
            }
        }
        catch(Exception|OutOfMemoryError outOfMemoryError)
        {
            //
        }
    }


    @Override
    public int getItemCount() {
        return arrayQuestion.size();
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
