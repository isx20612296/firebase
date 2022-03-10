package com.example.firebasetemplate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentDetailBinding;
import com.example.firebasetemplate.databinding.FragmentNewPostBinding;
import com.example.firebasetemplate.model.Comment;
import com.example.firebasetemplate.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends AppFragment {

    private FragmentDetailBinding binding;
    private List<Comment> llistaComments = new ArrayList<Comment>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentDetailBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String postid = DetailFragmentArgs.fromBundle(getArguments()).getPostid();

        db.collection("posts").document(postid).get().addOnSuccessListener(documentSnapshot -> {
           binding.textDetail.setText(documentSnapshot.getString("content"));
            Glide.with(this).load(documentSnapshot.getString("imageUrl")).into(binding.imageDetail);
        });

        db.collection("posts").document(postid).collection("comentaris").get().addOnSuccessListener(queryDocumentSnapshots -> {
           for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
               Log.d("tag", "Entro");
               Comment comment = new Comment();
               comment.texto = doc.get("texto").toString();
               comment.user = doc.get("user").toString();
               llistaComments.add(comment);
           }
        });

        Log.d("tag", String.valueOf(llistaComments.size()));

    }

    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

        @NonNull
        @Override
        public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}