package com.cong.eventcreater.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;

/**
 * Created by admin on 21/08/2016.
 */
public class AudioRecordingActivity extends Activity {
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String recordingFileName;
    private String playingFileName;
    private RecordButton btRecordButton;
    private PlayButton btPlayButton;

    private class RecordButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartRecording = true;

        public RecordButton(Context context) {
            super(context);
            setText("Start recording");
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onRecord(mStartRecording);
                    } catch (IOException e) {
                        Log.d("AudioRecording", e.toString());
                    }
                    if(mStartRecording) {
                        setText("Stop recording");
                    } else {
                        setText("Start recording");
                    }
                    mStartRecording = !mStartRecording;
                }
            });
        }
    }

    class PlayButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartPlaying = true;

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onPlay(mStartPlaying);
                    } catch (IOException e) {
                        Log.d("AudioRecording", e.toString());
                    }
                    if (mStartPlaying) {
                        setText("Stop playing");
                    } else {
                        setText("Start playing");
                    }
                    mStartPlaying = !mStartPlaying;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        btRecordButton = new RecordButton(this);
        layout.addView(btRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0
                ));
        btPlayButton = new PlayButton(this);
        layout.addView(btPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0
                ));
        setContentView(layout);

        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/"
                + getIntent().getStringExtra("filename")
                + ".3gp";
        this.recordingFileName = fileName;
        this.playingFileName = fileName;
    }

    private void onRecord(boolean start) throws IOException {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void onPlay(boolean start) throws IOException {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }


    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }



    public void startRecording() throws IOException {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFile(recordingFileName);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            throw e;
        }
        mRecorder.start();
    }

    public void startPlaying() throws IOException {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(playingFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("recordingFile", recordingFileName);
        setResult(RESULT_OK, i);
        finish();
    }

    public void setPlayingFileName(String playingFileName) {
        this.playingFileName = playingFileName;
    }
}
