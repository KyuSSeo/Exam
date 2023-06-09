package com.example.exam;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sos extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSend;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sos);

        databaseRef = FirebaseDatabase.getInstance().getReference("reports");

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSend = findViewById(R.id.send_Button);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String content = editTextContent.getText().toString().trim();

                String reportId = databaseRef.push().getKey();
                Report report = new Report(reportId, title, content);
                databaseRef.child(reportId).setValue(report);

                Toast.makeText(Sos.this, "문제가 신고되었습니다.", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}

