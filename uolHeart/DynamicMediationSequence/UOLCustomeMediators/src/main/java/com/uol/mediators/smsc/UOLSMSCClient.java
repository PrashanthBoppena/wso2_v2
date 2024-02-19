package com.uol.mediators.smsc;

import java.io.IOException;

import java.util.Date;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmResult;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UOLSMSCClient extends AbstractMediator{

	private static  final String SMSC_USER="UOLTEST";
	private static final String SMSC_PWD="uol@test";
	private static final String SMSC_HOST="10.10.92.115";
	private static final String SMSC_SOURCE_ADDR="UOL-OM";
	
	@Override
	public boolean mediate(MessageContext msgContext) {
		String msisdn="+9609111112";
		String message="UOL testing smsc node";
		try {
			String resObj = sendSMS(msisdn, message);
			System.out.println("SMSC response is=====================>"+resObj);
			return true;
		} catch (JSONException e) {

			e.printStackTrace();
			return false;
		}

	}

	private static String sendSMS(String msisdn,String message) throws JSONException 
	{
		JSONObject obj=new JSONObject();
		SMPPSession session = new SMPPSession();
		try {
			System.out.println("connecting.....................");
			String systemId = session.connectAndBind("smscsim.smpp.org", 2775, 
					new BindParameter(BindType.BIND_TX, 
							SMSC_USER, SMSC_PWD, "", 
							TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));

			System.out.println("SMSC system ID is "+systemId);
			String conMessage="Intiating connection...";
			try {
				SubmitSmResult submitSmResult = session.submitShortMessage("",
						TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, SMSC_SOURCE_ADDR,
						TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, msisdn,
						new ESMClass(), (byte)0, (byte)1,  null, null,
						new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), 
						(byte)0, 
						new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte)0,
						message.getBytes());
				String messageId = submitSmResult.getMessageId();
				obj.put("status", "success");
				obj.put("messageId", messageId);
				System.out.println("Message successfully submitted : message_id="+ messageId);

			} catch (PDUException e) {
				// Invalid PDU parameter
				System.out.println("Invalid PDU parameter"+e);
				conMessage=e.getMessage();
			} catch (ResponseTimeoutException e) {
				// Response timeout
				conMessage=e.getMessage();
				System.out.println("Response timeout"+ e);
			} catch (InvalidResponseException e) {
				// Invalid response
				conMessage=e.getMessage();
				System.out.println("Receive invalid response"+e);
			} catch (NegativeResponseException e) {
				conMessage=e.getMessage();
				// Receiving negative response (non-zero command_status)
				System.out.println("Receive negative response"+e);
			} catch (IOException e) {
				conMessage=e.getMessage();
				System.out.println("IO error occured"+e);
			}

			session.unbindAndClose();

		} catch (IOException e) {
			System.out.println("Failed connect and bind to SMSC"+e);
			obj.put("status", "FAILED");
			obj.put("message", "Failed connect and bind to SMSC");
		}
		return obj.toString();
	}


	public static void main(String[] args) 
	{
		String msisdn="+9609111112";
		String message="UOL testing smsc node";
		String resObj=null;
		try {
			resObj = sendSMS(msisdn, message);
			System.out.println("SMSC response:=====================>"+resObj);
		} catch (JSONException e) {

			e.printStackTrace();
			System.out.println("SMSC client:exception called=====================>"+e);

		}


	}
}

