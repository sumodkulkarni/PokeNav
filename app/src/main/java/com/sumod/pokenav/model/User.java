package com.sumod.pokenav.model;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import lombok.Data;


@Data
public class User extends BaseModel {
    String email;
    String displayName;
    String avatarUrl;

    // oAuth tokens
    OAuth google = new OAuth();
    OAuth facebook = new OAuth();


    @Data
    class OAuth {
        String id;
        String token;
    }


    public void populateWithGoogle(GoogleSignInAccount acct) {
        email = acct.getEmail();
        displayName = acct.getDisplayName();
        avatarUrl = acct.getPhotoUrl().toString();

        google.setId(acct.getId());
        google.setToken(acct.getIdToken());
    }
}
