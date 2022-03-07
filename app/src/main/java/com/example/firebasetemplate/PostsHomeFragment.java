package com.example.firebasetemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentPostsBinding;
import com.example.firebasetemplate.databinding.ViewholderPostBinding;
import com.example.firebasetemplate.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PostsHomeFragment extends AppFragment {

    private FragmentPostsBinding binding;
    private List<Post> postsList = new ArrayList<>();
    private PostsAdapter adapter;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPostsBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fab.setOnClickListener(v -> navController.navigate(R.id.newPostFragment));
        binding.postsRecyclerView.setAdapter(adapter = new PostsAdapter());
        setQuery().addSnapshotListener((collectionSnapshot, e) -> {
            postsList.clear();
            for (DocumentSnapshot documentSnapshot: collectionSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                post.postId = documentSnapshot.getId();
                postsList.add(post);
            }
            adapter.notifyDataSetChanged();
        });

    }

    Query setQuery(){
        return db.collection("posts");
    }

    class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ViewholderPostBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Post post = postsList.get(position);
            holder.binding.contenido.setText(post.content);
            holder.binding.autor.setText(post.authorName);
            Glide.with(requireContext()).load(post.imageUrl).into(holder.binding.imagen);

            holder.binding.favorito.setOnClickListener(v -> {
                db.collection("posts").document(post.postId).update("favs." + auth.getUid(), !post.favs.containsKey(auth.getUid()) ? true : FieldValue.delete());
            });

            db.collection("posts").document(post.postId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.get("likes." + auth.getUid()) != null) {
                    holder.binding.imageLike.setChecked(documentSnapshot.get("likes." + auth.getUid()).toString().equals("1"));
                }
            });

            db.collection("posts").document(post.postId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.get("likes." + auth.getUid()) != null) {
                    holder.binding.imageDislike.setChecked(documentSnapshot.get("likes." + auth.getUid()).toString().equals("-1"));
                }
            });


            holder.binding.favorito.setChecked(post.favs.containsKey(auth.getUid()));
            Glide.with(requireContext()).load(post.authorImage).into(holder.binding.autorFoto);
            db.collection("posts").document(post.postId).get().addOnSuccessListener(documentSnapshot -> {
                long sumaLikes = 0;

                HashMap<String, Long> mapLike = (HashMap<String, Long>) documentSnapshot.get("likes");
                Set keySet = mapLike.keySet();
                Iterator it = keySet.iterator();
                while(it.hasNext()){
                    String key = (String) it.next();
                    sumaLikes += mapLike.get(key);
                }

                holder.binding.textLikesTotal.setText(getFormattedSumaLikes(sumaLikes));

            });
            holder.binding.imageLike.setOnClickListener(view -> {
                afegirLike(post);

            });
            holder.binding.imageDislike.setOnClickListener(view -> {
                eliminarLike(post);

            });
        }

        private String getFormattedSumaLikes(long sumaLikes) {
            if (sumaLikes >= 1000000){
                return (double) sumaLikes / 1000000 + "M";
            } else if (sumaLikes >= 1000){
                return (double) sumaLikes / 1000 + "k";
            } else {
                return String.valueOf(sumaLikes);
            }
        }

        private void afegirLike(Post post) {
            db.collection("posts").document(post.postId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains("likes." + auth.getUid())){
                    switch (documentSnapshot.get("likes." + auth.getUid()).toString()){
                        case "-1":
                        case "0":
                            db.collection("posts").document(post.postId).update("likes." + auth.getUid(), 1);
                            break;
                        case "1":
                            db.collection("posts").document(post.postId).update("likes." + auth.getUid(), 0);
                            break;
                    }
                } else {
                    db.collection("posts").document(post.postId).update("likes." + auth.getUid(), 1);
                }
            });
        }

        private void eliminarLike(Post post) {
            db.collection("posts").document(post.postId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains("likes." + auth.getUid())){
                    switch (documentSnapshot.get("likes." + auth.getUid()).toString()){
                        case "1":
                        case "0":
                            db.collection("posts").document(post.postId).update("likes." + auth.getUid(), -1);
                            break;
                        case "-1":
                            db.collection("posts").document(post.postId).update("likes." + auth.getUid(), 0);
                            break;
                    }
                } else {
                    db.collection("posts").document(post.postId).update("likes." + auth.getUid(), -1);
                }
            });
        }

        @Override
        public int getItemCount() {
            return postsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewholderPostBinding binding;
            public ViewHolder(ViewholderPostBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}