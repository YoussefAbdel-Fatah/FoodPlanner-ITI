package com.example.foodplanner.data.network;

import com.google.firebase.auth.FirebaseAuth;
import io.reactivex.rxjava3.core.Completable;

public class FirebaseDelegate {
    private FirebaseAuth mAuth;

    public FirebaseDelegate() {
        mAuth = FirebaseAuth.getInstance();
    }

    public Completable registerUser(String email, String password) {
        return Completable.create(emitter -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(e -> emitter.onError(e));
        });
    }
}