package net.blaklizt.streets.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 7:50 AM
 */
public class Registration extends Activity
{
	private EditText username=null;
	private EditText  password=null;
	private TextView register_error;
	private Button login;
	int counter = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		username = (EditText)findViewById(R.id.registerName);
		password = (EditText)findViewById(R.id.registerPassword);
		register_error = (EditText)findViewById(R.id.register_error);
		login = (Button)findViewById(R.id.btnRegister);
	}

	public void login(View view){
		if(username.getText().toString().equals("tkaviya") &&
			password.getText().toString().equals("pass")){
			Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
			Intent mainActivity = new Intent(getApplicationContext(), Streets.class);
			startActivity(mainActivity);
		}
		else{
			Toast.makeText(getApplicationContext(), "Wrong Credentials",
				Toast.LENGTH_SHORT).show();
			register_error.setBackgroundColor(Color.RED);
			counter--;
			register_error.setText("Attempts " + Integer.toString(counter));

			if(counter==0)
			{
				Toast.makeText(getApplicationContext(), "Maximum password attempts. Please contact support", Toast.LENGTH_SHORT).show();
				login.setEnabled(false);
			}
		}

	}
}