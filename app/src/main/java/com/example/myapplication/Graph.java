package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Graph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);
        LineChart mChart = findViewById(R.id.chart);
        mChart.setNoDataText("Please enter the name of microorganizm to show graph :)");
        mChart.setTouchEnabled(true);
        XAxis xAxis = mChart.getXAxis();
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);

        Button button = findViewById(R.id.sendMicro);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText microorganizm = findViewById(R.id.micro);
                String micro = microorganizm.getText().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("");

                ArrayList<Entry> val = new ArrayList<>();
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Double lastValue = 0.0;
                        if(dataSnapshot.hasChild(micro)){
                            for (DataSnapshot children : dataSnapshot.child(micro).getChildren()) {
                                Integer key = Integer.valueOf(children.getKey());
                                val.add(new Entry(key,Float.parseFloat(children.getValue().toString())));
                                lastValue = Double.parseDouble(children.getValue().toString());
                            }
                            showChart(val,micro);
                            showGrowth(val.get(0).getY(),lastValue);
                        }
                        else{
                            mChart.clear();
                            mChart.setNoDataText("Your microorganizm does not exist.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });
            }
        });


    }

    public void showGrowth(Float first, Double second) {
        TextView growth = findViewById(R.id.growth);
        Double firs = Double.parseDouble(first.toString());
        Double finalGrowth = (second - firs) / firs * 100;;
        growth.setText("Your organism has grown by "+ roundTwoDecimals(finalGrowth) +" %");
    }

    public void showChart(ArrayList<Entry> val, String name){
        LineChart mChart = findViewById(R.id.chart);
        LineDataSet set1;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(val);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(val, name);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.rgb(255,152,0));

            set1.setLineWidth(1f);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(mChart.getContext(), R.drawable.fade_blue);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
        mChart.invalidate();
        mChart.refreshDrawableState();
    };

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
