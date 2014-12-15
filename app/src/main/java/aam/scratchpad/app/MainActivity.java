package aam.scratchpad.app;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


public class MainActivity extends Activity {

    private float X, Y ;


    private WebServer mWebServer;
    private static final String TAG = "MainActivity";
    public int flag = 1 ;

    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mbutton = (Button) findViewById(R.id.button1) ;
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==1)
                    flag=0;



            }
        });
        startServer();

        mWebServer = new WebServer();
        try {
            mWebServer.start();
        } catch(IOException ioe) {
            Log.w("Httpd", "The server could not start.");
        }
        Log.w("Httpd", "Web server initialized.");
        RelativeLayout  myLayout =
                (RelativeLayout)findViewById(R.id.RelativeLayout1);

        myLayout.setOnTouchListener(
                new RelativeLayout.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent m) {
                        handleTouch(m);
                        return true;
                    }
                }
        );

        /*final TextView mTextView = (TextView) findViewById(R.id.game_running);
       // final TextView ipTextView = (TextView) findViewById(R.id.ip_address);
       // final String ipAddress = Utils.getWifiApIpAddress();
        if(ipAddress != null) {
            ipTextView.setText("Please enter this url in your laptop's browser : " + ipAddress + ":4040");
        } else {
            ipTextView.setText("Please enable WiFi hotspot in your phone and connect your laptop to the hotspot.");
        }

        final Button mButton = (Button) findViewById(R.id.button_start);
        mButton.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ipAddress == null) {
                    Toast.makeText(getApplicationContext(), "Please turn on your hotspot first and restart the app!", Toast.LENGTH_SHORT).show();
                } else {
                    mButton.setVisibility(View.GONE);
                    gameStarted = true;
                    mTextView.setVisibility(View.VISIBLE);
                }
            }
        });*/

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");

        // Log.d(TAG, "Current IP Address = " + Utils.getLocalIpAddress());
    }



    protected void onResume() {
        super.onResume();
        wl.acquire();

    }

    @Override
    public void onPause() {
        super.onPause();
        wl.release();

    }

    @Override
    public void onDestroy() {
        mWebServer.stop();
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
    void handleTouch(MotionEvent m)
    {
        TextView textView1 = (TextView)findViewById(R.id.textView1);




            X = (int) m.getX(0);
            Y = (int) m.getY(0);




            String touchStatus = " X: " + X + " Y: " + Y ;


                textView1.setText(touchStatus);


    }

    PrintWriter out ;
    Socket clientSocket;
    ServerSocket connectSocket = null;

    public void printToServer(JSONObject obj){
        out.println(obj.toString());
    }

    public void startServer ()
    {





        try
        {

            connectSocket = new ServerSocket(2000);

            clientSocket = connectSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));


                String data = in.readLine();

               // out.println("YAY!");

        }
        catch (IOException e)
        {
            System.out.println("You fail: " + e.getMessage());
        }

        System.out.println("Finished!");
    }

    private class WebServer extends NanoHTTPD {

        public WebServer() {

            super(5050);
        } /*

        @Override
        public Response serve(IHTTPSession session) {
            Map<String, List<String>> decodedQueryParameters =
                    decodeParameters(session.getQueryParameterString());


/*
            try {
                Map<String, String> files = new HashMap<String, String>();
                session.parseBody(files);
                sb.append("<h3>Files</h3><p><blockquote>").
                        append(toString(files)).append("</blockquote></p>");
            } catch (Exception e) {
                e.printStackTrace();
            }

            sb.append("</body>");
            sb.append("</html>");

        } */

        @Override
        public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms,
                              Map<String, String> files) {

            headers.put("Access-Control-Allow-Origin", "*");
            String responseString = "";
            JSONObject obj=new JSONObject();
            Log.d(TAG, "URI = " + uri);
            if(uri.startsWith("/draw")) {
                try {

                        obj.put("flag",flag) ;
                        obj.put("x", X);

                        obj.put("y", Y);
                        if(flag==0)
                            flag=1;
                        printToServer(obj);

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    responseString = obj.toString();
                    return new Response(responseString);
                }
            } else {
                if (uri.equals("/")) {
                    //request for index
                    responseString = readText("index");
                    return new Response(responseString);
                }
                String mimeType = "";
                String tempString = uri.split("/")[1];
                Log.d(TAG, "First split = " + tempString);
                String fileExtension = tempString.split("\\.")[1];
                String resourceName = tempString.split("\\.")[0];
                if (fileExtension.equals("wav")) {
                    mimeType = "audio/x-wav";
                    return new Response(Response.Status.OK, mimeType, getInputStream(resourceName));
                } else if(fileExtension.equals("js")) {
                    mimeType = "text/javascript";
                    return new Response(Response.Status.OK, mimeType, getInputStream(resourceName));
                } else if(fileExtension.equals("jpg")) {
                    mimeType = "image/jpeg";
                    return new Response(Response.Status.OK, mimeType, getInputStream(resourceName));
                } else if(fileExtension.equals("png")) {
                    mimeType = "image/png";
                    return new Response(Response.Status.OK, mimeType, getInputStream(resourceName));
                }
                //Read text files
                responseString = readText(resourceName);
                return new Response(Response.Status.OK, mimeType, responseString);
            }
        }

        private InputStream getInputStream (String resourceName) {
            return getResources().openRawResource( getResources().getIdentifier(resourceName,"raw", getPackageName()));
        }

        private String readText(String resourceName) {
            int i;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                InputStream inputStream = getResources().openRawResource( getResources().getIdentifier(resourceName,"raw", getPackageName()));

                i = inputStream.read();
                while (i != -1)
                {
                    byteArrayOutputStream.write(i);
                    i = inputStream.read();
                }
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return byteArrayOutputStream.toString();
        }
    }

}
