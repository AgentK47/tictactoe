package com.agentk.tictactoe.users;

/**
 * Created by AgentK on 13/02/2018.
 */

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.agentk.tictactoe.R;
import com.agentk.tictactoe.model.User;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Holder> {
    private Context context;
    private List<User> users;
    private String grid;

    public Adapter(Context context, List<User> users,String grid) {
        this.context = context;
        this.users = users;
        this.grid=grid;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.user_list_item,
                parent,
                false);

        return new Holder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        User user = users.get(position);
        user.setGrid(grid);
        holder.binding.setUser(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
