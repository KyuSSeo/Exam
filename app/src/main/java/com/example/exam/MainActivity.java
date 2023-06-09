package com.example.exam;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText edtSearch;
    private ImageButton btnSearch;
    private RecyclerView rvSearch;

    private ImageButton GpsButton;
    private ImageButton SosButton;
    private TMapView tMapView;


    //국내 화장실 위치 정보 api, api 키 2015년 기준이라 추가 자료 필요
    private static final String BASE_URL = "http://api.data.go.kr/openapi/tn_pubr_public_toilet_api";
    private static final String API_KEY = "RWrHUL8v3vuP3TfKpb1po91xntJZMaKysHxEuih6XBuTbG2qmfJ%2Bl%2FlH%2FtrImFZ%2FaSImTSXvMhIRF3Mvq%2BTokw%3D%3D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSearch = findViewById(R.id.edt_search);
        btnSearch = findViewById(R.id.btn_search);
        rvSearch = findViewById(R.id.rv_search);

        SosButton = findViewById(R.id.btn_sos);
        GpsButton = findViewById(R.id.my_position);
        tMapView = new TMapView(this);

        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey("v5xIFFRmHAaVNSEXhNO8P2eqgglasTNm2viOYZbJ");
        linearLayoutTmap.addView(tMapView);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKeyword = edtSearch.getText().toString().trim();
                if (!searchKeyword.isEmpty()) {
                    performSearch(searchKeyword);
                } else {
                    Toast.makeText(MainActivity.this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TMapPoint tpoint = tMapView.convertPointToGps(50, 100); // tMapView 사용
                double Latitude = tpoint.getLatitude();
                double Longitude = tpoint.getLongitude();
                Toast.makeText(MainActivity.this, "Gps 이동", Toast.LENGTH_SHORT).show();
            }
        });

        SosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Sos.class);
                startActivity(intent);
            }
        });

        GetToiletDataAsyncTask getToiletDataAsyncTask = new GetToiletDataAsyncTask();
        getToiletDataAsyncTask.execute();
    }

    private void performSearch(String keyword) {
        TMapData tMapData = new TMapData();
        tMapData.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItems) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (poiItems != null && poiItems.size() > 0) {
                            // 검색 결과가 있는 경우 처리할 로직 작성필요함
                        } else {
                            Toast.makeText(MainActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private class GetToiletDataAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String response = "";

            try {
                String requestUrl = BASE_URL + "?serviceKey=" + API_KEY + "&param1=value1&param2=value2";

                URL url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        response += line;
                    }
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray toiletArray = jsonResponse.getJSONArray("toilets");

                for (int i = 0; i < toiletArray.length(); i++) {
                    JSONObject toiletObject = toiletArray.getJSONObject(i);

                    double latitude = toiletObject.getDouble("latitude");
                    double longitude = toiletObject.getDouble("longitude");
                    String name = toiletObject.getString("name");
                    TMapPoint toiletPoint = new TMapPoint(latitude, longitude);
                    TMapMarkerItem marker = new TMapMarkerItem();
                    marker.setTMapPoint(toiletPoint);
                    marker.setCalloutTitle(name);
                    tMapView.addMarkerItem("toilet_" + i, marker);
                }
                tMapView.setCenterPoint(toiletArray.getJSONObject(0).getDouble("latitude"),
                        toiletArray.getJSONObject(0).getDouble("longitude"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
