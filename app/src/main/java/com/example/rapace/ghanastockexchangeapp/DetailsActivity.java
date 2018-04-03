package com.example.rapace.ghanastockexchangeapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





public class DetailsActivity extends AppCompatActivity {

    private String TAG = DetailsActivity.class.getSimpleName();


    private PieChart pchart;
    private BarChart bchart;
    private LineChart lChart;

    String ticker;

  static   Float volume;
  static   Float price;
  static   Float shares;
  static   Float capital;
  static   Float dps;
  static   Float eps;
  static   Float change;

  static   String address;
  static   String email;
  static   String facsimile;
  static   String industry;
  static   String name;
  static   String sector;
  static   String telephone;
  static   String website;

    private static String url;

    private  ProgressDialog pDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle mainactivitydata=getIntent().getExtras();

        ticker = mainactivitydata.getString("name");
        url = "https://dev.kwayisi.org/apis/gse/equities/"+ticker;
        if(mainactivitydata==null)
        {
            return;
        }
        change=Float.parseFloat(mainactivitydata.getString("change"));
        volume=Float.parseFloat(mainactivitydata.getString("volume"));

        new GetJsonData().execute();




    }









    private void setupPieChart()
    {
        pchart=(PieChart)findViewById(R.id.piechart);
        pchart.setBackgroundColor(Color.GRAY);
        //pchart.setUsePercentValues(true);
        pchart.getDescription().setEnabled(false);
        pchart.setExtraOffsets(5,10,5,5);
        pchart.setDragDecelerationFrictionCoef(0.99f);
        pchart.setHoleColor(Color.GRAY);
        pchart.setTransparentCircleRadius(60f);

        ArrayList<PieEntry> values=new ArrayList<>();
        if((shares==null)|(capital==null))
        {
            return;
        }
        values.add(new PieEntry(shares,"Total Issued Shares"));
        values.add(new PieEntry(capital,"Market Capital(GH Cedis)"));

        Description description=new Description();
        description.setText("");
        description.setTextSize(15);
        pchart.setDescription(description);

        PieDataSet dataSet=new PieDataSet(values,"");
        dataSet.setSelectionShift(5f);
        dataSet.setSliceSpace(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data=new PieData(dataSet);
        //data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.WHITE);


        pchart.setData(data);
        pchart.invalidate();

        pchart.animateY(5000, Easing.EasingOption.EaseInOutCubic);

        pchart.setEntryLabelColor(Color.WHITE);
        pchart.setEntryLabelTextSize(12f);

    }








    private void setupBarChart()
    {
       bchart=(BarChart)findViewById(R.id.barchart);

       bchart.setDragEnabled(false);
       bchart.setDrawValueAboveBar(true);
       bchart.setMaxVisibleValueCount(50);
       bchart.setPinchZoom(true);
       bchart.setDrawGridBackground(false);
       bchart.setBackgroundColor(Color.GRAY);

       ArrayList<BarEntry> barEntries=new ArrayList<>();

       if((change==null)|(eps==null)|(dps==null))
       {
           return;
       }
       barEntries.add(new BarEntry(1,change));
       barEntries.add(new BarEntry(2,eps));
       barEntries.add(new BarEntry(3,dps));

       BarDataSet barDataSet=new BarDataSet(barEntries,"Price Change | Earning Per Share  |  Dividend Per Share");
       barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

       BarData data=new BarData(barDataSet);
       data.setBarWidth(0.7f);

       bchart.setData(data);
       bchart.invalidate();
       bchart.animateY(5000);

       Description description=new Description();
       description.setText("");
       description.setTextSize(15);
       bchart.setDescription(description);

       String[] labels=new String[]{"Change","Change","E.P.S.","D.P.S.","DPS"};
       XAxis xAxis=bchart.getXAxis();
       xAxis.setValueFormatter(new MyAxisValueFormatter(labels));
       xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
       xAxis.setGranularity(1);
      // xAxis.setCenterAxisLabels(true);
       //xAxis.setAxisMinimum(1);

    }
    public class MyAxisValueFormatter implements IAxisValueFormatter{

        private String[] mValues;
        public MyAxisValueFormatter(String [] values)
        {
            this.mValues=values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }










    private  class GetJsonData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailsActivity.this);
            pDialog.setMessage("Retrieving Data........");
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
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONObject c = jsonObj.getJSONObject("company");
                    address = c.getString("address");
                    email = c.getString("email");
                    facsimile=c.getString("facsimile");
                    industry=c.getString("industry");
                    name=c.getString("name");
                    sector=c.getString("sector");
                    telephone=c.getString("telephone");
                    website=c.getString("website");

                    capital=(float)jsonObj.getDouble("capital");
                    price=(float)jsonObj.getDouble("price");
                    shares=(float)jsonObj.getDouble("shares");

                    String temp;

                    temp=jsonObj.getString("eps");
                    if(temp=="null")
                    {
                        eps=0f;
                    }
                    else
                    {
                        eps=(float)jsonObj.getDouble("eps");
                    }

                    temp=jsonObj.getString("dps");
                    if(temp=="null")
                    {
                        dps=0f;
                    }
                    else
                    {
                        dps=(float)jsonObj.getDouble("dps");
                    }




                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
            {
                pDialog.dismiss();
            }

            setupPieChart();
            setupBarChart();
            setupLineChart();
            setupText();

        }


    }



    private void setupLineChart()
    {
        lChart=(LineChart)findViewById(R.id.linechart);
        lChart.setBackgroundColor(Color.GRAY);
        lChart.setDrawGridBackground(false);

        if((volume==null)|(price==null))
        {
            return;
        }

        ArrayList<Entry> yVals1=new ArrayList<>();
        yVals1.add(new Entry(0,volume));
        yVals1.add(new Entry(1,volume));
        yVals1.add(new Entry(2,volume));
        yVals1.add(new Entry(3,volume));
        yVals1.add(new Entry(4,volume));


        ArrayList<Entry> yVals2=new ArrayList<>();
        yVals2.add(new Entry(0,price));
        yVals2.add(new Entry(1,price));
        yVals2.add(new Entry(2,price));
        yVals2.add(new Entry(3,price));
        yVals2.add(new Entry(4,price));



        LineDataSet set1,set2;

        set1=new LineDataSet(yVals1,"Volume Of Shares Traded");
        set1.setColor(Color.GREEN);
        set1.setDrawCircles(false);
        set1.setLineWidth(3f);

        set2=new LineDataSet(yVals2,"Share Price");
        set2.setColor(Color.YELLOW);
        set2.setDrawCircles(false);
        set2.setLineWidth(5f);

        LineData data=new LineData(set1,set2);

        lChart.setData(data);

        lChart.animateX(10000);

        Description description=new Description();
        description.setText("");
        description.setTextSize(15);
        lChart.setDescription(description);

    }


    private void setupText()
    {
        TextView companyname=(TextView)findViewById(R.id.companyname);
        companyname.setText(name);

        TextView companyaddress=(TextView)findViewById(R.id.address);
        companyaddress.setText(address);

        TextView companyemail=(TextView)findViewById(R.id.email);
        companyemail.setText(email);

        TextView companyfacsimile=(TextView)findViewById(R.id.fascimile);
        companyfacsimile.setText(facsimile);

        TextView companyindustry=(TextView)findViewById(R.id.industry);
        companyindustry.setText(industry);

        TextView companysector=(TextView)findViewById(R.id.sector);
        companysector.setText(sector);

        TextView companytelephone=(TextView)findViewById(R.id.telephone);
        companytelephone.setText(telephone);

        TextView companywebsite=(TextView)findViewById(R.id.website);
        companywebsite.setText(website);
    }





}
