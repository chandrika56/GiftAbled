package com.example.android.mark1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import static android.system.Os.remove;
import static java.util.Collections.addAll;

public class ViewActivity extends AppCompatActivity {

    DatabaseReference mRef, rootRef,demoRef,cRef;
ListView mListView;

    private ArrayList<String> keysList = new ArrayList<>();
private ArrayList<String>mUsername=new ArrayList<> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_view);
        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Users");
        mListView=findViewById (R.id.lv);
        mRef=rootRef.child ("deleted");
        cRef=rootRef.child ("Completed");
         final ArrayAdapter<String>arrayAdapter=new ArrayAdapter<> (this,android.R.layout.simple_list_item_1,mUsername);
mListView.setAdapter (arrayAdapter);
mListView.setOnItemClickListener (new AdapterView.OnItemClickListener ( ) {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewActivity.this);
        alertDialog.setTitle("Confirm Delete...");
        alertDialog.setMessage("Are you sure you want delete this?");
        alertDialog.setIcon(R.drawable.ic_cancel_black_24dp);
        alertDialog.setPositiveButton("MODIFY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                showInputBox(mUsername.get(position),position);
               // startActivity (new Intent (ViewActivity.this,LoginActivity.class));             // Write your code here to invoke YES event
                Toast.makeText(getApplicationContext(), "CHANGES HAVE BEEN MADE!", Toast.LENGTH_SHORT).show();
            }
            public void showInputBox(String oldItem, final int index){
                final Dialog dialog=new Dialog(ViewActivity.this);
                dialog.setTitle("Input Box");
                dialog.setContentView(R.layout.input_box);
                TextView txtMessage=(TextView)dialog.findViewById(R.id.txtmessage);
                txtMessage.setText("Update item");
                txtMessage.setTextColor(Color.parseColor("#ff2222"));
                final EditText editText=(EditText)dialog.findViewById(R.id.txtinput);
                editText.setText(oldItem);
                Button bt=(Button)dialog.findViewById(R.id.btdone);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUsername.set(index,editText.getText().toString());
                        arrayAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });



        alertDialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,	int which) {

                mRef.push ().setValue (mUsername.get(position).toString ());

                String key = keysList.get(position);
                demoRef.child(key).removeValue();
                Toast.makeText(getApplicationContext(), "Your request has been deleted!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.setNeutralButton ("COMPLETED", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cRef.push ().setValue (mUsername.get (position).toString ());
                startActivity (new Intent (ViewActivity.this,CompletedActivity.class));

            }
        });

         alertDialog.show();
    }
});


demoRef.addChildEventListener (new ChildEventListener ( ) {
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        String string = dataSnapshot.getValue(String.class);

    mUsername.add(string);
        keysList.add(dataSnapshot.getKey());

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        // whenever data at this location is updated.
       // mUsername.clear ();
        mUsername.add(dataSnapshot.getValue(String.class));

arrayAdapter.notifyDataSetChanged ();

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        String string = dataSnapshot.getValue(String.class);

        mUsername.remove(string);
        keysList.remove(dataSnapshot.getKey());

        arrayAdapter.notifyDataSetChanged();
       // startActivity (new Intent (ViewActivity.this,CancelledActivity.class));
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
    }


}

