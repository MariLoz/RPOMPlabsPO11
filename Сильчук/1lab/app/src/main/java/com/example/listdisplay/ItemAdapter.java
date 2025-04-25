package com.example.listdisplay;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.nameTextView.setText(item.getLocalizedName());
        holder.attrTextView.setText(holder.itemView.getContext().getString(R.string.attribute_label, item.getPrimaryAttr()));
        holder.attackTypeTextView.setText(holder.itemView.getContext().getString(R.string.attack_type_label, item.getAttackType()));

        String roles = String.join(", ", item.getRoles());
        holder.rolesTextView.setText(holder.itemView.getContext().getString(R.string.roles_label, roles));

        Glide.with(holder.itemView.getContext())
                .load(item.getImg())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.heroImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HeroDetailActivity.class);
            intent.putExtra("hero_name", item.getLocalizedName());
            intent.putExtra("hero_attr", item.getPrimaryAttr());
            intent.putExtra("hero_attack_type", item.getAttackType());
            intent.putExtra("hero_roles", roles);
            intent.putExtra("hero_image", item.getImg());
            intent.putExtra("hero_health", item.getBaseHealth());
            intent.putExtra("hero_mana", item.getBaseMana());
            intent.putExtra("hero_armor", item.getBaseArmor());
            intent.putExtra("hero_move_speed", item.getMoveSpeed());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, attrTextView, attackTypeTextView, rolesTextView;
        ImageView heroImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.titleTextView);
            attrTextView = itemView.findViewById(R.id.attrTextView);
            attackTypeTextView = itemView.findViewById(R.id.attackTypeTextView);
            rolesTextView = itemView.findViewById(R.id.rolesTextView);
            heroImageView = itemView.findViewById(R.id.heroImageView);
        }
    }
}