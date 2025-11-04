package com.example.emfghost;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecordsActivity extends AppCompatActivity {

    private RecyclerView sessionsRecyclerView;
    private TextView emptyStateText;
    private SessionsAdapter adapter;
    private List<EMFSession> sessions;
    private AppDatabase database;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        sessionsRecyclerView = findViewById(R.id.sessionsRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);

        database = AppDatabase.getDatabase(this);
        sessions = new ArrayList<>();
        adapter = new SessionsAdapter(sessions,
            // Click listener - open session details
            session -> {
                Intent intent = new Intent(RecordsActivity.this, SessionDetailActivity.class);
                intent.putExtra("sessionId", session.getSessionId());
                startActivity(intent);
            },
            // Delete listener - show confirmation dialog
            session -> {
                showDeleteConfirmationDialog(session);
            }
        );

        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionsRecyclerView.setAdapter(adapter);

        loadSessions();
    }

    private void loadSessions() {
        executor.execute(() -> {
            List<EMFSessionEntity> sessionEntities = database.sessionDao().getAllSessions();
            List<EMFSession> loadedSessions = new ArrayList<>();

            for (EMFSessionEntity entity : sessionEntities) {
                EMFSession session = new EMFSession(
                        entity.sessionId,
                        entity.startTime,
                        entity.endTime,
                        entity.readings
                );
                loadedSessions.add(session);
            }

            runOnUiThread(() -> {
                sessions.clear();
                sessions.addAll(loadedSessions);
                adapter.notifyDataSetChanged();

                if (sessions.isEmpty()) {
                    emptyStateText.setVisibility(View.VISIBLE);
                    sessionsRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateText.setVisibility(View.GONE);
                    sessionsRecyclerView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void showDeleteConfirmationDialog(EMFSession session) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Session?")
            .setMessage("Are you sure you want to delete this recording session? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> deleteSession(session))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteSession(EMFSession session) {
        executor.execute(() -> {
            // Delete from database
            database.sessionDao().deleteSessionById(session.getSessionId());

            runOnUiThread(() -> {
                // Remove from list and update UI
                sessions.remove(session);
                adapter.notifyDataSetChanged();

                if (sessions.isEmpty()) {
                    emptyStateText.setVisibility(View.VISIBLE);
                    sessionsRecyclerView.setVisibility(View.GONE);
                }

                Toast.makeText(RecordsActivity.this, "Session deleted", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
