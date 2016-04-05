package com.example.yuriy.simpleclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText editText;
    private TextView dialog;

    private Socket sock;
    private BufferedReader reader;
    private PrintWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog     = (TextView)findViewById(R.id.dialog);
        sendButton = (Button)findViewById(R.id.sendButton);
        editText   = (EditText)findViewById(R.id.editText);

        dialog.setMovementMethod(new ScrollingMovementMethod());

        Client client = Client.instance;
        client.setActivity(this);
        client.setTextView(dialog);
        client.start();

        sendButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String text = editText.getText().toString();
                editText.setText("");
                try {
                    writer.println(text);
                    writer.flush();
                    dialog.append("\nClient: " + text);
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Menu.FIRST + 1, Menu.NONE, "Prefs");
        menu.add(1, Menu.FIRST + 2, Menu.NONE, "Connect");

        return true;
    }

    static final private int MENU_PREFERENCES = Menu.FIRST + 1;
    static final private int MENU_CONNECT = Menu.FIRST + 2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (MENU_PREFERENCES): {
                Intent i = new Intent(this, MyPreferenceActivity.class);
                startActivityForResult(i, 1);
                return true;
            }
            case (MENU_CONNECT): {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                String ipaddress = prefs.getString(MyPreferenceActivity.IPCONFIG, "192.168.1.107");
                String tcpPort = prefs.getString(MyPreferenceActivity.TCPCONFIG, "8080");

                dialog.append("\nTrying to connect to " + ipaddress + ':' + tcpPort);
                Client.IncomingReader.runningInstance.interrupt();
                Client.instance.start();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setSock(Socket sock) { this.sock = sock; }

    public void setWriter(PrintWriter writer) { this.writer = writer; }

    public void setReader(BufferedReader reader) { this.reader = reader; }


}
