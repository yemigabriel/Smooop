package com.devstudiosng.yg.smooop;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import com.devstudiosng.yg.smooop.adapter.EppContactAdapter;
import com.devstudiosng.yg.smooop.model.EppContact;

import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private Realm mRealm;
    private FloatingActionButton addContactFab;


    private static final int MY_PERMISSIONS_READ_CONTACTS = 123321;
    private final String TAG = this.getClass().getSimpleName();
    private final int PICK_CONTACT = 1602;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        Realm.init(mContext);
        mRealm = Realm.getDefaultInstance();

        addContactFab = rootView.findViewById(R.id.add_contact);
        mProgressBar = rootView.findViewById(R.id.progress_bar);

        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
            }
        });

        addContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEppContact();
            }
        });

        mRecyclerView = rootView.findViewById(R.id.contact_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                mSwipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }

        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);

        getContacts();

        return rootView;
    }

    private void getContacts() {
        RealmResults<EppContact> eppContacts = mRealm.where(EppContact.class).findAll().sort("mName");
        EppContactAdapter adapter = new EppContactAdapter(eppContacts, mContext);
        mRecyclerView.setAdapter(adapter);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    private void pickEppContact() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_READ_CONTACTS);
            }
        } else{
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(i, PICK_CONTACT);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = mContext.getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int columnNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int columnName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int columnEmail = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
//            (new normalizePhoneNumberTask()).execute(cursor.getString(column));
            Log.e(TAG+" phone number", cursor.getString(columnNumber));
//            mDesc.setText(cursor.getString(columnNumber)+" ");
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
                    Toast.makeText(mContext, "Contact added successfully", Toast.LENGTH_LONG).show();
                }
            });


            cursor.close();

            mRealm.close();

//            Intent intent = new Intent(mContext, LocationActivity.class);
//            startActivity(intent);
            getContacts();
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
}
