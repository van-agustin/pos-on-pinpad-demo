package au.com.dmg.terminalposdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import au.com.dmg.terminalposdemo.R;

import au.com.dmg.terminalposdemo.ingenicoUtil.DeviceHelper;
import com.usdk.apiservice.aidl.printer.AlignMode;
import com.usdk.apiservice.aidl.printer.FactorMode;
import com.usdk.apiservice.aidl.printer.OnPrintListener;
import com.usdk.apiservice.aidl.printer.UPrinter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ActivityResult extends AppCompatActivity {

    private Button btnPrintReceipt;
    private TextView tvResult;

    ///printer start
    private UPrinter printer;
    protected void initDeviceInstance() {
        printer = DeviceHelper.me().getPrinter();
    }
    public void initRegister() {
        register(true);
    }
    private void unregister() {
        try {
            DeviceHelper.me().unregister();
        } catch (IllegalStateException e) {
            System.out.println("unregister fail: " + e.getMessage());
        }
    }
    private void register(boolean useEpayModule) {
        try {
            DeviceHelper.me().register(useEpayModule);
        } catch (IllegalStateException e) {
            System.out.println("register fail: " + e.getMessage());
        }
    }
    ///printer end

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        tvResult = (TextView) findViewById(R.id.tvReceipt);
        btnPrintReceipt = (Button) findViewById(R.id.btnPrintReceipt);
        TextView tvReceipt = (TextView) findViewById(R.id.tvReceipt);
        TextView tvMessageHead = (TextView) findViewById(R.id.tvMessageHead);
        TextView tvMessageDetail = (TextView) findViewById(R.id.tvMessageDetail);

        String errorCondition = "";
        String additionalResponse = "";

        String txntype = getIntent().getStringExtra("txnType");
        String result = getIntent().getStringExtra("result");

        ///AmountsResp
        String authorizedAmount = getIntent().getStringExtra("AuthorizedAmount");
//        String totalFeesAmount = getIntent().getStringExtra("TotalFeesAmount");
        String cashBackAmount = getIntent().getStringExtra("CashBackAmount");
        String tipAmount = getIntent().getStringExtra("TipAmount");
        String surchargeAmount = getIntent().getStringExtra("SurchargeAmount");

        ///PaymentInstrumentData
        String paymentInstrumentType = getIntent().getStringExtra("PaymentInstrumentType");

        ///CardData
        String paymentBrand = getIntent().getStringExtra("PaymentBrand");
        String maskedPAN = getIntent().getStringExtra("MaskedPAN");
        String entryMode = getIntent().getStringExtra("EntryMode");
        String account = getIntent().getStringExtra("Account");

        ///PaymentAcquirerData
        String approvalCode = "";
        String transactionID = "";

        ///PaymentReceipt
        String outputXHTML = getIntent().getStringExtra("OutputXHTML");

        if(Objects.equals(result, "Success")){

//            String OutputXHTML = getIntent().getStringExtra("OutputXHTML");
//            String paymentresult = getIntent().getStringExtra("paymentresult");
//
//            String authorizedamount = getIntent().getStringExtra("authorizedamount");
//            String surcharge = getIntent().getStringExtra("surcharge");
//            String tip = getIntent().getStringExtra("tip");
//            String paymentbrand = getIntent().getStringExtra("paymentbrand");
//            String transactionid = getIntent().getStringExtra("transactionid");
//            String pan = getIntent().getStringExtra("pan");
//            String approvalcode = getIntent().getStringExtra("approvalcode");
//            String entrymode = getIntent().getStringExtra("entrymode");
//            String account = getIntent().getStringExtra("account");
//            String paymentinstrumenttype = getIntent().getStringExtra("paymentinstrumenttype");
            switch (txntype) {
                case "Refund":
                    txntype = "Refund";
                case "Payment":
                    //PaymentAcquirerData
                    approvalCode = getIntent().getStringExtra("ApprovalCode");
                    transactionID = getIntent().getStringExtra("TransactionID");

                case "TransactionStatus":
                    tvMessageHead.setText("Payment " + result);
                    tvMessageHead.setTextColor(Color.parseColor("#FF4CAF50"));

                    tvMessageDetail.setText("Authorized Amount: " + authorizedAmount + "\n"
                            + "Surcharge: " + surchargeAmount + "\n"
                            + "Tip: " + tipAmount  + "\n"
                            + "Payment Brand: " + paymentBrand  + "\n"
                            + "Transaction ID: " + transactionID  + "\n"
                            + "Masked PAN: " + maskedPAN  + "\n"
                            + "Approval Code: " + approvalCode  + "\n"
                            + "Entry Mode: " + entryMode  + "\n"
                            + "Account: " + account  + "\n"
                            + "Payment Instrument Type: " + paymentInstrumentType
                    );
                    tvMessageDetail.setVisibility(View.GONE);
                    tvReceipt.setText(HtmlCompat.fromHtml(outputXHTML, HtmlCompat.FROM_HTML_MODE_COMPACT));
                    break;
                case "refund":
                    tvMessageHead.setText("Refund " + result);
                    tvMessageHead.setTextColor(Color.parseColor("#FF4CAF50"));

                    tvMessageDetail.setText("Authorized Amount: " + authorizedAmount + "\n");

                    tvReceipt.setText(HtmlCompat.fromHtml(outputXHTML, 0));
                    break;
                case "cancel":
                    tvMessageHead.setText("Transaction Cancelled");
                    tvMessageHead.setTextColor(Color.parseColor("#FF4CAF50"));

                    tvMessageDetail.setText("Authorized Amount: " + authorizedAmount + "\n"
                            + "Surcharge: " + surchargeAmount + "\n"
                            + "Tip: " + tipAmount
                    );

                    tvReceipt.setText(HtmlCompat.fromHtml(outputXHTML, 0));
                    break;
                default:
                    tvMessageHead.setText("Error \n" + txntype);
                    break;
            }

        }

        else
        {
            tvMessageHead.setTextColor(Color.parseColor("#FF0000"));
            errorCondition = getIntent().getStringExtra("errorCondition");;
            additionalResponse = getIntent().getStringExtra("additionalResponse");;
            btnPrintReceipt.setVisibility(View.GONE);
            tvReceipt.setVisibility(View.GONE);
            tvMessageDetail.setVisibility(View.VISIBLE);
//            tvMessageDetail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            switch (errorCondition){

                case "NotConnectedException":
                    tvMessageHead.setText("No Internet");
                    tvMessageDetail.setText("Please make sure POS is connected to the internet");
                    break;
                case "Timeout":
                    tvMessageHead.setText("Transaction Timeout");
                    tvMessageDetail.setText("Transaction Aborted");
                    break;
                case "NotFound":
                    tvMessageHead.setText("Transaction Not Found");
                    tvMessageDetail.setText(additionalResponse);
                    break;
                case "Aborted":
                case "Busy":
                case "Cancel":
                case "DeviceOut":
                case "InsertedCard":
                case "InProgress":
                case "LoggedOut":
                case "MessageFormat":
                case "NotAllowed":
                case "PaymentRestriction":
                case "Refusal":
                case "UnavailableDevice":
                case "UnavailableService":
                case "InvalidCard":
                case "UnreachableHost":
                case "WrongPIN":
                default:
                    tvMessageHead.setText("Transaction Failed");
                    String errorMessage = errorCondition + "\n\n" + additionalResponse;
                    tvMessageDetail.setText(errorMessage);
            }
        }

    tvReceipt.setMovementMethod(new ScrollingMovementMethod());

        btnPrintReceipt.setOnClickListener(v -> {
            try {
                startPrint();
            } catch (RemoteException | IOException e) {
                e.printStackTrace();
            }
        });
    }
    private void startPrint() throws RemoteException, IOException{
//        initRegister();
        register(true);
        initDeviceInstance();

        byte[] image = readAssetsFile(this, "DMGReceipt.png");
        printer.addBmpImage(0, FactorMode.BMP1X1, image);
        printer.feedLine(1);
        String x = String.valueOf(tvResult.getText());
        printer.addText(AlignMode.LEFT, x);

        //test barcode
        printer.feedLine(1);
        printer.addBarCode(AlignMode.CENTER, 2, 48,  "DMGProductCode123");

        printer.feedLine(2);

        printer.startPrint(new OnPrintListener.Default() {
            @Override
            public void onFinish() throws RemoteException {
                System.out.println("=> onFinish | printing");

            }

            @Override
            public void onError(int error) throws RemoteException {
                System.out.println("=> onError: " + error);
            }
        });
        printer.cutPaper();

    }
    private static byte[] readAssetsFile(Context ctx, String fileName) throws IOException {
        InputStream input = null;
        try {
            input = ctx.getAssets().open(fileName);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}