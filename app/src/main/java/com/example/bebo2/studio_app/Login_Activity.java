package com.example.bebo2.studio_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText eMail,pAssword;
    private Button signin;
    private ProgressDialog Loginbar;
    private Spinner spin_type_login;
    private String type_chacker;
    private String type;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG="LoginActivity";
    private Button google_sign_in_btn;
    private TextView link_reset_pass;
    private DatabaseReference userref,photoref;

    private String DEFAULT="DN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();


        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        photoref = FirebaseDatabase.getInstance().getReference().child("Photographer");


        TextView link_text2 = (TextView)findViewById(R.id.text_link2);
       eMail = (EditText)findViewById(R.id.edt_email);
        pAssword = (EditText)findViewById(R.id.edt_pass);
        signin = (Button)findViewById(R.id.btn_con);
        google_sign_in_btn = (Button)findViewById(R.id.google_sign_in);
        link_reset_pass =(TextView)findViewById(R.id.edit_reset);

        link_reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_reset_activity = new Intent(getApplicationContext(),ResetpasswordActivity.class);
                startActivity(to_reset_activity);
            }
        });






        final ImageView icon = (ImageView)findViewById(R.id.icon);
        final ArrayList<Listitem> listitems=new ArrayList<Listitem>();
        listitems.add(new Listitem(R.string.select_type_account));
        listitems.add(new Listitem( R.string.user));
        listitems.add(new Listitem(R.string.photographer_or_studio));
        listitems.add(new Listitem(R.string.admin));


        final MyCustomeAdapter myCustomeAdapter3= new MyCustomeAdapter(listitems);
        final Spinner spin_type =(Spinner)findViewById(R.id.type_login);
        spin_type.setAdapter(myCustomeAdapter3);


        spin_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    type = "user";
                    type_chacker = "user";
                    icon.setBackgroundResource(R.drawable.man);

                    SharedPreferences sharedPreferences=getSharedPreferences("Data",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("kind_person",type);
                    editor.commit();


                }
                if(position==2)
                {
                    type = "photographer";
                    type_chacker = "photographer";
                    icon.setBackgroundResource(R.drawable.photograf);

                    SharedPreferences sharedPreferences=getSharedPreferences("Data",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("kind_person",type);
                    editor.commit();


                }
                if (position == 3)
                {

                    type = "admin";
                    type_chacker = "admin";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });










        link_text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemListDialogFragment_register bottomSheet = new ItemListDialogFragment_register();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("admin")) {
                    if (TextUtils.isEmpty(eMail.getText().toString()) || TextUtils.isEmpty(eMail.getText()))
                    {
                        Toast.makeText(Login_Activity.this, "please enter email and password and try again", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        if(eMail.getText().toString().equals("studioapp122@gmail.com")&&pAssword.getText().toString().equals("Mm1234567890;"))
                        {
                            startActivity(new Intent(getApplicationContext(),Admin_activity.class));

                        }else
                            {
                                Toast.makeText(Login_Activity.this,"please make sure your Email or password correct!",Toast.LENGTH_LONG).show();
                            }
                    }
                }else
                {
                    LoginuserAccount(eMail.getText().toString(),pAssword.getText().toString());

                }
            }
        });

        google_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(type_chacker))
                {
                    Toast.makeText(Login_Activity.this, R.string.select_type_account, Toast.LENGTH_SHORT).show();
                }else {
                    signIn();
                }
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(Login_Activity.this, "connection to google sign in failed ..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        Loginbar=new ProgressDialog(this);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Loginbar.setTitle(R.string.btn_login_google);
            Loginbar.show();
            Loginbar.setCanceledOnTouchOutside(true);


            GoogleSignInResult result =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "please wait while , getting your auth account.. ", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "can't get Auth result", Toast.LENGTH_SHORT).show();
                Loginbar.dismiss();
            }


        }
    }
    private void signIn()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("Data",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("kind_person",type);
        editor.commit();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");


                            Intent mainIntent = new Intent(getApplicationContext(), SetupActivity_users.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                            Loginbar.dismiss();


                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().getMessage();
                            Toast.makeText(getBaseContext(), "error occurred" + message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                            startActivity(intent);
                            Loginbar.dismiss();

                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currnetuser=mAuth.getCurrentUser();
        if(currnetuser!=null)
        {

            final coustom_dialog_two loding_bar = new coustom_dialog_two();
            loding_bar.showDialog(Login_Activity.this,R.string.login_the_account);

            SharedPreferences sharedPreferences_ret = getSharedPreferences("Data", Context.MODE_PRIVATE);
            type = sharedPreferences_ret.getString("kind_person", DEFAULT);
            if (type.equals(DEFAULT))
            {
                Toast.makeText(Login_Activity.this, "not found", Toast.LENGTH_SHORT).show();
            }

            if(type.equals("user"))
            {
                final String current_user_id = mAuth.getCurrentUser().getUid();
                userref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(current_user_id))
                        {

                            loding_bar.des(Login_Activity.this);
                            Intent to_main_activity = new Intent(getApplicationContext(),MainActivity.class);
                            to_main_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(to_main_activity);

                        }else
                        {
                            loding_bar.des(Login_Activity.this);
                            Intent to_main_activity = new Intent(getApplicationContext(),SetupActivity_users.class);
                            to_main_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(to_main_activity);
                            Loginbar.dismiss();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }else if(type.equals("photographer"))
            {
                final String current_user_id = mAuth.getCurrentUser().getUid();

                photoref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(current_user_id))
                        {

                            Intent to_main_activity = new Intent(getApplicationContext(),MainActivity.class);
                            to_main_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(to_main_activity);
                            Loginbar.dismiss();

                        }else
                        {
                            Intent to_main_activity = new Intent(getApplicationContext(),SetupActivity_users.class);
                            to_main_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(to_main_activity);
                            Loginbar.dismiss();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else
                {
                    loding_bar.des(this);
                    Toast.makeText(this, "select type account", Toast.LENGTH_SHORT).show();
                }
        }

    }




    public void LoginuserAccount(String email, String password)
    {

        if(TextUtils.isEmpty(email))
        {

            Toast.makeText(getBaseContext(),R.string.please_enter_you_Email, Toast.LENGTH_LONG).show();

        }
        if(TextUtils.isEmpty(password))
        {

            Toast.makeText(getBaseContext(),R.string.please_enter_your_pass, Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(type_chacker))
        {
            Toast.makeText(this, R.string.select_type_account, Toast.LENGTH_LONG).show();
        }else
        {
            Loginbar.setTitle(R.string.login_the_account);
            Loginbar.show();

            SharedPreferences sharedPreferences=getSharedPreferences("Data",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("kind_person",type);
            editor.commit();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            if (task.isSuccessful())
                            {
                                    Intent mainIntent = new Intent(getApplicationContext(), SetupActivity_users.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                            }
                            else
                            {
                                String messgae=task.toString();
                                Toast.makeText(getBaseContext(),R.string.make_sure+messgae, Toast.LENGTH_LONG).show();
                            }
                            Loginbar.dismiss();
                        }
                    });

        }
    }



    public class MyCustomeAdapter extends BaseAdapter {

        ArrayList<Listitem> listitems=new ArrayList<Listitem>();

        public MyCustomeAdapter( ArrayList<Listitem> items){
            this.listitems=items;
        }

        @Override
        public int getCount() {
            return listitems.size();
        }

        @Override
        public Object getItem(int i) {
            return listitems.get(i).name;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater=getLayoutInflater();
            View view3=layoutInflater.inflate(R.layout.row_lay,null);
            final TextView textView=(TextView)view3.findViewById(R.id.textname);

            textView.setText(listitems.get(i).name);

            return view3;
        }
    }

}
