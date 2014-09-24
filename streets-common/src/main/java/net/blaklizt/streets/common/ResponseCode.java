package net.blaklizt.streets.common;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 11:32 PM
 */

import java.util.HashMap;
import java.util.Map;

public enum ResponseCode
{
	/*
	 * For response codes < 0 display "General Error" as they are internal only
	 * 0  - 30 General response code
	 * 31 - 50 Account status
	 */

	GENERAL_ERROR
	{
		public int getValue() {return -1;}
		public String getDescription() {return "A system error occurred";}
	},

	SUCCESS
	{
		public int getValue() {return 0;}
		public String getDescription() {return "Success";}
	},

	INVALID_SESSION
	{
		public int getValue() {return 2;}
		public String getDescription() {return "Session Expired";}
	},

	AUTHENTICATION_FAILED
	{
		public int getValue() {return 3;}
		public String getDescription() {return "Authentication failed";}
	},

	INVALID_MSISDN
	{
		public int getValue() {return 7;}
		public String getDescription() {return "Invalid mobile number";}
	},

	INVALID_EMAIL
	{
		public int getValue() {return 8;}
		public String getDescription() {return "Email provided was not valid";}
	},

	INVALID_USERNAME
	{
		public int getValue() {return 9;}
		public String getDescription() {return "Username provided was not valid";}
	},

	INCOMPLETE_REQUEST
	{
		public int getValue() {return 10;}
		public String getDescription() {return "Element missing in request";}
	},

	UNKNOWN_TRANSACTION
	{
		public int getValue() {return 12;}
		public String getDescription() {return "Requested transaction unknown";}
	},

	TIMEOUT
	{
		public int getValue() {return 20;}
		public String getDescription() {return "Timeout elapsed before transaction completion";}
	},


	/*
	 * Account Status [31 - 50]
	 */
	ACTIVE
	{
		public int getValue() {return 31;}
		public String getDescription() {return "Account is active";}
	},
	INACTIVE
	{
		public int getValue() {return 32;}
		public String getDescription() {return "Account is inactive";}
	},
	SUSPENDED
	{
		public int getValue() {return 33;}
		public String getDescription() {return "Account has been suspended";}
	},
	CLOSED
	{
		public int getValue() {return 34;}
		public String getDescription() {return "Account has been closed";}
	},
	PASSWORD_TRIES_EXCEEDED
	{
		public int getValue() {return 35;}
		public String getDescription() {return "Password tries exceeded";}
	},
	NOT_REGISTERED
	{
		public int getValue() {return 36;}
		public String getDescription() {return "User not registered";}
	},

	/* Registration Code [61 - 70]
	              	 */
	REGISTRATION_FAILED
	{
		public int getValue() {return 61;}
		public String getDescription() {return "Registration Failed";}
	},
	PREVIOUS_MSISDN_FOUND
	{
		public int getValue() {return 62;}
		public String getDescription() {return "Phone number has been previously registered";}
	},
	PREVIOUS_EMAIL_FOUND
	{
		public int getValue() {return 63;}
		public String getDescription() {return "Email has been previously registered";}
	},
	PREVIOUS_REGISTRATION_FOUND
	{
		public int getValue() {return 64;}
		public String getDescription() {return "Previous registration found.";}
	},
	;

	public abstract int getValue();

	public abstract String getDescription();

	static Map<Integer, ResponseCode> enumMap;

	public static ResponseCode valueOf(int value)
	{
		if(enumMap == null)
		{
			enumMap = new HashMap<Integer, ResponseCode>();
			for(ResponseCode rc : ResponseCode.values())
			{
				enumMap.put(rc.getValue(), rc);
			}
		}
		return enumMap.get(value);
	}
}