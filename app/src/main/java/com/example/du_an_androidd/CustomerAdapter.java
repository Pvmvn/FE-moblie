package com.example.du_an_androidd;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.du_an_androidd.model.response.Member;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Member> memberList;
    private OnCustomerClickListener listener;

    public interface OnCustomerClickListener {
        void onCustomerClick(Member member);
    }

    public CustomerAdapter(OnCustomerClickListener listener) {
        this.memberList = new ArrayList<>();
        this.listener = listener;
    }

    public void setData(List<Member> list) {
        this.memberList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Member member = memberList.get(position);
        if (member == null) return;

        // Member name
        holder.tvMemberName.setText(member.getFullName() != null ? member.getFullName() : "");

        // Email
        holder.tvEmail.setText(member.getEmail() != null ? member.getEmail() : "");

        // Status
        boolean isActive = member.isActive();
        if (isActive) {
            holder.tvStatusLabel.setText("Hoạt động");
            holder.tvStatusLabel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#10B981")));
            holder.viewStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#10B981")));
        } else {
            // TODO: Determine if expired or locked based on additional fields
            holder.tvStatusLabel.setText("Hết hạn");
            holder.tvStatusLabel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F59E0B")));
            holder.viewStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F59E0B")));
        }

        // Avatar - using default icon for now
        holder.imgAvatar.setImageResource(R.drawable.ic_person);

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCustomerClick(member);
        });
    }

    @Override
    public int getItemCount() {
        return memberList != null ? memberList.size() : 0;
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        View viewStatusDot;
        TextView tvMemberName;
        TextView tvEmail;
        TextView tvStatusLabel;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            viewStatusDot = itemView.findViewById(R.id.viewStatusDot);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvStatusLabel = itemView.findViewById(R.id.tvStatusLabel);
        }
    }
}

