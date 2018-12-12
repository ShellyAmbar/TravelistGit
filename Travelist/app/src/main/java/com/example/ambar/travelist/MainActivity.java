package com.example.ambar.travelist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {



     LoginButton loginButton;
     CallbackManager  callbackManager;
     TextView LogInTextView;
     TextView BirthDayText;
     TextView EmailText;
     TextView FriendsText;
     ProgressDialog mDialog;
     ImageView ImageAvatar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        callbackManager=CallbackManager.Factory.create();

        LogInTextView=(TextView) findViewById(R.id.LoginTextview) ;
        BirthDayText=(TextView) findViewById(R.id.BirthText);
        EmailText=(TextView) findViewById(R.id.EmailText) ;
        FriendsText=(TextView) findViewById(R.id.Friends);
        ImageAvatar=(ImageView) findViewById(R.id.ImageView);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday","user_friends"));







        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogInTextView.setText("You Loged in Seccessfully.\n");
                mDialog= new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Retrieving Data..");
                mDialog.show();
                String AccessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                    mDialog.dismiss();

                   Log.d("response", response.toString());
                   getData(object);
                    }
                });

                Bundle Parameters = new Bundle();
                Parameters.putString("fields","email,birthday,friends");
                request.setParameters(Parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
               LogInTextView.setText("Login Canceled successfully.");// App code
            }

            @Override
            public void onError(FacebookException exception) {
                LogInTextView.setText(exception.getMessage()); // App code
            }
        });




    }

    private void getData(JSONObject object) {
        try {
            URL Profile_picture = new URL ("https://graph.facebook.com/" +  object.getString("id") + "/picture?width=250&height=250");
            Picasso.get().load(Profile_picture.toString()).into(ImageAvatar);
            EmailText.setText("Your Email is: " + object.getString("email" ));
            BirthDayText.setText("Your birthday is :"+ object.getString("birthday"));
            FriendsText.setText("Your Total Number Of Friends is:" + object.getJSONObject("friends").getJSONObject("summary").getString("total_count"));


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
