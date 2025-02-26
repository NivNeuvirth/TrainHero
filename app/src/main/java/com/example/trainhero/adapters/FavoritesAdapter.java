//package com.example.trainhero.adapters;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.trainhero.models.Exercise;
//
//import java.util.List;
//
//public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {
//
//    private List<Exercise> favoriteList;
//
//    public FavoritesAdapter(List<Exercise> favoriteList) {
//        this.favoriteList = favoriteList;
//    }
//
//    @NonNull
//    @Override
//    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
//        return new FavoriteViewHolder(view);
//    }
//
//
//    @Override
//    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
//        holder.textView.setText(favoriteList.get(position).getName()); // Assuming Exercise has getName()
//    }
//
//    @Override
//    public int getItemCount() {
//        return favoriteList.size();
//    }
//
//    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
//        TextView textView;
//
//        public FavoriteViewHolder(View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(android.R.id.text1);
//        }
//    }
//
//
//}
