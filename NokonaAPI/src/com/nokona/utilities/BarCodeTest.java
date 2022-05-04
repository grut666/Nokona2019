package com.nokona.utilities;

import java.util.ArrayList;
import java.util.List;

import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;

public class BarCodeTest {
	public static void main(String[] args) throws NullInputDataException {
		
		TicketHeader th = new TicketHeader();
		th.setJobId("W-200M-LH");
		th.setKey(50796);
		th.setDescription("Stuff");
		th.setQuantity(10);
		TicketDetail td1 = new TicketDetail();
		td1.setOpCode("111");
		td1.setSequenceOriginal(1);
		td1.setSequenceUpdated(1);
		td1.setHourlyRateSAH(0);
		td1.setStandardQuantity(10);
		td1.setOperationDescription("xxxxx yyy zzz");
		TicketDetail td2 = new TicketDetail();
		td2.setOpCode("E200");
		td2.setSequenceOriginal(2);
		td2.setSequenceUpdated(2);
		td2.setHourlyRateSAH(.1142);
		td2.setStandardQuantity(10);
		td2.setOperationDescription("xxxxx yyy zzz");
		TicketDetail td3 = new TicketDetail();
		td3.setOpCode("B21A");
		td3.setSequenceOriginal(3);
		td3.setSequenceUpdated(3);
		td3.setHourlyRateSAH(.0033);
		td3.setStandardQuantity(10);
		td3.setOperationDescription("xxxxx yyy zzz");
		TicketDetail td4 = new TicketDetail();
		td4.setOpCode("C206");
		td4.setSequenceOriginal(4);
		td4.setSequenceUpdated(4);
		td4.setHourlyRateSAH(.0126);
		td4.setStandardQuantity(10);
		td4.setOperationDescription("xxxxx yyy zzz");

		List<TicketDetail> tdList = new ArrayList<>();
		tdList.add(td1);
		tdList.add(td2);
		tdList.add(td3);
		tdList.add(td4);
		Ticket ticket = new Ticket(th, tdList);
		BarCodeUtilities.generateTicketLabels(ticket);
//		String s0 = BarCodeUtilities.convertBarCode2of5(BarCodeUtilities.formatBarCode("05079701"));
//		String s1 = BarCodeUtilities.convertBarCode2of5(BarCodeUtilities.formatBarCode("05079702"));
//		String s2 = BarCodeUtilities.convertBarCode2of5(BarCodeUtilities.formatBarCode("05079703"));
//		for(int i = 0; i < s0.length(); i++) {
//			System.out.print((int)s0.charAt(i) + " ");
//		}
//		System.out.println();
//		for(int i = 0; i < s1.length(); i++) {
//			System.out.print((int)s1.charAt(i) + " ");
//		}
//		System.out.println();
//		for(int i = 0; i < s2.length(); i++) {
//			System.out.print((int)s2.charAt(i) + " ");
//		}
//		System.out.println();
	}



}
