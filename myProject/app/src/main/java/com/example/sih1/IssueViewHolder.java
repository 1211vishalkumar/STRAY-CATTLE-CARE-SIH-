package com.example.sih1;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class IssueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView tvIssueName, tvIssueDescription;
    ImageView ivIssueImage;

    ItemClickListener listener;

    public IssueViewHolder(View itemView){
        super(itemView);

        ivIssueImage = itemView.findViewById(R.id.ivIssueImage);
        tvIssueName = itemView.findViewById(R.id.tvIssueName);
        tvIssueDescription = itemView.findViewById(R.id.tvIssueDescription);
    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
