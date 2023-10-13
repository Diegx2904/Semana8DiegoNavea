package com.example.semana8diegonavea;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button botonObtener;
    private ImageView imagenSolari;
    private Button botonRotar;

    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;

    private boolean isRotating = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonObtener = findViewById(R.id.btn_Obtener);
        imagenSolari = findViewById(R.id.imgSolari);
        botonRotar = findViewById(R.id.btn_Rotar);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        botonObtener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = loadImageFromNetwork("https://www.latercera.com/resizer/7NnIHc-ZtM9KL0FSNkkrS55rprA=/900x600/smart/cloudfront-us-east-1.images.arcpublishing.com/copesa/B6ZL73ULRBFKZBBGI2VDR2DUFE.jpg");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imagenSolari.setImageBitmap(bitmap);
                            }
                        });
                    }
                }).start();
            }
        });

        botonRotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotating = !isRotating;

                if (isRotating) {
                    sensorManager.registerListener(MainActivity.this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    sensorManager.unregisterListener(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Obtener los valores de rotación del sensor de rotación
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // Obtener los valores de orientación
            float[] orientationValues = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            // Obtener el ángulo de rotación en radianes y convertirlo a grados
            float rotationInRadians = orientationValues[0];
            float rotationInDegrees = (float) Math.toDegrees(rotationInRadians);

            // Rotar la imagen
            imagenSolari.setRotation(rotationInDegrees);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Manejar cambios en la precisión del sensor si es necesario
    }

    private Bitmap loadImageFromNetwork(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);

            connection.disconnect();

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


