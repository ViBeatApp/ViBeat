package com.vibeat.vibeatapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vibeat.vibeatapp.Adapters.CostumeListAdapter;
import com.vibeat.vibeatapp.ListClasses.PartiesList;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.Party;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.HelperClasses.passingInfo;
import com.vibeat.vibeatapp.Adapters.PartyCoustumAdapter;
import com.vibeat.vibeatapp.R;
import com.vibeat.vibeatapp.HelperClasses.pictureChange;

import java.util.List;

public class EnterPartyActivity extends AppCompatActivity {

    public MyApplication app;
    ListView listOfParties;

    /*User user1 = new User("Dana Oshri", R.drawable.dana, 1, false);
    User user2 = new User("Idan Cohen", R.drawable.idan, 2, true);
    User user3 = new User("Ido Abulafya", R.drawable.ido, 3, false);
    User user4 = new User("Tomer Solomon", R.drawable.tomer, 4, false);

    User[][] users = { {user3, user1}, {user2, user4}};
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_party);

        app = (MyApplication) this.getApplication();
        List<Party> nearby_parties = app.client_manager.getPartiesNearby();

        listOfParties = (ListView) findViewById(R.id.parties_list);
        listOfParties.setAdapter(new CostumeListAdapter(EnterPartyActivity.this,
                                                        new PartiesList(nearby_parties)));

        ImageView user = (ImageView) findViewById(R.id.izzy);
        TextView name = (TextView) findViewById(R.id.hi);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.izzy);
        bm = pictureChange.getCroppedBitmap(bm);
        user.setImageBitmap(bm);

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createParty(v);
            }
        });
    }

    public void createParty(View v) {
        Intent intent = new Intent(this, CreatePartyActivity.class);
        info.group_id = 100;
        intent.putExtra("info",info);
        startActivity(intent);
    }
}
