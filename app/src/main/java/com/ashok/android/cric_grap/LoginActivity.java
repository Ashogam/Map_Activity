package com.ashok.android.cric_grap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sqlite.cric_grap.SQLiteManagement;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private static String email, username, password;
    public String text;
    private TextView txtForgotPassowrd;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView, mRegEmail;
    private EditText mPasswordView, mRegName, mRegPass, mregConfirmPass;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        txtForgotPassowrd = (TextView) findViewById(R.id.txtForgotPassowrd);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        txtForgotPassowrd.setOnClickListener(new OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View v) {

                try {
                    LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
                    View promptView = layoutInflater.inflate(R.layout.register_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            LoginActivity.this, R.style.CustomDialog);
                    alertDialogBuilder.setView(promptView);
                    mRegEmail = (AutoCompleteTextView) promptView.findViewById(R.id.ACTemail);
                    mRegName = (EditText) promptView.findViewById(R.id.edtUserName);
                    mRegPass = (EditText) promptView.findViewById(R.id.EdtPassword);
                    mregConfirmPass = (EditText) promptView.findViewById(R.id.EdtConfirmPassword);

                    alertDialogBuilder.setCancelable(false).setTitle("Register").setIcon(android.R.drawable.ic_dialog_alert).
                            setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (TextUtils.isEmpty(mRegEmail.getText().toString()) || !isEmailValid(mRegEmail.getText().toString())) {
                                                Toast.makeText(LoginActivity.this, "Registration Failed! Ensure the Email ID is tobe correct!", Toast.LENGTH_SHORT).show();


                                            } else {
                                                email = mRegEmail.getText().toString();
                                                //Toast.makeText(LoginActivity.this, email, Toast.LENGTH_SHORT).show();
                                                if (TextUtils.isEmpty(mRegName.getText().toString())) {
                                                    Toast.makeText(LoginActivity.this, "Registration Failed! Ensure the Name is not empty!", Toast.LENGTH_SHORT).show();
                                                } else {

                                                    username = mRegName.getText().toString();
                                                    //Toast.makeText(LoginActivity.this, username, Toast.LENGTH_SHORT).show();
                                                }
                                                Log.d("Validate", !isPasswordValid(mRegPass.getText().toString()) == true ? "true" : "false");
                                                if (!TextUtils.isEmpty(mRegPass.getText().toString()) && !isPasswordValid(mRegPass.getText().toString())) {
                                                    Toast.makeText(LoginActivity.this, "Registration Failed! Ensure the Password is atleast 6 Character!", Toast.LENGTH_SHORT).show();
                                                } else if (mRegPass.getText().toString().equals(mregConfirmPass.getText().toString())) {
                                                    password = mRegPass.getText().toString();
                                                    //Toast.makeText(LoginActivity.this, password, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Password Dismatched", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            if (TextUtils.isEmpty(password)) {
                                                count++;
                                            } else if (TextUtils.isEmpty(email)) {
                                                count++;

                                            } else if (TextUtils.isEmpty(username)) {
                                                count++;

                                            }
                                            if (count == 0) {
                                                Log.d("Result", username + password + email);
                                                new Register().execute(username, email,password);
                                            }
                                        }
                                    }

                            ).

                            setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }

                            );
                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();


                } catch (IllegalArgumentException illegalArgumentException) {
                    illegalArgumentException.printStackTrace();
                }

            }
        });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4 ? true : false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class Register extends AsyncTask<String, Void, Integer> {
        SQLiteManagement sqLiteManagement = null;
        private ProgressDialog myProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myProgressDialog = new ProgressDialog(
                    LoginActivity.this);
            myProgressDialog.setMessage("Please wait.....");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();

        }

        @Override
        protected Integer doInBackground(String... params) {
            sqLiteManagement = new SQLiteManagement(LoginActivity.this);
            sqLiteManagement.open();
            try {
                Thread.sleep(2000);
                Log.d("Parameters ", params[0] + params[1] + params[2]);

                long result = sqLiteManagement.registration(params[0], params[1], params[2]);
                if (result == 1) {
                    return 1;
                } else {
                    return 0;
                }
            } catch (Exception e) {

            } finally {
                if (sqLiteManagement != null) {
                    try {
                        sqLiteManagement.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            myProgressDialog.dismiss();
            if (aVoid == 1) {
                Toast.makeText(LoginActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Database Error. registration unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            SQLiteManagement sqLiteManagement = new SQLiteManagement(getApplicationContext());
            sqLiteManagement.open();


            try {
                // Simulate network access.
                Thread.sleep(2000);
                Log.d("Email and Passowrd",mEmail+" "+mPassword);
                sqLiteManagement.userLoginCheck(mEmail,mPassword);
            } catch (Exception e) {
                return false;
            }
            sqLiteManagement.close();

           /* for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

