package net.blaklizt.streets.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.RegisterTask;

import static net.blaklizt.streets.android.activity.AppContext.getStreetsCommon;
import static net.blaklizt.streets.android.common.StreetsCommon.getTag;
import static net.blaklizt.streets.android.common.StreetsCommon.showSnackBar;
import static net.blaklizt.symbiosis.sym_core_lib.utilities.Validator.isValidPin;
import static net.blaklizt.symbiosis.sym_core_lib.utilities.Validator.isValidUsername;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 7:50 AM
 */
public class Register extends AppCompatActivity implements View.OnClickListener
{
	private static final String TAG = getTag(Register.class);

	private static Register register;
    public ImageView imgRegisterBack;
    public ImageView imgRegisterForward;
    public TextView textInfoRegister;
    public EditText registerUsername;
    public EditText registerPin;
    public EditText registerConfirmPin;
    public TextView registerError;
	public Button registerBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        register = this;

        imgRegisterBack = (ImageView) findViewById(R.id.imgRegisterBack);
        imgRegisterForward = (ImageView) findViewById(R.id.imgRegisterForward);
        registerUsername = (EditText) findViewById(R.id.registerUsername);
        registerPin = (EditText) findViewById(R.id.registerPin);
        registerConfirmPin = (EditText) findViewById(R.id.registerConfirmPin);
        registerError = (TextView) findViewById(R.id.registerError);
        textInfoRegister = (TextView) findViewById(R.id.textInfoRegister);
        registerBtn = (Button) findViewById(R.id.btnRegister);
        registerBtn.setOnClickListener(this);

        imgRegisterBack.setOnClickListener(view -> {
            Intent registerServiceActivity = new Intent(getInstance(), RegisterService.class);
            startActivity(registerServiceActivity);
        });

        if (registerUsername.getText().length() == 0) {
            registerUsername.setText(getStreetsCommon().getDefaultUsername());
        }

		String chosenServiceMessage = null;
		if (RegisterTask.registrationServiceRequest.serviceType == null) {
			chosenServiceMessage = "You have chosen not to register any service";
		} else {
			switch (RegisterTask.registrationServiceRequest.serviceType) {
				case ESTABLISHED_BUSINESS: {
					chosenServiceMessage = "You have chosen to register an established business\n\n";
					break;
				}
				case SMALL_BUSINESS: {
					chosenServiceMessage = "You have chosen to register a small/medium business\n\n";
					break;
				}
				case OG_HUSTLE: {
					chosenServiceMessage = "You have chosen to register as an O.G. hustler\n\n";
					break;
				}
			}
		}
        textInfoRegister.setText(chosenServiceMessage);
	}

	@Override
	public void onResume() {
		Log.i(TAG, "+++ ON RESUME +++");
		super.onResume();
	}

	public static Register getInstance() { return register; }

	public void onClick(View view)
	{
        /* clear any outstanding error messages before starting validation */
        registerError.setText("");
        String errorMessage = "";

        if (!isValidUsername(registerUsername.getText().toString())) {
            errorMessage = "The username you typed is not valid! It must be between 2-50 characters long, start with a letter," +
                          " and contain only numbers, letters, or the following characters: - _ .";
            registerUsername.requestFocus();
        }
		else if (!isValidPin(registerPin.getText().toString())) {
            errorMessage = "The pin you typed is not valid! It must be 4 digits long!";
            registerPin.requestFocus();
        }
        else if (!registerPin.getText().toString().equals(registerConfirmPin.getText().toString()))
		{
            errorMessage = "The confirmation pin did not match the original pin!";
            registerConfirmPin.requestFocus();
        } else {
            /* all valid, going through */
            registerError.setText("");
            new RegisterTask(this).execute();
        }
        showSnackBar(this, TAG, errorMessage, Snackbar.LENGTH_SHORT);
        registerError.setText(errorMessage);
	}
}