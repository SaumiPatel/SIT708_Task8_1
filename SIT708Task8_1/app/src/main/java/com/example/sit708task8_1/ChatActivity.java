package com.example.sit708task8_1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");
        recyclerView = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, username);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Welcome message
        messageList.add(new Message("Welcome User!", false));
        chatAdapter.notifyItemInserted(messageList.size() - 1);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editTextMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    messageList.add(new Message(msg, true));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    editTextMessage.setText("");
                    // Call chatbot API here
                    ChatbotApi.sendMessage(msg, username, new ChatbotApi.Callback() {
                        @Override
                        public void onResponse(String response) {
                            runOnUiThread(() -> {
                                messageList.add(new Message(response, false));
                                chatAdapter.notifyItemInserted(messageList.size() - 1);
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            });
                        }
                    });
                }
            }
        });
    }
} 