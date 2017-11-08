package com.example.moonc.emergencysms;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class addnumber extends AppCompatActivity {

    TextView txt1,txt2,txt3;
    Button btn;
    String num1 = "",num2 = "",num3 = "";
    File file;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SavedContracts";
    String[] ary;
    Button first,second,third;
    int Contactrequest;
    int whichString ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnumber);
        txt1 = (TextView)findViewById(R.id.editText);
        txt2 = (TextView)findViewById(R.id.editText2);
        txt3 = (TextView)findViewById(R.id.editText3);
        btn = (Button)findViewById(R.id.button);
        first = (Button)findViewById(R.id.first);
        Contactrequest = 10;
        first.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        SelectContacts(1);
                    }
                }
        );
        second = (Button)findViewById(R.id.second);
        second.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                         SelectContacts(2);
                    }
                }
        );
        third = (Button)findViewById(R.id.third);
        third.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        SelectContacts(3);
                    }
                }
        );

        final String[] Contracts = new String[3];
        file = new File(path+"/Contracts.txt");

        if(file.length()>3) {

            String[] ary = Load(file);
            num1 = ary[0];
            num2 = ary[1];
            num3 = ary[2];

            txt1.setText(num1);
            txt2.setText(num2);
            txt3.setText(num3);
        }else
        {
            txt1.setText(num1);
            txt2.setText(num2);
            txt3.setText(num3);
        }

        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        num1 = txt1.getText().toString();
                        num2 = txt2.getText().toString();
                        num3 = txt3.getText().toString();
                        Contracts[0] = num1;
                        Contracts[1]  = num2;
                        Contracts[2] = num3;

                        SaveContracts(file,Contracts);
                        Toast.makeText(addnumber.this,"Saved",Toast.LENGTH_SHORT).show();

                    }
                }
        );

    }

    private void SelectContacts(int i) {
        whichString = i;
        Intent intent  = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Contactrequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Contactrequest) {
            if(resultCode==RESULT_OK) {
                Log.v("MainActivity", "On Contacts");
                Uri ContactListData = data.getData();
                //  Uri ContactListData = (Uri) data.getExtras().get("data");
                Cursor c = getContentResolver().query(ContactListData, null, null, null, null);


                if(c.getCount()>0) {
                    if(c.moveToNext()){
                        String Name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        Toast.makeText(this, Name + " Has been added", Toast.LENGTH_SHORT).show();

                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1"))
                        {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                            phones.moveToFirst();
                            String NumberString = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                             if(whichString==1)
                             {
                                 num1 = NumberString;
                             }else if(whichString == 2)
                             {
                                 num2 = NumberString;
                             }else if(whichString == 3)
                             {
                                 num3 = NumberString;
                             }else
                             {
                                 Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                             }

                            txt1.setText(num1);
                            txt2.setText(num2);
                            txt3.setText(num3);

                        }else
                        {
                            Toast.makeText(this,"Multiple Numbers",Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        }

    }

    public static String[] Load(File file)
    {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl = 0;
        try {
            while ((test = br.readLine()) != null) {
                anzahl++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis.getChannel().position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try {
            while ((line = br.readLine()) != null) {
                array[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    public  void  SaveContracts(File file,String[] Contracts)
    {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(int i = 0;i<Contracts.length;i++)
        {
            try {
                fos.write(Contracts[i].getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(i<Contracts.length)
            {
                try {
                    fos.write("\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
