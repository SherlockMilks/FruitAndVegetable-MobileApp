package com.example.fruitandvegetableshop.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fruitandvegetableshop.Model.User;
import com.example.fruitandvegetableshop.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mFirestore;

    EditText username;
    EditText password;
    EditText passwordAgain;
    EditText phoneNumber;
    EditText address;
    EditText email;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordAgain = findViewById(R.id.passwordagain);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.postalAddress);
        email = findViewById(R.id.email);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Helymeghatározási engedély szükséges!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            String userAddress = "Lat: " + latitude + ", Long: " + longitude;
                            address.setText(userAddress);
                        }
                    }
                });
    }

    public void register(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String passwordAgain = this.passwordAgain.getText().toString();
        String phoneNumber = this.phoneNumber.getText().toString();
        String address = this.address.getText().toString();
        String email = this.email.getText().toString();

        if (username.isBlank() || email.isBlank() || password.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
            Log.d(LOG_TAG, "Hiányzó adat!");
            Toast.makeText(RegistrationActivity.this, "Hiányzó adat!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Log.d(LOG_TAG, "Nem megegyező jelszó páros!");
            Toast.makeText(RegistrationActivity.this, "Nem megegyező jelszó páros!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(LOG_TAG, username + " " + email + " " + phoneNumber + " " + address);
        User user = new User(username, email, phoneNumber, address, false);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mFirestore.collection("users").add(user);
                    Log.i(LOG_TAG, "Sikeres regisztráció");
                    login(view);
                } else {
                    Log.i(LOG_TAG, "Sikertelen regisztáció");
                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void login(View view) {
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button);
        view.startAnimation(bounce);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
