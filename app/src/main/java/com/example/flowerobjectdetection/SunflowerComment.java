package com.example.flowerobjectdetection;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class SunflowerComment extends AppCompatActivity {

    private EditText editTextComment;
    private Button buttonSubmitComment;
    private RecyclerView recyclerViewComments;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunflower_comment);

        editTextComment = findViewById(R.id.commentInput);
        buttonSubmitComment = findViewById(R.id.btn_submitComment);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setup RecyclerView
        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter((ArrayList<Comment>) commentList);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(commentsAdapter);

        // Load comments from Firestore
        loadComments();

        buttonSubmitComment.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString();
            if (!commentText.isEmpty()) {
                submitComment(commentText);
            } else {
                Toast.makeText(SunflowerComment.this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        db.collection("comments") // Ensure you have a "comments" collection in Firestore
                .orderBy("timestamp", Query.Direction.ASCENDING) // Order by timestamp
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentList.clear(); // Clear previous comments
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            commentList.add(comment);
                        }
                        commentsAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                    } else {
                        Toast.makeText(SunflowerComment.this, "Error loading comments.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitComment(String commentText) {
        if (currentUser != null) {
            Comments newComment = new Comments(currentUser.getUid(), commentText, System.currentTimeMillis());

            db.collection("comments").add(newComment)
                    .addOnSuccessListener(documentReference -> {
                        editTextComment.setText(""); // Clear input field
                        loadComments(); // Reload comments after submission
                    })
                    .addOnFailureListener(e -> Toast.makeText(SunflowerComment.this, "Error submitting comment.", Toast.LENGTH_SHORT).show());
        }
    }
}
