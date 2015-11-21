package net.blaklizt.streets.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.RegisterTask;

import static net.blaklizt.streets.android.common.StreetsCommon.getTag;
import static net.blaklizt.streets.android.common.StreetsCommon.showToast;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 7:50 AM
 */
public class Register extends Activity implements View.OnClickListener
{
	private static final String TAG = getTag(Register.class);

	private static Register register;

	public EditText firstName = null;
	public EditText lastName = null;
	public EditText username = null;
	public EditText msisdn = null;
	public EditText email = null;
	public EditText confirmPassword = null;
	public EditText password = null;
	public TextView registerError;
	public Button registerBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			register = this;
			setContentView(R.layout.register_layout);
			firstName = (EditText) findViewById(R.id.registerFirstName);
			lastName = (EditText) findViewById(R.id.registerLastName);
			username = (EditText) findViewById(R.id.registerUsername);
			msisdn = (EditText) findViewById(R.id.registerMsisdn);
			email = (EditText) findViewById(R.id.registerEmail);
			password = (EditText) findViewById(R.id.registerPin);
			confirmPassword = (EditText) findViewById(R.id.registerConfirmPin);
			registerError = (TextView) findViewById(R.id.register_error);
			registerBtn = (Button) findViewById(R.id.btnRegister);
			registerBtn.setOnClickListener(this);

			findViewById(R.id.labelRegisterHeader).setOnClickListener(view -> {
                Intent loginActivity = new Intent(getInstance(), Startup.class);
                startActivity(loginActivity);
            });
			findViewById(R.id.labelGoToLogin).setOnClickListener(view -> {
                Intent loginActivity = new Intent(getInstance(), Startup.class);
                startActivity(loginActivity);
            });
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to create register dialogue: " + ex.getMessage(), ex);
			runOnUiThread(() -> showToast(TAG, "Failed to create register dialogue! An error occurred.", Toast.LENGTH_LONG));
		}
	}

	public static Register getInstance() { return register; }

	public void onClick(View view)
	{
		if (!password.getText().toString().equals(confirmPassword.getText().toString()))
		{
			Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
			password.requestFocus();
			return;
		}
		new RegisterTask(this).execute();
	}
}