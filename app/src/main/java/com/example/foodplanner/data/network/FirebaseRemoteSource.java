package com.example.foodplanner.data.network;

import com.google.firebase.auth.FirebaseAuth;
import io.reactivex.rxjava3.core.Completable;

public class FirebaseRemoteSource {
    private FirebaseAuth mAuth;

    public FirebaseRemoteSource() {
        mAuth = FirebaseAuth.getInstance();
    }

    public Completable signUp(String email, String password) {
        return Completable.create(emitter -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onComplete(); // Success!
                        } else {
                            emitter.onError(task.getException()); // Failure
                        }
                    });
        });
    }

    // Inside FirebaseRemoteSource.java

    public Completable signIn(String email, String password) {
        return Completable.create(emitter -> {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onComplete();
                        } else {
                            if (task.getException() != null) {
                                emitter.onError(task.getException());
                            } else {
                                emitter.onError(new Exception("Login failed"));
                            }
                        }
                    });
        });
    }
}