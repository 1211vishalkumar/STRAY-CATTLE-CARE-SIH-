package com.example.sih1;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NewIssueViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvIssueUserName, tvIssueUserPhone, tvNewIssueState, tvIssueAddSate, tvDateNTime;
    public ImageView ivNewIssueImage;

    ItemClickListener listener;

    public NewIssueViewHolder(View itemView){
        super(itemView);

        ivNewIssueImage = itemView.findViewById(R.id.ivNewIssueImage);
        tvIssueUserName = itemView.findViewById(R.id.tvIssueUserName);
        tvIssueUserPhone = itemView.findViewById(R.id.tvIssueUserPhone);
        tvNewIssueState = itemView.findViewById(R.id.tvNewIssueState);
        tvIssueAddSate = itemView.findViewById(R.id.tvIssueAddSate);
        tvDateNTime = itemView.findViewById(R.id.tvDateNTime);
    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }

}

