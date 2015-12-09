package net.blaklizt.streets.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.R.id;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static net.blaklizt.streets.android.activity.helpers.RegisterTask.registrationServiceRequest;
import static net.blaklizt.streets.android.common.StreetsCommon.getTag;
import static net.blaklizt.streets.android.common.StreetsCommon.showSnackBar;
import static net.blaklizt.streets.android.common.enumeration.RegistrationServiceRequest.SERVICE_TYPE.ESTABLISHED_BUSINESS;
import static net.blaklizt.streets.android.common.enumeration.RegistrationServiceRequest.SERVICE_TYPE.OG_HUSTLE;
import static net.blaklizt.streets.android.common.enumeration.RegistrationServiceRequest.SERVICE_TYPE.SMALL_BUSINESS;
import static net.blaklizt.symbiosis.sym_core_lib.utilities.Validator.isValidProviderServiceName;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 7:50 AM
 */
public class RegisterService extends AppCompatActivity
{
	private static final String TAG = getTag(RegisterService.class);

	private static RegisterService registerService;
    private ImageView imgRegisterServiceBack;
    private ImageView imgRegisterServiceForward;
    private RadioGroup registerServiceRadioGroup;
    private RadioButton registerCompanyBusiness;
    private RadioButton registerSmallBusiness;
    private RadioButton registerOGHustle;
    private RadioButton registerNone;
    private EditText registerServiceName;
    private Button btnStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE +++");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_service_layout);
        registerService = this;

        imgRegisterServiceBack = (ImageView) findViewById(id.imgRegisterServiceBack);
        imgRegisterServiceForward = (ImageView) findViewById(id.imgRegisterServiceForward);
        registerServiceRadioGroup = (RadioGroup) findViewById(id.registerServiceRadioGroup);
        registerCompanyBusiness = (RadioButton) findViewById(id.registerCompanyBusiness);
        registerSmallBusiness = (RadioButton) findViewById(id.registerSmallBusiness);
        registerOGHustle = (RadioButton) findViewById(id.registerOGHustle);
        registerNone = (RadioButton) findViewById(id.registerNone);
        registerServiceName = (EditText) findViewById(id.registerServiceName);
        btnStart = (Button) findViewById(id.btnStart);

        imgRegisterServiceBack.setOnClickListener(v -> finish());
        imgRegisterServiceForward.setOnClickListener(v1 -> {
            if (registerNone.isChecked() || registerServiceRadioGroup.getCheckedRadioButtonId() != -1 &&
	            isValidProviderServiceName(registrationServiceRequest.serivceName)) {
                Intent registerProfileActivity = new Intent(getInstance(), Register.class);
                startActivity(registerProfileActivity);
            }
        });

        registerCompanyBusiness.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) { registrationServiceRequest.serviceType = ESTABLISHED_BUSINESS; } });
        registerSmallBusiness.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) { registrationServiceRequest.serviceType = SMALL_BUSINESS; } });
        registerOGHustle.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) { registrationServiceRequest.serviceType = OG_HUSTLE; } });
        registerNone.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) { registrationServiceRequest.serviceType = null; } });

        btnStart.setOnClickListener(v -> {
            if (registerServiceRadioGroup.getCheckedRadioButtonId() == -1) {
                showSnackBar(this, TAG, "You must select your service type (or choose \"None\") before proceeding", LENGTH_SHORT);
            } else if (registerNone.isChecked()) {
                Intent registerProfileActivity = new Intent(getInstance(), Register.class);
                startActivity(registerProfileActivity);
            } else if (!isValidProviderServiceName(registerServiceName.getText().toString())) {
                showSnackBar(this, TAG, "The service name you typed is not valid! It must be between 2-50 characters long, " +
                                        " and contain only numbers, letters, or the following characters: - _ .", LENGTH_SHORT);
            } else {

                if (registerOGHustle.isChecked()) { AppContext.getStreetsCommon().setDefaultUsername(registerServiceName.getText().toString()); }

                registrationServiceRequest.serivceName = registerServiceName.getText().toString();
                Intent registerProfileActivity = new Intent(getInstance(), Register.class);
                startActivity(registerProfileActivity);
            }
        });
	}

	public static RegisterService getInstance() { return registerService; }
}