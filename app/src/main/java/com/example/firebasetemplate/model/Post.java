package com.example.firebasetemplate.model;

import java.util.HashMap;

public class Post {
    public String postId;
    public String content;
    public String authorName;
    public String date;
    public String imageUrl;
    public String authorImage;

    // Likes als posts
    public HashMap<String, Integer> likes = new HashMap<String, Integer>();

    public HashMap<String, Boolean> favs = new HashMap<String, Boolean>();
}