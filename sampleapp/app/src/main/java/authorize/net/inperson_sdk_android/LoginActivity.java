package authorize.net.inperson_sdk_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.authorize.Environment;
import net.authorize.Merchant;
import net.authorize.TransactionType;
import net.authorize.aim.cardpresent.DeviceType;
import net.authorize.aim.cardpresent.MarketType;
import net.authorize.aim.emv.EMVTransaction;
import net.authorize.aim.emv.EMVTransactionManager;
import net.authorize.auth.PasswordAuthentication;
import net.authorize.auth.SessionTokenAuthentication;
import net.authorize.data.Order;
import net.authorize.data.OrderItem;
import net.authorize.data.creditcard.CreditCard;
import net.authorize.data.creditcard.CreditCardPresenceType;
import net.authorize.data.mobile.MobileDevice;

import java.math.BigDecimal;

/**
 * Created by vkinagi on 11/3/2015.
 */
public class LoginActivity extends FragmentActivity {
Button b;
EditText login;
EditText pwd;
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        b= (Button)findViewById(R.id.buttonLoginLoginIn);
        b.setOnClickListener(mListner);

        login = (EditText)findViewById(R.id.editTextLoginLoginID);
        pwd = (EditText)findViewById(R.id.editTextLoginPassword);


//        login.setText("javasdk1");
//        pwd.setText("Authnet103");

//        login.setText("FdcRetailOwner1");
//        pwd.setText("Authnet101");


        login.setText("MobileCNP1");
        pwd.setText("Authnet105");
        context = this;
    }


    Handler handler = new Handler(){

@Override
public void handleMessage(Message inputMessage) {

    if(inputMessage.what==0){
        Intent i = new Intent(context,MainActivity.class);
        startActivity(i);
        finish();
        pd.dismiss();
    }
    else{
        pd.dismiss();
        Toast.makeText(context.getApplicationContext(),"Login Error",Toast.LENGTH_SHORT).show();
    }

}

    };

    ProgressDialog pd;
    View.OnClickListener mListner = new View.OnClickListener(){

        public void onClick(View view){
        Thread t = new Thread(){

            @Override
        public void run(){

//                handler.sendEmptyMessage(0);
                net.authorize.mobile.Result result;
                String deviceID = "Test EMV Android";
                PasswordAuthentication passAuth = PasswordAuthentication
                        .createMerchantAuthentication(login.getText().toString(), pwd.getText().toString(), deviceID);

                AppManager.merchant = Merchant.createMerchant(Environment.SANDBOX, passAuth);

                net.authorize.mobile.Transaction transaction = AppManager.merchant
                        .createMobileTransaction(net.authorize.mobile.TransactionType.MOBILE_DEVICE_LOGIN);
                MobileDevice mobileDevice = MobileDevice.createMobileDevice(deviceID,
                        "Device description", "425-555-0000", "Android");
                transaction.setMobileDevice(mobileDevice);
                result = (net.authorize.mobile.Result) AppManager.merchant
                        .postTransaction(transaction);

                if(result.isOk()){

                    try {
                            SessionTokenAuthentication sessionTokenAuthentication = SessionTokenAuthentication
                                    .createMerchantAuthentication(AppManager.merchant
                                            .getMerchantAuthentication().getName(), result
                                            .getSessionToken(), "Test EMV Android");
                            if ((result.getSessionToken() != null)
                                    && (sessionTokenAuthentication != null)) {
                                AppManager.merchant
                                        .setMerchantAuthentication(sessionTokenAuthentication);

                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception ex) {

                        }
                }
                else{
                    handler.sendEmptyMessage(1);
                    Log.e("EMVResponse",result.getXmlResponse());
//                    Toast.makeText(context, result.getXmlResponse(),Toast.LENGTH_SHORT).show();
                }

            }
        };
            t.start();
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setTitle("Logging in..");
            pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            pd.show();

        }
    };


}
