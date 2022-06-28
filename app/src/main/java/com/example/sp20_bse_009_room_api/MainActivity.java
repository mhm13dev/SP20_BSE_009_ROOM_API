package com.example.sp20_bse_009_room_api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private EditText email, name, address;
    private RadioGroup marital_status;
    private Button updateButton, deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-db").build();

        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        marital_status = findViewById(R.id.marital_status);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public void addButtonAction(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.name = name.getText().toString();
                user.email = email.getText().toString();
                user.address = address.getText().toString();
                user.marital_status = marital_status.getCheckedRadioButtonId();

                UserDao userDao = db.userDao();

               try {

                   userDao.insertOne(user);

                   v.post(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(MainActivity.this, "User Added!", Toast.LENGTH_SHORT).show();
                           updateButton.setEnabled(false);
                           deleteButton.setEnabled(false);
                           reset();
                       }
                   });
               } catch (SQLiteConstraintException e) {
                   v.post(new Runnable() {
                       @Override
                       public void run() {
                           if(Objects.requireNonNull(e.getLocalizedMessage()).contains("code 1555 SQLITE_CONSTRAINT_PRIMARYKEY")) {
                               Toast.makeText(MainActivity.this, "User already exist!", Toast.LENGTH_SHORT).show();
                           } else {
                               Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                           }
                           updateButton.setEnabled(false);
                           deleteButton.setEnabled(false);
                       }
                   });
               }
            }
        });
    }

    public void searchButtonAction(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = db.userDao();
                User user = userDao.findByEmail(email.getText().toString());
                if(user != null) {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            email.setText(user.email);
                            name.setText(user.name);
                            address.setText(user.address);
                            marital_status.check(user.marital_status);

                            updateButton.setEnabled(true);
                            deleteButton.setEnabled(true);
                        }
                    });
                } else {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                            updateButton.setEnabled(false);
                            deleteButton.setEnabled(false);
                        }
                    });
                }
            }
        });
    }


    public void updateButtonAction(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = db.userDao();
                User user = new User();
                user.email = email.getText().toString();

                user.name = name.getText().toString();
                user.address = address.getText().toString();
                user.marital_status = marital_status.getCheckedRadioButtonId();

                userDao.updateOne(user);

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "User Updated!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void deleteButtonAction(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = db.userDao();

                User user = new User();
                user.email = email.getText().toString();

                userDao.delete(user);

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "User Deleted!", Toast.LENGTH_SHORT).show();
                        updateButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                        reset();
                    }
                });
            }
        });
    }

    public void resetButtonAction(View v) {
        reset();
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public void reset() {
        email.setText("");
        name.setText("");
        address.setText("");
        marital_status.clearCheck();
    }

}