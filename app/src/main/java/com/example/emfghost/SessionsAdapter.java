package com.example.emfghost;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionViewHolder> {

    private List<EMFSession> sessions;
    private OnSessionClickListener clickListener;
    private OnSessionDeleteListener deleteListener;

    public interface OnSessionClickListener {
        void onSessionClick(EMFSession session);
    }

    public interface OnSessionDeleteListener {
        void onSessionDelete(EMFSession session);
    }

    public SessionsAdapter(List<EMFSession> sessions,
                          OnSessionClickListener clickListener,
                          OnSessionDeleteListener deleteListener) {
        this.sessions = sessions;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        EMFSession session = sessions.get(position);
        holder.bind(session, clickListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView sessionTitle;
        TextView sessionDate;
        TextView sessionDuration;
        ImageButton deleteButton;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionTitle = itemView.findViewById(R.id.sessionTitle);
            sessionDate = itemView.findViewById(R.id.sessionDate);
            sessionDuration = itemView.findViewById(R.id.sessionDuration);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(EMFSession session, OnSessionClickListener clickListener, OnSessionDeleteListener deleteListener) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
            String dateStr = dateFormat.format(new Date(session.getStartTime()));

            long durationSeconds = (session.getEndTime() - session.getStartTime()) / 1000;
            long minutes = durationSeconds / 60;
            long seconds = durationSeconds % 60;

            sessionTitle.setText("Session #" + session.getSessionId().substring(session.getSessionId().length() - 6));
            sessionDate.setText(dateStr);
            sessionDuration.setText(String.format(Locale.getDefault(),
                    "Duration: %dm %ds | Readings: %d",
                    minutes, seconds,
                    session.getReadings() != null ? session.getReadings().size() : 0));

            itemView.setOnClickListener(v -> clickListener.onSessionClick(session));
            deleteButton.setOnClickListener(v -> deleteListener.onSessionDelete(session));
        }
    }
}

