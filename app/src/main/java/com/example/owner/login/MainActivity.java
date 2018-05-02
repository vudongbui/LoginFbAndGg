package com.example.owner.login;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";

    CallbackManager callbackManager;
    LoginButton mLoginButton;
    TextView tvName;
    ImageView ivAvatar;
    ProgressDialog mDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        callbackManager = CallbackManager.Factory.create();
        mLoginButton = findViewById(R.id.login_button);
        tvName = findViewById(R.id.tv_fblogin);
        ivAvatar = findViewById(R.id.iv_fb);

        mLoginButton.setReadPermissions(Arrays.asList("public_profile"));
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("loading...");
                mDialog.show();

                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        try {
                            String nameFb = object.getString("name");
                            tvName.setText(nameFb);
                            String idFb = object.getString("id");
                            Picasso.get().load("https://graph.facebook.com/" + idFb + "/picture?type=large")
                                    .resize(100,100)
                                    .centerCrop()
                                    .into(ivAvatar);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });

//                Bundle parameters = new Bundle();
//                parameters.putString();
//                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.owner.login",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }
//    private void getInfor(){
//        try{
//            URL Avatar = new URL("https://graph.facebook.com/"+obj)
//        }
//    }

}
