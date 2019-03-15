package com.devstudiosng.yg.smooop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devstudiosng.yg.smooop.R;

import java.security.acl.Permission;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.devstudiosng.yg.smooop.model.EppContact;

public class ContactActivity extends Activity {

    private static final int MY_PERMISSIONS_READ_CONTACTS = 123321;
    private final String TAG = this.getClass().getSimpleName();
    private final int PICK_CONTACT = 1602;
    ImageView mContactBtn;
    TextView mDesc, mTitleLabel;
    Button introBtn;

    Realm mRealm;

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
//            android.Manifest.permission.SEND_SMS
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_contacts);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        RealmResults<EppContact> eppContacts = mRealm.where(EppContact.class).findAll();

        introBtn = findViewById(R.id.intro_button);
        if (eppContacts == null || eppContacts.size() == 0) {
//            introBtn.setEnabled(false);
        }

        mContactBtn = findViewById(R.id.contact_button);
        mDesc = findViewById(R.id.desc);
        mTitleLabel = findViewById(R.id.title_label);

        Typeface pacificoFont = Typeface.createFromAsset(getAssets(),"fonts/pacifico.ttf");
        mTitleLabel.setTypeface(pacificoFont);
        
        introBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eppContacts == null || eppContacts.size() == 0) {
                    MyToolBox.AlertMessage(ContactActivity.this, "Please tap on the contact icon to select an emergency contact");
                } else {
                    Intent intent = new Intent(ContactActivity.this, LocationActivity.class);
                    startActivity(intent);
                }
            }
        });
        
        mContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEppContact();
            }
        });

        
        

    }

    private void pickEppContact() {
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_READ_CONTACTS);
        } else{
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(i, PICK_CONTACT);
        }

//        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
//                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactActivity.this,
//                    Manifest.permission.READ_CONTACTS)) {
//            } else {
//                ActivityCompat.requestPermissions(ContactActivity.this,
//                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_READ_CONTACTS);
//            }
//        } else{
//                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                startActivityForResult(i, PICK_CONTACT);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = ContactActivity.this.getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int columnNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int columnName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int columnEmail = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
//            (new normalizePhoneNumberTask()).execute(cursor.getString(column));
            Log.e(TAG+" phone number", cursor.getString(columnNumber));
            mDesc.setText(cursor.getString(columnNumber)+" ");
            final EppContact eppContact = new EppContact();
            eppContact.setId(UUID.randomUUID().toString());
            eppContact.setName(cursor.getString(columnName));
            eppContact.setPhone(cursor.getString(columnNumber)+"");
            if (cursor.getString(columnEmail) != null) {
                eppContact.setEmail(cursor.getString(columnEmail));
            }else {
                eppContact.setEmail("");
            }
            eppContact.setCreatedAt(new Date());
            eppContact.setUpdatedAt(new Date());


            mRealm.executeTransactionAsync(new Realm.Transaction() {
                public void execute(Realm realm) {
                    realm.insert(eppContact);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "realm contact inserted and closed");
                    Toast.makeText(ContactActivity.this, "Contact added successfully", Toast.LENGTH_LONG).show();
//                    introBtn.setEnabled(true);
                }
            });


            cursor.close();

            mRealm.close();

            Intent intent = new Intent(ContactActivity.this, LocationActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickEppContact();
                }
//                else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
            }

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
