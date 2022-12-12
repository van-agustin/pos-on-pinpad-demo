package au.com.dmg.terminalposdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

// TODO check library - refund
public class MainActivity extends AppCompatActivity {

    private Button btnCart;
    private Button btnSatellite;
    private Button btnPrint;
    private Button btnScan;
    private DMGDeviceImpl device = new DMGDeviceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        device.init(getApplicationContext());

        setContentView(R.layout.activity_main);

        btnCart = (Button) findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> openActivityCart());

        btnSatellite = (Button) findViewById(R.id.btnSatellite);
        btnSatellite.setOnClickListener(v -> openActivitySattelite());

        btnPrint = (Button) findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(v -> {
            try {
                testPrint();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            try {
                testScan();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public void testPrint() throws RemoteException {
        new Thread(new Runnable(){
            public void run() {
                    try {
                        Bitmap img = getBitmapFromAsset(getApplicationContext(),"DMGReceipt.png");
                        device.printBitmap(img);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

            }
        }).start();

    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public void testScan() throws RemoteException {
        String pcode = "";
        try {
            pcode = device.frontScanBarcode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Toast toast = Toast.makeText(MainActivity.this, "Barcode: " + pcode, Toast.LENGTH_LONG);
        View view = toast.getView();

        view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();
    }

    public void openActivityCart() {
        Intent intent = new Intent(this, ActivityCart.class);
        startActivity(intent);
    }

    public void openActivitySattelite() {
        Intent intent = new Intent(this, ActivitySatellite.class);
        startActivity(intent);
    }

}


