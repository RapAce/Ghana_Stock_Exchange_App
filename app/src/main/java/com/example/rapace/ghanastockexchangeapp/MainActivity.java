package com.example.rapace.ghanastockexchangeapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;





public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private  ProgressDialog pDialog;
    private ListView lv;
    private EditText searchtext;

    ListAdapter adapter;




    // URL to get  JSON
    private static String url = "https://dev.kwayisi.org/apis/gse/live";

    ArrayList<HashMap<String, String>> contactList;

    ///////////////////

    Intent i;
    SwipeRefreshLayout RefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();


        ///////////////////////////////////////////////////////////

        i=new Intent(this,DetailsActivity.class);

        RefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetContacts().execute();
                RefreshLayout.setRefreshing(false);
            }
        });

        searchtext=(EditText)findViewById(R.id.search);
        setnotification();




    }

    public void setnotification()
    {
        NotificationCompat.Builder buidler = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        buidler.setContentIntent(pendingIntent);
        buidler.setAutoCancel(true);
        buidler.setDefaults(Notification.DEFAULT_ALL);
        buidler.setContentTitle("Ghana Stock Exchange");
        buidler.setSmallIcon(R.mipmap.ic_launcher);
        buidler.setWhen(System.currentTimeMillis());
        buidler.setContentText("Check Your Stocks Now");
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(001, buidler.build());
    }




    /**
     * Async task class to get json by making HTTP call
     */
    private  class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);

                    // Getting JSON Array node
                    //JSONArray contacts = jsonObj.getJSONArray("");

                    // looping through All Contacts
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);

                        String name = c.getString("name");
                        Double price = c.getDouble("price");
                        Double change = c.getDouble("change");
                        Double volume=c.getDouble("volume");




                        // Phone node is JSON Object
                        //JSONObject phone = c.getJSONObject("phone");
                        //String mobile = phone.getString("mobile");
                        //String home = phone.getString("home");
                        //String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value

                        contact.put("name", name);
                        contact.put("price",Double.toString(price));
                        contact.put("change",Double.toString(change));
                        contact.put("volume",Double.toString(volume));


                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            adapter = new SimpleAdapter(MainActivity.this, contactList, R.layout.list_item, new String[]{"name","price","change","volume"}, new int[]{R.id.name,R.id.price,R.id.change,R.id.volume});
            lv.setAdapter(adapter);



            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView

                    TextView clickedView = (TextView) view.findViewById(R.id.name);
                    TextView clickedView2 = (TextView) view.findViewById(R.id.volume);
                    TextView clickedView3 = (TextView) view.findViewById(R.id.change);

                    String data=(String)clickedView.getText();
                    String data2=(String)clickedView2.getText();
                    String data3=(String)clickedView3.getText();

                    i.putExtra("name",data);
                    i.putExtra("volume",data2);
                    i.putExtra("change",data3);

                    //Toast.makeText(MainActivity.this, "Item with name "+data, Toast.LENGTH_LONG).show();

                    startActivity(i);
                }
            });

            searchtext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ((SimpleAdapter)MainActivity.this.adapter).getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


        }

    }


}

