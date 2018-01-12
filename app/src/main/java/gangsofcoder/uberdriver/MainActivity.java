package gangsofcoder.uberdriver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import gangsofcoder.uberdriver.model.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {


    private Button btnMainSignIn, btnMainRegister;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference users;
    private RelativeLayout rlMainRoot;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        //init firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        users = mFirebaseDatabse.getReference();

        btnMainSignIn = findViewById(R.id.btnMainSignIn);
        btnMainRegister = findViewById(R.id.btnMainRegister);
        rlMainRoot = findViewById(R.id.rlMainRoot);
        //events
        btnMainRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });
        btnMainSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("LOGIN");
        dialog.setMessage("Please use email to login");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View login_layout = layoutInflater.inflate(R.layout.layout_login, null);

        dialog.setView(login_layout);

        final MaterialEditText etLoginEmail = login_layout.findViewById(R.id.etLoginEmail);
        final MaterialEditText etLoginPassword = login_layout.findViewById(R.id.etLoginPassword);


        dialog.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //validation checking
                if (TextUtils.isEmpty(etLoginEmail.getText().toString())) {
                    Snackbar.make(rlMainRoot, "Please enter email address", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (etLoginPassword.getText().length() < 6) {
                    Snackbar.make(rlMainRoot, "Password too short", Snackbar.LENGTH_LONG).show();
                    return;
                }
                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                //login
                mFirebaseAuth.signInWithEmailAndPassword(etLoginEmail.getText().toString(), etLoginPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(rlMainRoot, "Message " + e.getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View register_layout = layoutInflater.inflate(R.layout.layout_register, null);

        dialog.setView(register_layout);

        final MaterialEditText etRegisterEmail = register_layout.findViewById(R.id.etRegisterEmail);
        final MaterialEditText etRegisterPassword = register_layout.findViewById(R.id.etRegisterPassword);
        final MaterialEditText etRegisterName = register_layout.findViewById(R.id.etRegisterName);
        final MaterialEditText etRegisterPhone = register_layout.findViewById(R.id.etRegisterPhone);

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //validation checking
                if (TextUtils.isEmpty(etRegisterEmail.getText().toString())) {
                    Snackbar.make(rlMainRoot, "Please enter email address", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (etRegisterPassword.getText().length() < 6) {
                    Snackbar.make(rlMainRoot, "Password too short", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(etRegisterName.getText().toString())) {
                    Snackbar.make(rlMainRoot, "Please enter name", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(etRegisterPhone.getText().toString())) {
                    Snackbar.make(rlMainRoot, "Please enter phone number", Snackbar.LENGTH_LONG).show();
                    return;
                }

                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                //Register new user
                mFirebaseAuth.createUserWithEmailAndPassword(etRegisterEmail.getText().toString(), etRegisterPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                //save user to databse
                                User user = new User();
                                user.setEmail(etRegisterEmail.getText().toString());
                                user.setPassword(etRegisterPassword.getText().toString());
                                user.setPhone(etRegisterPhone.getText().toString());
                                user.setName(etRegisterName.getText().toString());

                                users.child(mFirebaseAuth.getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rlMainRoot, "Register Successfully", Snackbar.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rlMainRoot, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rlMainRoot, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }
}
