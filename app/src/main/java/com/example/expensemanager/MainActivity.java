package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Button logout;

    RecyclerView recyclerView;
    ExpenseAdapter adapter;
    ArrayList<Expense> list;
    TextInputLayout exp,amount;
    TextView total;
    Button add;
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();
        exp = findViewById(R.id.expense);
        amount = findViewById(R.id.amount);
        add = findViewById(R.id.addbutton);
        total = findViewById(R.id.totalamount);

        adapter = new ExpenseAdapter(this,list);
        LinearLayoutManager llm = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        if(user == null && account == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int totalAmount = 0;
                    String ex = exp.getEditText().getText().toString();
                    int a = Integer.parseInt(amount.getEditText().getText().toString());
                    list.add(new Expense(ex,a));
                    adapter.notifyDataSetChanged();
                    exp.getEditText().setText("");
                    amount.getEditText().setText("");

                    for(int i = 0;i <list.size();i++){

                        totalAmount += list.get(i).getExpenseAmount();
                        total.setText("Total Lei "+totalAmount);
                    }

                }
            });



        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(account==null) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), Login.class);

                    startActivity(intent);
                    finish();
                } else{
                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });


    }
}